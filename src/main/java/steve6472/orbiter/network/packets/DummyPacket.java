package steve6472.orbiter.network.packets;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.packets.game.GameListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class DummyPacket implements Packet<DummyPacket, OrbiterPacketListener>
{
    private static final DummyPacket INSTANCE = new DummyPacket();
    public static final Key KEY = Key.defaultNamespace("dummy");
    public static final BufferCodec<ByteBuf, DummyPacket> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private DummyPacket() {}

    public static DummyPacket instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, DummyPacket> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(OrbiterPacketListener listener)
    {
        System.out.println("Dummy!");
    }
}
