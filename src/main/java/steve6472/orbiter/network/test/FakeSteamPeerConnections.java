package steve6472.orbiter.network.test;

import com.codedisaster.steamworks.SteamID;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.network.PeerConnections;
import steve6472.orbiter.network.packets.game.Heartbeat;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.SteamPeer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class FakeSteamPeerConnections extends PeerConnections<SteamPeer>
{
    P2PNode node;

    public FakeSteamPeerConnections(SteamMain steamMain)
    {
        super(steamMain);

        try
        {
            node = new P2PNode("localhost",
                OrbiterMain.FAKE_PEER ? FakeP2PConstants.SERVER_PORT : FakeP2PConstants.PEER_PORT,
                !OrbiterMain.FAKE_PEER ? FakeP2PConstants.SERVER_PORT : FakeP2PConstants.PEER_PORT);

            SteamID sender = OrbiterMain.FAKE_PEER ? FakeP2PConstants.USER_ID : FakeP2PConstants.FAKE_PEER;
            addPeer(new SteamPeer(sender));

            node.readPacket = (bytes) ->
            {
                if (verifySender(new SteamPeer(sender)))
                {
                    packetManager.handleRawPacket(bytes, listener(), sender);
                }
            };

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean sendPacket(SteamPeer peer, ByteBuffer packetBuffer) throws Exception
    {
        node.sendPacket(packetBuffer);
        return true;
    }

    @Override
    protected boolean readPackets() throws Exception
    {
        for (int i = 0; i < 4; i++)
        {
            node.listen();
        }
        return true;
    }
}
