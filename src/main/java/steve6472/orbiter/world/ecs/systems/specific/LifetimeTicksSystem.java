package steve6472.orbiter.world.ecs.systems.specific;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.specific.LifetimeTicks;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class LifetimeTicksSystem extends IteratingProfiledSystem
{
    public LifetimeTicksSystem()
    {
        super(Family.all(LifetimeTicks.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        LifetimeTicks lifetimeTicks = Components.LIFETIME_TICKS.get(entity);
        lifetimeTicks.remainingTicksToLive--;

        if (lifetimeTicks.remainingTicksToLive <= 0)
        {
            Components.UUID.ifPresentOrElse(entity, uuid -> {
                OrbiterApp.getInstance().getClient().getWorld().removeEntity(uuid.uuid(), true);
            }, () -> {
                getEngine().removeEntity(entity);
            });
        }
    }
}
