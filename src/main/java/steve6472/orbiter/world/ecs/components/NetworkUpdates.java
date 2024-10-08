package steve6472.orbiter.world.ecs.components;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public sealed class NetworkUpdates permits NetworkAdd, NetworkRemove
{
    private final Set<Class<?>> components = new HashSet<>();

    public Set<Class<?>> components()
    {
        return components;
    }

    public void add(Class<?> component)
    {
        components.add(component);
    }

    public void remove(Class<?> component)
    {
        components.remove(component);
    }

    public void clear()
    {
        components.clear();
    }

    public Predicate<Class<?>> test()
    {
        return components::contains;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[" + "components=" + components + ']';
    }
}
