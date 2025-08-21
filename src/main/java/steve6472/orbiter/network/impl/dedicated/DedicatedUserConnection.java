package steve6472.orbiter.network.impl.dedicated;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class DedicatedUserConnection
{
    DedicatedMain network;
    private final SocketAddress peerAddress;

    public DedicatedUserConnection(DedicatedMain network, String peerHost, int peerPort)
    {
        this.network = network;
        // Initialize peer address
        peerAddress = new InetSocketAddress(peerHost, peerPort);
    }

    public DedicatedUserConnection(DedicatedMain network, SocketAddress address)
    {
        this.network = network;
        peerAddress = address;
    }

    public void sendPacket(ByteBuffer buffer)
    {
        try
        {
            ((DedicatedLobby) network.lobby()).getChannel().send(buffer, peerAddress);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public SocketAddress getPeerAddress()
    {
        return peerAddress;
    }
}