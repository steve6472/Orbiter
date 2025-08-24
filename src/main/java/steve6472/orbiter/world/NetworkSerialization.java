package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class NetworkSerialization
{
    public static List<Component> selectComponents(Entity entity, Predicate<Class<? extends Component>> filter)
    {
        UUID uuid = Components.UUID.get(entity).uuid();
        if (uuid == null)
            throw new RuntimeException("Tried to network serialize an entity without UUID");

        ImmutableArray<Component> components = entity.getComponents();

        List<Component> selectedComponents = new ArrayList<>();
        for (Component component : components)
        {
            Class<? extends Component> componentClass = component.getClass();

            // Never changes
            if (componentClass.equals(UUIDComp.class))
                continue;

            if (!filter.test(componentClass))
                continue;

            selectedComponents.add(component);
        }
        return selectedComponents;
    }

    public static List<Component> selectComponents(Entity entity)
    {
        return selectComponents(entity, _ -> true);
    }
}
