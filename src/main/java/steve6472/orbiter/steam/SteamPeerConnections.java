package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import org.lwjgl.BufferUtils;
import steve6472.core.log.Log;
import steve6472.orbiter.network.PeerConnections;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class SteamPeerConnections extends PeerConnections<SteamPeer>
{
    private static final Logger LOGGER = Log.getLogger(SteamPeerConnections.class);
    private final SteamNetworking networking;

    public SteamPeerConnections(SteamMain steamMain)
    {
        super(steamMain);
        this.networking = steamMain.steamNetworking;
    }

    @Override
    protected boolean sendPacket(SteamPeer peer, ByteBuffer packetBuffer) throws Exception
    {
        return networking.sendP2PPacket(peer.steamID(), packetBuffer, SteamNetworking.P2PSend.Reliable, 0);
    }

    @Override
    protected boolean readPackets() throws Exception
    {
        int[] messageSize = new int[1];

        while (networking.isP2PPacketAvailable(0, messageSize))
        {
            SteamID remoteID = new SteamID();
            ByteBuffer buffer = BufferUtils.createByteBuffer(messageSize[0]);
            int i = networking.readP2PPacket(remoteID, buffer, 0);

            if (i != messageSize[0])
                LOGGER.severe("Packet size mismatch");

            if (verifySender(new SteamPeer(remoteID)))
            {
                packetManager.handlePacket(buffer, listener(), remoteID);
            }
        }
        return true;
    }
}
