package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record AcceptedPeerConnection(boolean VR) implements Packet<AcceptedPeerConnection, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("accepted_peer_connection");
    public static final BufferCodec<ByteBuf, AcceptedPeerConnection> BUFFER_CODEC = BufferCodec.of(BufferCodecs.BOOL, AcceptedPeerConnection::VR, AcceptedPeerConnection::new);

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
        listener.acceptedPeerConnection(VR);
    }
}
