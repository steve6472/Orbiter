package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import steve6472.orbiter.network.api.Connections;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.packets.play.clientbound.CreateCustomEntity;
import steve6472.orbiter.network.packets.play.clientbound.RemoveEntity;
import steve6472.orbiter.network.packets.play.clientbound.CreateEntity;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
@SuppressWarnings("UnusedReturnValue")
public interface EntityControl
{
    PhysicsSpace physics();
    Engine ecsEngine();
    Map<UUID, PhysicsRigidBody> bodyMap();
    NetworkMain network();

    private Connections connections()
    {
        return network().connections();
    }

    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid, boolean broadcast)
    {
        List<Component> components = entityBlueprint.createEntityComponents(uuid);
        Entity entity = createEntity(components);

        // Special physics tag handling
        handlePhysics(entity, components);

        // Broadcast new entity to peers
        if (broadcast && connections() != null && network().lobby().isHost())
            connections().broadcastPacket(new CreateEntity(uuid, entityBlueprint.key()));

        return entity;
    }

    default Entity addCustomEntity(UUID uuid, Collection<Component> components, boolean broadcast)
    {
        components.add(new UUIDComp(uuid));
        Entity entity = createEntity(components);

        // Special physics tag handling
        handlePhysics(entity, components);

        // Broadcast new entity to peers
        if (broadcast && connections() != null && network().lobby().isHost())
        {
            CreateCustomEntity packet = new CreateCustomEntity(entity);
            connections().broadcastPacket(packet);
        }

        return entity;
    }

//    default Entity spawnDebugPlayer(User user, boolean VR)
//    {
//        ArrayList<Component> objects = new ArrayList<>();
//        Key key = Constants.key(VR ? "blockbench/static/vr_player" : "blockbench/static/player_capsule");
//        Model model = FlareRegistries.STATIC_MODEL.get(key);
//        Collision collision = new Collision(key);
//        UUID uuid = UUID.randomUUID();
//
//        objects.add(new IndexModel(model));
//        objects.add(new UUIDComp(uuid));
//        objects.add(new MPControlled(user));
//        objects.add(Tag.PHYSICS);
//        objects.add(new Position());
//        objects.add(new Gravity(0, 0, 0));
//        objects.add(collision);
//
//        if (VR)
//        {
//            objects.add(new Gravity(0, 0, 0));
//        }
//
//        Entity entity = createEntity(objects);
//
//        PhysicsRigidBody body = handlePhysics(entity, objects);
//        Objects.requireNonNull(body, "Player entity missing physics!");
//        body.setUserIndex(Constants.PLAYER_MAGIC_CONSTANT);
//        body.setAngularFactor(0f);
//
//        return entity;
//    }

    private void handlePhysics(Entity entity, Collection<Component> components)
    {
        if (!Components.TAG_PHYSICS.has(entity))
            return;

        UUID uuid = Components.UUID.get(entity).uuid();
        Objects.requireNonNull(uuid);

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
    }

    /// Sends the RemoveEntity packet
    default void removeEntity(UUID uuid, boolean broadcast)
    {
        PhysicsRigidBody body = bodyMap().get(uuid);
        if (body != null)
        {
            physics().remove(body);
            bodyMap().remove(uuid);
        }

        for (Entity entity : ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()))
        {
            if (Components.UUID.get(entity).uuid().equals(uuid))
            {
                ecsEngine().removeEntity(entity);
            }
        }

        if (broadcast && connections() != null && network().lobby().isHost())
            connections().broadcastPacket(new RemoveEntity(uuid));
    }

    default Optional<Entity> getEntityByUUID(UUID uuid)
    {
        return Stream.of(ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()).toArray(Entity.class)).filter(e -> Components.UUID.get(e).uuid().equals(uuid)).findAny();
    }

    private Entity createEntity(Collection<Component> components)
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
