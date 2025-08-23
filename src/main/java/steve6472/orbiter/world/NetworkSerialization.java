package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.core.ComponentEntry;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class NetworkSerialization
{
    public static Pair<Integer, ByteBuf> entityComponentsToBuffer(Entity entity, Predicate<Class<? extends Component>> filter)
    {
        final int INITIAL_BYTES = 64;

        UUID uuid = Components.UUID.get(entity).uuid();
        if (uuid == null)
            throw new RuntimeException("Tried to network serialize an entity without UUID");

        ImmutableArray<Component> components = entity.getComponents();

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(INITIAL_BYTES + components.size() * 16);
        int encoded = 0;

        for (Component component : components)
        {
            Class<? extends Component> componentClass = component.getClass();

            // Never changes
            if (componentClass.equals(UUIDComp.class))
                continue;

            if (!filter.test(componentClass))
                continue;

            for (ComponentEntry<?> componentType : Registries.COMPONENT.getMap().values())
            {
                //noinspection unchecked
                BufferCodec<ByteBuf, Object> networkCodec = (BufferCodec<ByteBuf, Object>) componentType.getNetworkCodec();

                if (networkCodec == null)
                    continue;

                if (componentType.componentClass().equals(componentClass))
                {
                    BufferCodecs.INT.encode(buffer, componentType.networkID());
                    networkCodec.encode(buffer, component);
                    encoded++;
                }
            }
        }

        return Pair.of(encoded, buffer);
    }

    public static Pair<Integer, ByteBuf> entityComponentsToBuffer(Entity entity)
    {
        return entityComponentsToBuffer(entity, _ -> true);
    }
}
