package steve6472.orbiter.network.packets.game.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.game.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 8/22/2025
 * Project: Orbiter <br>
 */
public final class Disconnect implements Packet<Disconnect, GameHostboundListener>
{
    private static final Disconnect INSTANCE = new Disconnect();
    public static final Key KEY = Constants.key("game/hb/disconnect");
    public static final BufferCodec<ByteBuf, Disconnect> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private Disconnect() {}

    public static Disconnect instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, Disconnect> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameHostboundListener listener)
    {
        listener.disconnect();
    }
}
