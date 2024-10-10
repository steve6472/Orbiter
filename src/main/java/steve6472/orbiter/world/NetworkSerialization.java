package steve6472.orbiter.world;

import com.mojang.datafixers.util.Pair;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.engine.IntEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.core.Component;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class NetworkSerialization
{
    public static Pair<Integer, ByteBuf> entityComponentsToBuffer(Entity entity, Predicate<Class<?>> filter)
    {
        final int INITIAL_BYTES = 64;

        UUID uuid = entity.get(UUID.class);
        if (uuid == null)
            throw new RuntimeException("Tried to network serialize an entity without UUID");

        Object[] componentArray = ((IntEntity) entity).getComponentArray();

        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(INITIAL_BYTES + componentArray.length * 16);
        int encoded = 0;

        for (Object component : componentArray)
        {
            // Never changes
            Class<?> componentClass = component.getClass();
            if (componentClass.equals(UUID.class))
                continue;

            if (!filter.test(componentClass))
                continue;

            for (Component<?> componentType : Registries.COMPONENT.getMap().values())
            {
                //noinspection unchecked
                BufferCodec<ByteBuf, Object> networkCodec = (BufferCodec<ByteBuf, Object>) componentType.getNetworkCodec();

                if (networkCodec == null)
                    continue;

                if (componentType.componentClass().equals(componentClass))
                {
                    BufferCodecs.KEY.encode(buffer, componentType.key());
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
