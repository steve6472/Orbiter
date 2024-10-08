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
public class LobbyClosing implements Packet<LobbyClosing, LobbyListener>
{
    private static final LobbyClosing INSTANCE = new LobbyClosing();
    public static final Key KEY = Key.defaultNamespace("lobby_closing");
    public static final BufferCodec<ByteBuf, LobbyClosing> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private LobbyClosing() {}

    public static LobbyClosing instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, LobbyClosing> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LobbyListener listener)
    {
        listener.lobbyClosing();
    }
}
