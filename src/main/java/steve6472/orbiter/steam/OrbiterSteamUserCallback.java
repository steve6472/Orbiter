package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import steve6472.core.log.Log;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamUserCallback implements SteamUserCallback
{
    private static final Logger LOGGER = Log.getLogger(OrbiterSteamUserCallback.class);

    @Override
    public void onAuthSessionTicket(SteamAuthTicket authTicket, SteamResult result)
    {
        LOGGER.finest("onAuthSessionTicket");
    }

    @Override
    public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID ownerSteamID)
    {
        LOGGER.finest("onValidateAuthTicket");
    }

    @Override
    public void onMicroTxnAuthorization(int appID, long orderID, boolean authorized)
    {
        LOGGER.finest("onMicroTxnAuthorization");
    }

    @Override
    public void onEncryptedAppTicket(SteamResult result)
    {
        LOGGER.finest("onEncryptedAppTicket");
    }
}
