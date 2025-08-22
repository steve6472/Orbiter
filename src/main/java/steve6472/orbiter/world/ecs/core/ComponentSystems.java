package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.EntitySystem;
import steve6472.orbiter.util.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ComponentSystems<T extends EntitySystem>
{
    public final List<ComponentSystemEntry<T>> systemEntries;
    public Consumer<T> systemRunFunction;
    public Profiler profiler = new Profiler(15);

    public ComponentSystems(Consumer<T> systemRunFunction)
    {
        systemEntries = new ArrayList<>();
        this.systemRunFunction = systemRunFunction;
    }

    public void run()
    {
        runSystems();
    }

    private void runSystems()
    {
        if (systemRunFunction == null)
            return;

        profiler.start();
        for (ComponentSystemEntry<T> entry : systemEntries)
        {
            if (!entry.enabled)
                continue;

            entry.profiler.start();
            try
            {
                systemRunFunction.accept(entry.system);
            } catch (Exception exception)
            {
                exception.printStackTrace();
            }
            entry.profiler.end();
        }
        profiler.end();
    }

    public void registerSystem(T system, String name)
    {
        registerSystem(system, name, "");
    }

    public void registerSystem(T system, String name, String description)
    {
        registerSystem(system, name, description, true, false);
    }

    public void registerDebugSystem(T system, String name, String description)
    {
        registerSystem(system, name, description, true, true);
    }

    public void registerDebugSystem(T system, String name, String description, boolean enabled)
    {
        registerSystem(system, name, description, enabled, true);
    }
    
    public void registerSystem(T system, String name, String description, boolean enabled)
    {
        registerSystem(system, name, description, enabled, false);
    }

    public void registerSystem(T system, String name, String description, boolean enabled, boolean debug)
    {
        ComponentSystemEntry<T> entry = new ComponentSystemEntry<>();
        entry.system = system;
        entry.id = name.toLowerCase(Locale.ROOT).replace(' ', '_');
        entry.enabled = enabled;
        entry.debug = debug;
        entry.name = name;
        entry.description = description;
        systemEntries.add(entry);
    }
}
