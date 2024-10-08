package steve6472.orbiter.network.packets.lobby;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.test.PacketTest;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record SimpleTestMessage(String message) implements Packet<SimpleTestMessage, PacketTest.TestListener>
{
    public static final Key KEY = Key.defaultNamespace("simple_message");
    public static final BufferCodec<ByteBuf, SimpleTestMessage> BUFFER_CODEC = BufferCodec.of(BufferCodecs.STRING, SimpleTestMessage::message, SimpleTestMessage::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, SimpleTestMessage> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(PacketTest.TestListener listener)
    {
        listener.simpleMessage(message);
    }
}
