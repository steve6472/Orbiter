package steve6472.orbiter.world.ecs.components.specific;

import com.badlogic.ashley.core.Component;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class SeedBag implements Component
{
    public int seedCount;

    public SeedBag()
    {
        this.seedCount = 4;
    }
}
