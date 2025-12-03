package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class LifetimeTicks implements Component
{
    public long remainingTicksToLive;

    public LifetimeTicks(long remainingTicksToLive)
    {
        this.remainingTicksToLive = remainingTicksToLive;
    }
}
