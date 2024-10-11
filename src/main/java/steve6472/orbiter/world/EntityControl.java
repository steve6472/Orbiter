package steve6472.orbiter.world;

import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.api.Results;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.network.PeerConnections;
import steve6472.orbiter.network.packets.game.CreateEntity;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.volkaniums.assets.model.Model;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.util.*;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
public interface EntityControl
{
    PhysicsSpace physics();
    Dominion ecs();
    Map<UUID, PhysicsRigidBody> bodyMap();
    PeerConnections<?> connections();

    default Entity addEntity(Model model, UUID uuid, Object... extraComponents)
    {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new IndexModel(model));
        objects.add(uuid);

        Collections.addAll(objects, extraComponents);

        Entity entity = ecs().createEntity(objects.toArray());

        // Broadcast new entity to peers
        if (connections() != null)
            connections().broadcastMessage(new CreateEntity(entity));

        return entity;
    }

    // TODO: possibly a packet for this instead of generic create entity, could save bandwidth
    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid)
    {
        Set<Object> components = entityBlueprint.createComponents();
        components.add(uuid);

        Entity entity = ecs().createEntity(components.toArray());

        // Special physics tag handling
        if (entity.has(Tag.Physics.class))
        {
            Collision collision = entity.get(Collision.class);
            if (collision == null)
                throw new RuntimeException("Entity blueprint needs Collision if Physics tag is specified!");

            PhysicsRigidBody body = new PhysicsRigidBody(collision.shape());
            bodyMap().put(uuid, body);
            physics().add(body);

            for (Object component : components)
            {
                if (component instanceof PhysicsProperty pp)
                {
                    pp.modifyBody(body);
                }
            }
        }

        // Broadcast new entity to peers
        if (connections() != null)
            connections().broadcastMessage(new CreateEntity(entity));

        return entity;
    }

    default Entity addEntity(EntityBlueprint entityBlueprint)
    {
        return addEntity(entityBlueprint, UUID.randomUUID());
    }

    default Entity addEntity(Model model, Object... extraComponents)
    {
        return addEntity(model, UUID.randomUUID(), extraComponents);
    }

    default Entity spawnDebugPlayer(SteamID steamID)
    {
        ArrayList<Object> objects = new ArrayList<>();
        Model model = VolkaniumsRegistries.STATIC_MODEL.get(Key.defaultNamespace("blockbench/static/player_capsule"));
        Collision collision = new Collision(Key.defaultNamespace("blockbench/static/player_capsule"));
        UUID uuid = UUID.randomUUID();

        objects.add(new IndexModel(model));
        objects.add(uuid);
        objects.add(new MPControlled(steamID));
        objects.add(Tag.PHYSICS);
        objects.add(new Position());
        objects.add(collision);

        Entity entity = ecs().createEntity(objects.toArray());

        CollisionShape shape = collision.shape();
//        if (!(shape instanceof ConvexShape convexShape))
//        {
//            throw new RuntimeException("Player character collision needs to be convex shape!");
//        }

        PhysicsRigidBody body = new PhysicsRigidBody(collision.shape());
        body.setUserIndex(Constants.PLAYER_MAGIC_CONSTANT);
        body.setAngularFactor(0f);
//        PhysicsCharacter body = new PhysicsCharacter(convexShape, 0);
        body.setUserObject(uuid);
        physics().add(body);
        bodyMap().put(uuid, body);

//        if (objects.stream().anyMatch(o -> o instanceof MPControlled))
//        {
//            body.setGravity(Convert.jomlToPhys(new Vector3f(0, 0, 0)));
//        }

        return entity;
    }

    default void removeEntity(UUID uuid)
    {
        PhysicsRigidBody body = bodyMap().get(uuid);
        if (body != null)
        {
            physics().remove(body);
        }
        ecs().findEntitiesWith(UUID.class).stream().filter(e -> e.comp().equals(uuid)).forEach(e -> ecs().deleteEntity(e.entity()));
    }

    default Optional<Entity> getEntityByUUID(UUID uuid)
    {
        return ecs().findEntitiesWith(UUID.class).stream().filter(e -> e.comp().equals(uuid)).findAny().map(Results.With1::entity);
    }
}
