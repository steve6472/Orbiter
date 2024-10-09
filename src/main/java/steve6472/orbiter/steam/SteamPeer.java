package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamID;
import steve6472.orbiter.network.Peer;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public final class SteamPeer implements Peer
{
    private final SteamID steamID;

    public SteamPeer(SteamID steamID)
    {
        this.steamID = steamID;
    }

    public SteamID steamID()
    {
        return steamID;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        var that = (SteamPeer) obj;
        return Objects.equals(this.steamID, that.steamID);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(steamID);
    }

    @Override
    public String toString()
    {
        return "SteamPeer[" + "steamID=" + steamID + ']';
    }
}
