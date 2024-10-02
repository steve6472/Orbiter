package steve6472.orbiter.world.ecs.core;

import steve6472.orbiter.util.Profiler;

public class SystemEntry<T>
{
    public T system;
    public boolean enabled;
    public boolean debug;

    public String id;
    public String name;
    public String description;

    public Profiler profiler = new Profiler(15);
}
