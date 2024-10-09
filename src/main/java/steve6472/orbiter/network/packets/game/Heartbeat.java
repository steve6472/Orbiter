package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class Heartbeat implements Packet<Heartbeat, GameListener>
{
    private static final Heartbeat INSTANCE = new Heartbeat();
    public static final Key KEY = Key.defaultNamespace("heartbeat");
    public static final BufferCodec<ByteBuf, Heartbeat> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private Heartbeat() {}

    public static Heartbeat instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, Heartbeat> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener) {}
}
