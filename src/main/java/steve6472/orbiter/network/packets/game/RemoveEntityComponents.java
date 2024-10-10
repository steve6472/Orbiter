package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.ExtraBufferCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record RemoveEntityComponents(UUID uuid, String componentKeys) implements Packet<RemoveEntityComponents, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("remove_entity_components");
    public static final BufferCodec<ByteBuf, RemoveEntityComponents> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, RemoveEntityComponents::uuid,
        BufferCodecs.STRING, RemoveEntityComponents::componentKeys,
        RemoveEntityComponents::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, RemoveEntityComponents> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        String[] split = componentKeys.split(";");
        List<Key> components = new ArrayList<>(split.length);
        for (String s : split)
        {
            String[] split1 = s.split(":");
            components.add(Key.withNamespace(split1[0], split1[1]));
        }

        gameListener.removeEntityComponents(uuid, components);
    }
}
