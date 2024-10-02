package steve6472.orbiter.world.ecs.systems;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Results;
import org.joml.Vector3f;
import steve6472.orbiter.Convert;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.ECSystem;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdateECSPositions implements ECSystem
{
    @Override
    public void tick(Dominion dominion, World world)
    {
        var found = dominion.findEntitiesWith(UUID.class, Position.class).withAlso(Tag.PHYSICS.getClass());

        final Vector3f store = new Vector3f();

        for (var entityComps : found)
        {
            UUID uuid = entityComps.comp1();
            Position position = entityComps.comp2();

            PhysicsRigidBody body = world.bodyMap.get(uuid);

            if (body == null)
                throw new RuntimeException("Body does not exist for entity " + uuid);

            Convert.physGetToJoml(body::getPhysicsLocation, store);

            position.set(store.x, store.y, store.z);
            position.resetModified();
        }
    }
}
