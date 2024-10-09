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
public class AcceptedPeerConnection implements Packet<AcceptedPeerConnection, GameListener>
{
    private static final AcceptedPeerConnection INSTANCE = new AcceptedPeerConnection();
    public static final Key KEY = Key.defaultNamespace("accepted_peer_connection");
    public static final BufferCodec<ByteBuf, AcceptedPeerConnection> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private AcceptedPeerConnection() {}

    public static AcceptedPeerConnection instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, AcceptedPeerConnection> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        listener.acceptedPeerConnection();
    }
}
