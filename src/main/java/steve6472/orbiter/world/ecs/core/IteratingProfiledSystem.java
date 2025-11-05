package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import steve6472.core.util.Profiler;
import steve6472.orbiter.tracy.IProfiler;
import steve6472.orbiter.tracy.OrbiterProfiler;

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
        IProfiler profiler = OrbiterProfiler.world();
        profiler.push(name());
        super.update(deltaTime);
        profiler.pop();
    }

    public String name()
    {
        return getClass().getSimpleName();
    }
}
