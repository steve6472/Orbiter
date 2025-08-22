package steve6472.orbiter.network.packets.game;

import com.badlogic.ashley.core.Entity;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.Components;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record RemoveEntity(UUID uuid) implements Packet<RemoveEntity, GameListener>
{
    public static final Key KEY = Constants.key("remove_entity");
    public static final BufferCodec<ByteBuf, RemoveEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, RemoveEntity::uuid,
        RemoveEntity::new);

    public RemoveEntity(Entity entity)
    {
        this(Components.UUID.get(entity).uuid());
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
