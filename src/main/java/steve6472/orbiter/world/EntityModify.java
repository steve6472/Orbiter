package steve6472.orbiter.world;

import dev.dominion.ecs.api.Entity;
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
    default void markModified(Entity entity, Class<?> type)
    {
        NetworkUpdates networkUpdates = entity.get(NetworkUpdates.class);
        if (networkUpdates == null)
        {
            networkUpdates = new NetworkUpdates();
            entity.add(networkUpdates);
        }

        networkUpdates.add(type);
    }

    default <T> void modifyComponent(Entity entity, T component, Consumer<T> update)
    {
        update.accept(component);
        markModified(entity, component.getClass());
    }

    default <T> void addComponent(Entity entity, T component)
    {
        entity.add(component);
        NetworkAdd networkUpdates = entity.get(NetworkAdd.class);
        if (networkUpdates == null)
        {
            networkUpdates = new NetworkAdd();
            entity.add(networkUpdates);
        }

        networkUpdates.add(component.getClass());
    }

    default <T> void removeComponent(Entity entity, Class<T> component)
    {
        if (entity.removeType(component))
        {
            NetworkRemove networkUpdates = entity.get(NetworkRemove.class);
            if (networkUpdates == null)
            {
                networkUpdates = new NetworkRemove();
                entity.add(networkUpdates);
            }

            networkUpdates.add(component);
        }
    }

    default <T> void removeComponent(Entity entity, T component)
    {
        if (entity.remove(component))
        {
            NetworkRemove networkUpdates = entity.get(NetworkRemove.class);
            if (networkUpdates == null)
            {
                networkUpdates = new NetworkRemove();
                entity.add(networkUpdates);
            }

            networkUpdates.add(component.getClass());
        }
    }
}
