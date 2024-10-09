package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import steve6472.core.network.PacketListener;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.SteamPeer;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public abstract class OrbiterPacketListener implements PacketListener
{
    protected final SteamMain steamMain;
    protected final PeerConnections<SteamPeer> connections;

    public OrbiterPacketListener(SteamMain steamMain)
    {
        this.steamMain = steamMain;
        this.connections = steamMain.connections;
    }

    protected SteamID sender()
    {
        return steamMain.packetManager.lastSender();
    }

    protected SteamPeer peer()
    {
        return new SteamPeer(sender());
    }
}
