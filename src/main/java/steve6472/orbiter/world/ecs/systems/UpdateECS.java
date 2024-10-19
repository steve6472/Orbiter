package steve6472.orbiter.world.ecs.systems;

import com.jme3.bullet.objects.PhysicsRigidBody;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import steve6472.core.log.Log;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.ModifyState;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;
import steve6472.orbiter.world.ecs.core.ComponentSystem;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdateECS implements ComponentSystem
{
    private static final Logger LOGGER = Log.getLogger(UpdateECS.class);

    @Override
    public void tick(Dominion dominion, World world)
    {
        var found = dominion.findEntitiesWith(UUID.class).withAlso(Tag.PHYSICS.getClass());

        for (var entityComps : found)
        {
            UUID uuid = entityComps.comp();
            Entity entity = entityComps.entity();

            PhysicsRigidBody body = world.bodyMap().get(uuid);

            if (body == null)
            {
                LOGGER.warning("Body does not exist for entity " + uuid);
                continue;
            }

            for (Class<? extends PhysicsProperty> physicsComponent : PhysicsProperty.PHYSICS_COMPONENTS)
            {
                PhysicsProperty physicsProperty = entity.get(physicsComponent);
                if (physicsProperty == null)
                    continue;

                ModifyState modified = physicsProperty.modifyComponent(body);

                if (modified.hasNewComponent())
                {
                    entity.removeType(physicsComponent);
                    entity.add(modified.getComponent());
                    markModified(entity, physicsComponent);
                } else if (modified.hasModifiedComponent())
                {
                    markModified(entityComps.entity(), physicsComponent);
                }
            }
        }
    }
}
