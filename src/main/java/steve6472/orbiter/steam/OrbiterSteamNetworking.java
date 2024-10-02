package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamNetworking implements SteamNetworkingCallback
{
    @Override
    public void onP2PSessionConnectFail(SteamID steamIDRemote, SteamNetworking.P2PSessionError sessionError)
    {

    }

    @Override
    public void onP2PSessionRequest(SteamID steamIDRemote)
    {

    }
}
