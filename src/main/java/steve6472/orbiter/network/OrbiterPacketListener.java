package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import steve6472.core.network.PacketListener;
import steve6472.orbiter.steam.SteamMain;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public abstract class OrbiterPacketListener implements PacketListener
{
    protected final SteamMain steamMain;

    public OrbiterPacketListener(SteamMain steamMain)
    {
        this.steamMain = steamMain;
    }

    protected SteamID sender()
    {
        return steamMain.packetManager.lastSender();
    }
}
