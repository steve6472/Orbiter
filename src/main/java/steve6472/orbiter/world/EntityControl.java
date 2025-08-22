package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.api.Connections;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.packets.game.CreateEntity;
import steve6472.orbiter.network.packets.game.RemoveEntity;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.flare.assets.model.Model;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
public interface EntityControl
{
    PhysicsSpace physics();
    Engine ecsEngine();
    Map<UUID, PhysicsRigidBody> bodyMap();
    Connections connections();

    default Entity addEntity(Model model, UUID uuid, Component... extraComponents)
    {
        ArrayList<Component> objects = new ArrayList<>();
        objects.add(new IndexModel(model));
        objects.add(new UUIDComp(uuid));

        Collections.addAll(objects, extraComponents);

        Entity entity = createEntity(objects);

        // Special physics tag handling
        handlePhysics(entity, objects);

        // Broadcast new entity to peers
        if (connections() != null)
            connections().broadcastPacket(new CreateEntity(entity));

        return entity;
    }

    // TODO: possibly a packet for this instead of generic create entity, could save bandwidth
    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid)
    {
        List<Component> components = entityBlueprint.createComponents();
        components.add(new UUIDComp(uuid));

        Entity entity = createEntity(components);

        // Special physics tag handling
        handlePhysics(entity, components);

        // Broadcast new entity to peers
        if (connections() != null)
            connections().broadcastPacket(new CreateEntity(entity));

        return entity;
    }

    default Entity addEntity(EntityBlueprint entityBlueprint)
    {
        return addEntity(entityBlueprint, UUID.randomUUID());
    }

    default Entity addEntity(Model model, Component... extraComponents)
    {
        return addEntity(model, UUID.randomUUID(), extraComponents);
    }

    /// Does not call CreateEntity
    default Entity addEntity(Component... components)
    {
        Entity entity = createEntity(components);

        // Special physics tag handling
        handlePhysics(entity, Set.of(components));

        return entity;
    }

    default Entity spawnDebugPlayer(User user, boolean VR)
    {
        ArrayList<Component> objects = new ArrayList<>();
        Key key = Constants.key(VR ? "blockbench/static/vr_player" : "blockbench/static/player_capsule");
        Model model = FlareRegistries.STATIC_MODEL.get(key);
        Collision collision = new Collision(key);
        UUID uuid = UUID.randomUUID();

        objects.add(new IndexModel(model));
        objects.add(new UUIDComp(uuid));
        objects.add(new MPControlled(user));
        objects.add(Tag.PHYSICS);
        objects.add(new Position());
        objects.add(new Gravity(0, 0, 0));
        objects.add(collision);

        if (VR)
        {
            objects.add(new Gravity(0, 0, 0));
        }

        Entity entity = createEntity(objects);

        PhysicsRigidBody body = handlePhysics(entity, objects);
        Objects.requireNonNull(body, "Player entity missing physics!");
        body.setUserIndex(Constants.PLAYER_MAGIC_CONSTANT);
        body.setAngularFactor(0f);

        return entity;
    }

    private PhysicsRigidBody handlePhysics(Entity entity, Collection<Component> components)
    {
        UUID uuid = Components.UUID.get(entity).uuid();
        Objects.requireNonNull(uuid);

        if (Components.TAG_PHYSICS.has(entity))
        {
            Collision collision_ = Components.COLLISION.get(entity);
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
        Stream.of(ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()).toArray()).filter(e -> Components.UUID.get(e).uuid().equals(uuid)).forEach(e -> ecsEngine().removeEntity(e));

        if (connections() != null)
            connections().broadcastPacket(new RemoveEntity(uuid));
    }

    default Optional<Entity> getEntityByUUID(UUID uuid)
    {
        return Stream.of(ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()).toArray()).filter(e -> Components.UUID.get(e).uuid().equals(uuid)).findAny();
    }

    default Entity createEntity(Component... components)
    {
        Entity entity = ecsEngine().createEntity();
        for (Component o : components)
        {
            entity.add(o);
        }
        ecsEngine().addEntity(entity);
        return entity;
    }

    default Entity createEntity(Collection<Component> components)
    {
        Entity entity = ecsEngine().createEntity();
        for (Component component : components)
        {
            entity.add(component);
        }
        ecsEngine().addEntity(entity);
        return entity;
    }
}
