package steve6472.orbiter.network.packets.game.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.game.GameClientboundListener;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record CreateEntity(UUID uuid, Key entityType) implements Packet<CreateEntity, GameClientboundListener>
{
    public static final Key KEY = Constants.key("game/cb/create_entity");
    public static final BufferCodec<ByteBuf, CreateEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, CreateEntity::uuid,
        BufferCodecs.KEY, CreateEntity::entityType,
        CreateEntity::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, CreateEntity> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener gameListener)
    {
        gameListener.createEntity(uuid, entityType);
    }
}
