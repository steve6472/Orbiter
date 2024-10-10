package steve6472.orbiter.world;

import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.api.Results;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.orbiter.Convert;
import steve6472.orbiter.commands.arguments.EntityBlueprintArgument;
import steve6472.orbiter.network.PeerConnections;
import steve6472.orbiter.network.packets.game.CreateEntity;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
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

    default Entity addPhysicsEntity(PhysicsRigidBody body, Model model, Object... extraComponents)
    {
        return addPhysicsEntity(body, model, UUID.randomUUID(), extraComponents);
    }

    default Entity addPhysicsEntity(PhysicsRigidBody body, Model model, UUID uuid, Object... extraComponents)
    {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new IndexModel(model));
        objects.add(uuid);

        objects.add(Tag.PHYSICS);
        objects.add(new Position());

        Collections.addAll(objects, extraComponents);
        Entity entity = ecs().createEntity(objects.toArray());
        body.setUserObject(uuid);
        physics().add(body);
        bodyMap().put(uuid, body);

        if (objects.stream().anyMatch(o -> o instanceof MPControlled))
        {
            body.setGravity(Convert.jomlToPhys(new Vector3f(0, 0, 0)));
        }

        // Broadcast new entity to peers
        // TODO: physics entities
//        connections().broadcastMessage(new CreateEntity(entity));

        return entity;
    }

    default Entity addEntity(Model model, UUID uuid, Object... extraComponents)
    {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new IndexModel(model));
        objects.add(uuid);

        Collections.addAll(objects, extraComponents);

        Entity entity = ecs().createEntity(objects.toArray());

        // Broadcast new entity to peers
        connections().broadcastMessage(new CreateEntity(entity));

        return entity;
    }

    // TODO: possibly a packet for this instead of generic create entity, could save bandwidth
    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid)
    {
        Set<Object> components = entityBlueprint.createComponents();
        components.add(uuid);

        Entity entity = ecs().createEntity(components.toArray());

        // Broadcast new entity to peers
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
        PhysicsRigidBody body = new PhysicsRigidBody(new CapsuleCollisionShape(PCPlayer.RADIUS, PCPlayer.HEIGHT / 2f));
        body.setAngularFactor(Convert.jomlToPhys(new Vector3f(0, 1, 0)));
        return addPhysicsEntity(body, VolkaniumsRegistries.STATIC_MODEL.get(Key.defaultNamespace("blockbench/static/player_capsule")), new MPControlled(steamID));
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
