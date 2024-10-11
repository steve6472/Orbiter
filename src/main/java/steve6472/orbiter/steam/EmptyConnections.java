package steve6472.orbiter.steam;

import steve6472.orbiter.network.PeerConnections;

import java.nio.ByteBuffer;

/**
 * Created by steve6472
 * Date: 10/12/2024
 * Project: Orbiter <br>
 */
public class EmptyConnections extends PeerConnections<SteamPeer>
{
    public EmptyConnections(SteamMain steamMain)
    {
        super(steamMain);
    }

    @Override
    protected boolean sendPacket(SteamPeer peer, ByteBuffer packetBuffer)
    {
        return true;
    }

    @Override
    protected boolean readPackets() throws Exception
    {
        return false;
    }
}
