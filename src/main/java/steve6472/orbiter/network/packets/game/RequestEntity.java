package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record RequestEntity(UUID entity) implements Packet<RequestEntity, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("request_entity");
    public static final BufferCodec<ByteBuf, RequestEntity> BUFFER_CODEC = BufferCodec.of(BufferCodecs.UUID, RequestEntity::entity, RequestEntity::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, RequestEntity> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        listener.entityRequested(entity);
    }
}
