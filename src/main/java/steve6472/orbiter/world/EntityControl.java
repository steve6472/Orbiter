package steve6472.orbiter.world;

import com.codedisaster.steamworks.SteamID;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.orbiter.Convert;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.volkaniums.assets.model.Model;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

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

        return entity;
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
        ecs().findEntitiesWith(UUID.class).forEach(e -> ecs().deleteEntity(e.entity()));
    }
}
