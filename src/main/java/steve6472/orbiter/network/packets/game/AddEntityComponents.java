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
public record AddEntityComponents(UUID uuid, int componentCount, ByteBuf buffer) implements Packet<AddEntityComponents, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("add_entity_components");
    public static final BufferCodec<ByteBuf, AddEntityComponents> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, AddEntityComponents::uuid,
        BufferCodecs.INT, AddEntityComponents::componentCount,
        ExtraBufferCodecs.BUFFER, AddEntityComponents::buffer,
        AddEntityComponents::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, AddEntityComponents> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        List<Object> components = new ArrayList<>(componentCount);
        for (int i = 0; i < componentCount; i++)
        {
            Key componentKey = BufferCodecs.KEY.decode(buffer);
            Object component = Registries.COMPONENT.get(componentKey).getNetworkCodec().decode(buffer);
            components.add(component);
        }

        buffer.release();

        gameListener.addEntityComponents(uuid, components);
    }
}
