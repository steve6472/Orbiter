package steve6472.orbiter.network.packets.game;

import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import io.netty.buffer.ByteBuf;
import steve6472.orbiter.network.ExtraBufferCodecs;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record TeleportToPosition(Vector3f destination) implements Packet<TeleportToPosition, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("teleport");
    public static final BufferCodec<ByteBuf, TeleportToPosition> BUFFER_CODEC = BufferCodec.of(ExtraBufferCodecs.VEC3F, TeleportToPosition::destination, TeleportToPosition::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, TeleportToPosition> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        listener.teleport(destination);
    }
}
