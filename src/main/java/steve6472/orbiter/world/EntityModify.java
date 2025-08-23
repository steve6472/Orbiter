package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.NetworkAdd;
import steve6472.orbiter.world.ecs.components.NetworkRemove;
import steve6472.orbiter.world.ecs.components.NetworkUpdates;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public interface EntityModify
{
    static void _markModified(Entity entity, Class<? extends Component> type)
    {
        NetworkUpdates networkUpdates = Components.NETWORK_UPDATES.get(entity);
        if (networkUpdates == null)
        {
            networkUpdates = new NetworkUpdates();
            entity.add(networkUpdates);
        }

        networkUpdates.add(type);
    }

    default void markModified(Entity entity, Class<? extends Component> type)
    {
        _markModified(entity, type);
    }

    default <T extends Component> void modifyComponent(Entity entity, T component, Consumer<T> update)
    {
        update.accept(component);
        markModified(entity, component.getClass());
    }

    default <T extends Component> void addComponent(Entity entity, T component)
    {
        entity.add(component);
        NetworkAdd networkUpdates = Components.NETWORK_ADD.get(entity);
        if (networkUpdates == null)
        {
            networkUpdates = new NetworkAdd();
            entity.add(networkUpdates);
        }

        networkUpdates.add(component.getClass());
    }

    default <T extends Component> void removeComponent(Entity entity, Class<T> component)
    {
        if (entity.remove(component) != null)
        {
            NetworkRemove networkUpdates = Components.NETWORK_REMOVE.get(entity);
            if (networkUpdates == null)
            {
                networkUpdates = new NetworkRemove();
                entity.add(networkUpdates);
            }

            networkUpdates.add(component);
        }
    }

    default <T extends Component> void removeComponent(Entity entity, T component)
    {
        removeComponent(entity, component.getClass());
    }
}
