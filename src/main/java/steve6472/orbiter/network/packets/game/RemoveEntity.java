package steve6472.orbiter.network.packets.game;

import dev.dominion.ecs.api.Entity;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record RemoveEntity(UUID uuid) implements Packet<RemoveEntity, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("remove_entity");
    public static final BufferCodec<ByteBuf, RemoveEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, RemoveEntity::uuid,
        RemoveEntity::new);

    public RemoveEntity(Entity entity)
    {
        this(entity.get(UUID.class));
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, RemoveEntity> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        gameListener.removeEntity(uuid);
    }
}
