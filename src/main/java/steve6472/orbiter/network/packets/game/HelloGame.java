package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.packets.lobby.LobbyListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class HelloGame implements Packet<HelloGame, GameListener>
{
    private static final HelloGame INSTANCE = new HelloGame();
    public static final Key KEY = Key.defaultNamespace("hello_game");
    public static final BufferCodec<ByteBuf, HelloGame> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private HelloGame() {}

    public static HelloGame instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, HelloGame> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        System.out.println("Hello Game!");
//        listener.helloWorld();
    }
}