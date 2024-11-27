package steve6472.orbiter.network.packets.game;

import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.mojang.datafixers.util.Pair;
import dev.dominion.ecs.api.Entity;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.vr.VrData;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;
import steve6472.orbiter.world.ecs.core.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class GameListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(GameListener.class);

    private final World world;

    public GameListener(SteamMain steamMain, World world)
    {
        super(steamMain);
        this.world = world;
    }

    public void teleport(Vector3f destination)
    {
        var entityList = world.ecs().findEntitiesWith(MPControlled.class, UUID.class, Tag.Physics.class);

        for (var entityData : entityList)
        {
            MPControlled mpControlled = entityData.comp1();
            if (!mpControlled.controller().equals(sender()))
                continue;

            UUID uuid = entityData.comp2();

            world.bodyMap().get(uuid).setPhysicsLocation(Convert.jomlToPhys(destination));
        }
    }

    public void acceptedPeerConnection(boolean VR)
    {
        if (!OrbiterMain.FAKE_P2P)
            LOGGER.info(steamMain.friendNames.getUserName(sender()) + " accepted peer connection!");
        else
            LOGGER.info("Fake P2P accepted");
        sendExistingData();
        world.spawnDebugPlayer(sender(), VR);
    }

    private void sendExistingData()
    {
        LOGGER.info("Sending existing data!");
        connections.sendMessage(peer(), new SpawnPlayerCharacter(steamMain.userID, VrData.VR_ON));

        world.ecs().findEntitiesWith(UUID.class).forEach(ent -> {
//            LOGGER.info("Sending " + System.currentTimeMillis() + " " + ent);
            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(ent.entity());
            connections.sendMessage(peer(), new CreateEntity(ent.comp(), serialized.getFirst(), serialized.getSecond()));
        });
    }

    public void spawnPlayer(SteamID player, boolean VR)
    {
        LOGGER.info("Spawn player " + player + " VR: " + VR);
        world.spawnDebugPlayer(player, VR);
    }

    public void disconnectPlayer(SteamID disconnectedPlayer)
    {
        world
            .ecs()
            .findEntitiesWith(MPControlled.class, UUID.class)
            .stream()
            .filter(e -> e.comp1().controller().equals(disconnectedPlayer))
            .forEach(e -> world.removeEntity(e.comp2()));
    }

    public void updateEntity(UUID uuid, List<Object> components)
    {
//        LOGGER.info("Update components " + uuid + " " + components);
        world.getEntityByUUID(uuid).ifPresentOrElse(entity -> {
            for (Object component : components)
            {
                entity.removeType(component.getClass());
                entity.add(component);

                if (component instanceof PhysicsProperty pp)
                {
                    PhysicsRigidBody body = world.bodyMap().get(uuid);
                    if (body == null)
                    {
                        LOGGER.warning("Body does not exist for entity " + uuid);
                        continue;
                    }
                    pp.modifyBody(body);
                }
            }
        }, () ->
        {
//            LOGGER.info("Requesting entity (update) " + System.currentTimeMillis() + " " + uuid);
            connections.sendMessage(peer(), new RequestEntity(uuid));
        });
    }

    public void addEntityComponents(UUID uuid, List<Object> components)
    {
        world.getEntityByUUID(uuid).ifPresentOrElse(entity -> {
            for (Object component : components)
            {
                entity.add(component);
            }
        }, () ->
        {
//            LOGGER.info("Requesting entity (add) " + System.currentTimeMillis() + " " + uuid);
            connections.sendMessage(peer(), new RequestEntity(uuid));
        });
    }

    public void removeEntityComponents(UUID uuid, List<Key> components)
    {
        world.getEntityByUUID(uuid).ifPresent(entity -> {
            for (Key component : components)
            {
                Component<?> cmpnnt = Registries.COMPONENT.get(component);
                entity.removeType(cmpnnt.componentClass());
            }
        });
    }

    public void entityRequested(UUID entity)
    {
        LOGGER.info("Requested entity " + System.currentTimeMillis() + " " + entity);
        Optional<Entity> entityByUUID = world.getEntityByUUID(entity);
        entityByUUID.ifPresent(e -> {
            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(e);
            connections.sendMessage(peer(), new CreateEntity(entity, serialized.getFirst(), serialized.getSecond()));
        });
    }

    public void createEntity(UUID uuid, List<Object> components)
    {
        LOGGER.info("Create entity " + uuid + " " + components);
        if (world.getEntityByUUID(uuid).isEmpty())
        {
            Entity entity = world.addEntity(components.toArray());
//            if (entity.has(Tag.Physics.class))
//            {
//                Collision collision = entity.get(Collision.class);
//                if (collision == null)
//                {
//                    LOGGER.severe("Physics entity has no collision!");
//                    return;
//                }
//
//                PhysicsRigidBody body = new PhysicsRigidBody(collision.shape());
//                world.bodyMap().put(uuid, body);
//                world.physics().add(body);
//
//                var position = entity.get(Position.class);
//                if (position != null)
//                    body.setPhysicsLocation(Convert.jomlToPhys(position.toVec3f()));
//            }
        }
    }

    public void removeEntity(UUID uuid)
    {
        LOGGER.info("Remove entity " + uuid);
        world.getEntityByUUID(uuid).ifPresent(e -> world.ecs().deleteEntity(e));
        PhysicsRigidBody body = world.bodyMap().get(uuid);
        if (body != null)
        {
            world.bodyMap().remove(uuid);
            world.physics().remove(body);
        }
    }

    public void clearJoints(UUID uuid)
    {
        PhysicsRigidBody body = world.bodyMap().get(uuid);
        if (body != null)
        {
            for (PhysicsJoint physicsJoint : body.listJoints())
            {
                world.physics().removeJoint(physicsJoint);
                body.removeJoint(physicsJoint);
            }
        } else
        {
            LOGGER.warning("No body for " + uuid + " can not clear joints!");
        }

        connections.broadcastMessageExclude(new ClearJoints(uuid), peer());
    }

    public void addJoints(AddJoint addJoint)
    {
        PhysicsRigidBody bodyA = world.bodyMap().get(addJoint.bodyA());
        PhysicsRigidBody bodyB = world.bodyMap().get(addJoint.bodyB());

        PhysicsJoint joint = new SixDofSpringJoint(
            bodyA,
            bodyB,
            Convert.jomlToPhys(addJoint.pivotA()),
            Convert.jomlToPhys(addJoint.pivotB()),
            Convert.jomlToPhys(addJoint.rotA(), new Matrix3f()),
            Convert.jomlToPhys(addJoint.rotB(), new Matrix3f()),
            false);
        world.physics().addJoint(joint);
        connections.broadcastMessageExclude(addJoint, peer());
    }
}
