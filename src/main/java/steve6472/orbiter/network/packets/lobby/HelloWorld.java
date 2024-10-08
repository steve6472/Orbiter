package steve6472.orbiter.network.packets.lobby;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class HelloWorld implements Packet<HelloWorld, LobbyListener>
{
    private static final HelloWorld INSTANCE = new HelloWorld();
    public static final Key KEY = Key.defaultNamespace("hello_world");
    public static final BufferCodec<ByteBuf, HelloWorld> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private HelloWorld() {}

    public static HelloWorld instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, HelloWorld> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LobbyListener listener)
    {
        listener.helloWorld();
    }
}
