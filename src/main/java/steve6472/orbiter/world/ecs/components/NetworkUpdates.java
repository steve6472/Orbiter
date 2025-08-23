package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public sealed class NetworkUpdates implements Component permits NetworkAdd, NetworkRemove
{
    private final Set<Class<? extends Component>> components = new HashSet<>();

    public Set<Class<? extends Component>> components()
    {
        return components;
    }

    public void add(Class<? extends Component> component)
    {
        components.add(component);
    }

    public void clear()
    {
        components.clear();
    }

    public Predicate<Class<? extends Component>> test()
    {
        return components::contains;
    }

    @Override
    public String toString()
    {
        return "%s[components=%s]".formatted(getClass().getSimpleName(), components.stream().map(Class::getSimpleName).toList());
    }
}
