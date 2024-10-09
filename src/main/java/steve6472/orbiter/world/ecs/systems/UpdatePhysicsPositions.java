package steve6472.orbiter.world.ecs.systems;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import steve6472.orbiter.Convert;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.ComponentSystem;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdatePhysicsPositions implements ComponentSystem
{
    @Override
    public void tick(Dominion dominion, World world)
    {
        var found = dominion.findEntitiesWith(UUID.class, Position.class).withAlso(Tag.PHYSICS.getClass());

        for (var entityComps : found)
        {
            UUID uuid = entityComps.comp1();
            Position position = entityComps.comp2();

            PhysicsRigidBody body = world.bodyMap.get(uuid);

            if (body == null)
                throw new RuntimeException("Body does not exist for entity " + uuid);

            if (position.modified())
            {
                position.resetModified();
                body.setPhysicsLocation(Convert.jomlToPhys(position.toVec3f()));
            }
        }
    }
}
