package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import steve6472.core.log.Log;
import steve6472.orbiter.network.packets.game.GameListener;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamNetworking implements SteamNetworkingCallback
{
    private static final Logger LOGGER = Log.getLogger(OrbiterSteamNetworking.class);
    private final SteamMain steamMain;

    public OrbiterSteamNetworking(SteamMain steamMain)
    {
        this.steamMain = steamMain;
    }

    @Override
    public void onP2PSessionConnectFail(SteamID steamIDRemote, SteamNetworking.P2PSessionError sessionError)
    {
        LOGGER.warning("Connect Fail from " + steamIDRemote.getAccountID() + " error: " + sessionError);
    }

    @Override
    public void onP2PSessionRequest(SteamID steamIDRemote)
    {
        LOGGER.info("Session request from " + steamIDRemote.getAccountID() + " (" + steamMain.steamFriends.getFriendPersonaName(steamIDRemote) + ")");
        boolean b = steamMain.steamNetworking.acceptP2PSessionWithUser(steamIDRemote);
        LOGGER.fine("Accept result: " + b);
        steamMain.peer = steamIDRemote;
        steamMain.orbiterApp.getWorld().spawnDebugPlayer(steamIDRemote);
        LOGGER.info("Peer set!");
    }
}
