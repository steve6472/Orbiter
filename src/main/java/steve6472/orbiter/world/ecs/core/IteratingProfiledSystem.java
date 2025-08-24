package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import steve6472.core.util.Profiler;

/**
 * Created by steve6472
 * Date: 8/24/2025
 * Project: Orbiter <br>
 */
public abstract class IteratingProfiledSystem extends IteratingSystem implements ProfiledSystem
{
    private final Profiler profiler = new Profiler(60);

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
        profiler.start();
        super.update(deltaTime);
        profiler.end();
    }

    @Override
    public Profiler profiler()
    {
        return profiler;
    }
}
