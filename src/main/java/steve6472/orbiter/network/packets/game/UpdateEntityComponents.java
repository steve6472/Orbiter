package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
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
public record UpdateEntityComponents(UUID uuid, int componentsToAdd, int componentsToRemove, ByteBuf addBuffer, ByteBuf removeBuffer) implements Packet<UpdateEntityComponents, GameListener>
{
    public static final Key KEY = Constants.key("update_entity_components");
    public static final BufferCodec<ByteBuf, UpdateEntityComponents> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, UpdateEntityComponents::uuid,
        BufferCodecs.INT, UpdateEntityComponents::componentsToAdd,
        BufferCodecs.INT, UpdateEntityComponents::componentsToRemove,
        ExtraBufferCodecs.BUFFER, UpdateEntityComponents::addBuffer,
        ExtraBufferCodecs.BUFFER, UpdateEntityComponents::removeBuffer,
        UpdateEntityComponents::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, UpdateEntityComponents> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
//        List<Object> components = new ArrayList<>(componentCount);
//        for (int i = 0; i < componentCount; i++)
//        {
//            Key componentKey = BufferCodecs.KEY.decode(buffer);
//            Object component = Registries.COMPONENT.get(componentKey).getNetworkCodec().decode(buffer);
//            components.add(component);
//        }
//
//        buffer.release();
//
//        gameListener.updateEntity(uuid, components);
    }
}
