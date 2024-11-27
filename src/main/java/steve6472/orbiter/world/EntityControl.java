package steve6472.orbiter.world;

import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.api.Results;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.PeerConnections;
import steve6472.orbiter.network.packets.game.CreateEntity;
import steve6472.orbiter.network.packets.game.RemoveEntity;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.flare.assets.model.Model;

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

        // Special physics tag handling
        handlePhysics(entity, objects);

        // Broadcast new entity to peers
        if (connections() != null)
            connections().broadcastMessage(new CreateEntity(entity));

        return entity;
    }

    // TODO: possibly a packet for this instead of generic create entity, could save bandwidth
    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid)
    {
        List<Object> components = entityBlueprint.createComponents();
        components.add(uuid);

        Entity entity = ecs().createEntity(components.toArray());

        // Special physics tag handling
        handlePhysics(entity, components);

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

    /// Does not call CreateEntity
    default Entity addEntity(Object... components)
    {
        Entity entity = ecs().createEntity(components);

        // Special physics tag handling
        handlePhysics(entity, Set.of(components));

        return entity;
    }

    default Entity spawnDebugPlayer(SteamID steamID, boolean VR)
    {
        ArrayList<Object> objects = new ArrayList<>();
        Key key = Key.defaultNamespace(VR ? "blockbench/static/vr_player" : "blockbench/static/player_capsule");
        Model model = FlareRegistries.STATIC_MODEL.get(key);
        Collision collision = new Collision(key);
        UUID uuid = UUID.randomUUID();

        objects.add(new IndexModel(model));
        objects.add(uuid);
        objects.add(new MPControlled(steamID));
        objects.add(Tag.PHYSICS);
        objects.add(new Position());
        objects.add(new Gravity(0, 0, 0));
        objects.add(collision);

        if (VR)
        {
            objects.add(new Gravity(0, 0, 0));
        }

        Entity entity = ecs().createEntity(objects.toArray());

        PhysicsRigidBody body = handlePhysics(entity, objects);
        Objects.requireNonNull(body, "Player entity missing physics!");
        body.setUserIndex(Constants.PLAYER_MAGIC_CONSTANT);
        body.setAngularFactor(0f);

        return entity;
    }

    private PhysicsRigidBody handlePhysics(Entity entity, Collection<Object> components)
    {
        UUID uuid = entity.get(UUID.class);
        Objects.requireNonNull(uuid);

        if (entity.has(Tag.Physics.class))
        {
            Collision collision_ = entity.get(Collision.class);
            if (collision_ == null)
                throw new RuntimeException("Entity blueprint needs Collision if Physics tag is specified!");

            PhysicsRigidBody body = new PhysicsRigidBody(collision_.shape());
            body.setUserObject(uuid);
            bodyMap().put(uuid, body);
            physics().add(body);

            for (Object component : components)
            {
                if (component instanceof PhysicsProperty pp)
                {
                    pp.modifyBody(body);
                }
            }

            return body;
        }

        return null;
    }

    /// Sends the RemoveEntity packet
    default void removeEntity(UUID uuid)
    {
        PhysicsRigidBody body = bodyMap().get(uuid);
        if (body != null)
        {
            physics().remove(body);
            bodyMap().remove(uuid);
        }
        ecs().findEntitiesWith(UUID.class).stream().filter(e -> e.comp().equals(uuid)).forEach(e -> ecs().deleteEntity(e.entity()));

        if (connections() != null)
            connections().broadcastMessage(new RemoveEntity(uuid));
    }

    default Optional<Entity> getEntityByUUID(UUID uuid)
    {
        return ecs().findEntitiesWith(UUID.class).stream().filter(e -> e.comp().equals(uuid)).findAny().map(Results.With1::entity);
    }
}
