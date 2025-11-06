package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;

/**
 * Created by steve6472
 * Date: 8/24/2025
 * Project: Orbiter <br>
 */
public abstract class IteratingProfiledSystem extends IteratingSystem
{
    public IteratingProfiledSystem(Family family)
    {
        super(family);
    }

    public IteratingProfiledSystem(Family family, int priority)
    {
        super(family, priority);
    }

    @Override
    public void update(float deltaTime)
    {
        Profiler profiler = FlareProfiler.world();
        profiler.push(name());
        super.update(deltaTime);
        profiler.pop();
    }

    public String name()
    {
        return getClass().getSimpleName();
    }
}
