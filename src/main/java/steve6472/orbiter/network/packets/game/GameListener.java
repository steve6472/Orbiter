package steve6472.orbiter.network.packets.game;

import com.badlogic.ashley.core.Entity;
import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;
import steve6472.orbiter.world.ecs.core.ComponentEntry;

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

    private final Client client;

    public GameListener(Client client)
    {
        this.client = client;
    }

    public void teleport(Vector3f destination)
    {/*
        var entityList = client.getWorld().ecs().findEntitiesWith(MPControlled.class, UUID.class, Tag.Physics.class);

        for (var entityData : entityList)
        {
            MPControlled mpControlled = entityData.comp1();
            if (!mpControlled.controller().equals(sender()))
                continue;

            UUID uuid = entityData.comp2();

            client.getWorld().bodyMap().get(uuid).setPhysicsLocation(Convert.jomlToPhys(position));
        }*/
    }

    public void acceptedPeerConnection(boolean VR)
    {
        LOGGER.info(sender() + " accepted peer connection!");
        sendExistingData();
//        client.getWorld().spawnDebugPlayer(sender(), VR);
    }

    private void sendExistingData()
    {
        LOGGER.info("Sending existing data!");
//        connections().sendPacket(sender(), new SpawnPlayerCharacter(steamMain.userID, VrData.VR_ON));
/*
        client.getWorld().ecs().findEntitiesWith(UUID.class).forEach(ent -> {
//            LOGGER.info("Sending " + System.currentTimeMillis() + " " + ent);
            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(ent.entity());
            connections().sendPacket(sender(), new CreateEntity(ent.comp(), serialized.getFirst(), serialized.getSecond()));
        });*/
    }

    public void spawnPlayer(User player, boolean VR)
    {
        LOGGER.info("Spawn player " + player + " VR: " + VR);
//        client.getWorld().spawnDebugPlayer(player, VR);
    }

    public void disconnectPlayer(SteamID disconnectedPlayer)
    {/*
        client.getWorld()
            .ecs()
            .findEntitiesWith(MPControlled.class, UUID.class)
            .stream()
            .filter(e -> e.comp1().controller().equals(disconnectedPlayer))
            .forEach(e -> client.getWorld().removeEntity(e.comp2()));*/
    }

    public void updateEntity(UUID uuid, List<Object> components)
    {
//        LOGGER.info("Update components " + uuid + " " + components);
        /*client.getWorld().getEntityByUUID(uuid).ifPresentOrElse(entity -> {
            for (Object component : components)
            {
                entity.removeType(component.getClass());
                entity.add(component);

                if (component instanceof PhysicsProperty pp)
                {
                    PhysicsRigidBody body = client.getWorld().bodyMap().get(uuid);
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
            connections().sendPacket(sender(), new RequestEntity(uuid));
        });*/
    }

    public void addEntityComponents(UUID uuid, List<Object> components)
    {/*
        client.getWorld().getEntityByUUID(uuid).ifPresentOrElse(entity -> {
            for (Object component : components)
            {
                entity.add(component);
            }
        }, () ->
        {
//            LOGGER.info("Requesting entity (add) " + System.currentTimeMillis() + " " + uuid);
            connections().sendPacket(sender(), new RequestEntity(uuid));
        });*/
    }

    public void removeEntityComponents(UUID uuid, List<Key> components)
    {/*
        client.getWorld().getEntityByUUID(uuid).ifPresent(entity -> {
            for (Key component : components)
            {
                ComponentEntry<?> cmpnnt = Registries.COMPONENT.get(component);
                entity.removeType(cmpnnt.componentClass());
            }
        });*/
    }

    public void entityRequested(UUID entity)
    {/*
        LOGGER.info("Requested entity " + System.currentTimeMillis() + " " + entity);
        Optional<Entity> entityByUUID = client.getWorld().getEntityByUUID(entity);
        entityByUUID.ifPresent(e -> {
            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(e);
            connections().sendPacket(sender(), new CreateEntity(entity, serialized.getFirst(), serialized.getSecond()));
        });*/
    }

    public void createEntity(UUID uuid, List<Object> components)
    {/*
        LOGGER.info("Create entity " + uuid + " " + components);
        if (client.getWorld().getEntityByUUID(uuid).isEmpty())
        {
            Entity entity = client.getWorld().addEntity(components.toArray());
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
        }*/
    }

    public void removeEntity(UUID uuid)
    {/*
        LOGGER.info("Remove entity " + uuid);
        client.getWorld().getEntityByUUID(uuid).ifPresent(e -> client.getWorld().ecs().deleteEntity(e));
        PhysicsRigidBody body = client.getWorld().bodyMap().get(uuid);
        if (body != null)
        {
            client.getWorld().bodyMap().remove(uuid);
            client.getWorld().physics().remove(body);
        }*/
    }

    public void clearJoints(UUID uuid)
    {
        LOGGER.warning("clearJoints method partially disabled");
        PhysicsRigidBody body = client.getWorld().bodyMap().get(uuid);
        if (body != null)
        {
            for (PhysicsJoint physicsJoint : body.listJoints())
            {
                client.getWorld().physics().removeJoint(physicsJoint);
                body.removeJoint(physicsJoint);
            }
        } else
        {
            LOGGER.warning("No body for " + uuid + " can not clear joints!");
        }

//        connections.broadcastMessageExclude(new ClearJoints(uuid), peer());
    }

    public void addJoints(AddJoint addJoint)
    {
        LOGGER.warning("addJoints method partially disabled");
        PhysicsRigidBody bodyA = client.getWorld().bodyMap().get(addJoint.bodyA());
        PhysicsRigidBody bodyB = client.getWorld().bodyMap().get(addJoint.bodyB());

        PhysicsJoint joint = new SixDofSpringJoint(
            bodyA,
            bodyB,
            Convert.jomlToPhys(addJoint.pivotA()),
            Convert.jomlToPhys(addJoint.pivotB()),
            Convert.jomlToPhys(addJoint.rotA(), new Matrix3f()),
            Convert.jomlToPhys(addJoint.rotB(), new Matrix3f()),
            false);
        client.getWorld().physics().addJoint(joint);
//        connections.broadcastMessageExclude(addJoint, peer());
    }
}
