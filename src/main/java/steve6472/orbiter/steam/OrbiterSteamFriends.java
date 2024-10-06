package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import steve6472.core.log.Log;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamFriends implements SteamFriendsCallback
{
    private static final Logger LOGGER = Log.getLogger(OrbiterSteamFriends.class);

    @Override
    public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result)
    {
        LOGGER.finest("onSetPersonaNameResponse: %s, %s, %s".formatted(success, localSuccess, result));
    }

    @Override
    public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change)
    {
        LOGGER.finest("onPersonaStateChange: %s, %s".formatted(steamID, change));
    }

    @Override
    public void onGameOverlayActivated(boolean active)
    {
        LOGGER.finest("onGameOverlayActivated: %s".formatted(active));
    }

    @Override
    public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend)
    {
        LOGGER.finest("onGameLobbyJoinRequested: %s, %s".formatted(steamIDLobby, steamIDFriend));
    }

    @Override
    public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height)
    {
        LOGGER.finest("onAvatarImageLoaded: %s, %s, %s, %s".formatted(steamID, image, width, height));
    }

    @Override
    public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID)
    {
        LOGGER.finest("onFriendRichPresenceUpdate: %s, %s".formatted(steamIDFriend, appID));
    }

    @Override
    public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect)
    {
        LOGGER.finest("onGameRichPresenceJoinRequested: %s, %s".formatted(steamIDFriend, connect));
    }

    @Override
    public void onGameServerChangeRequested(String server, String password)
    {
        LOGGER.finest("onGameServerChangeRequested: %s, %s".formatted(server, password));
    }
}
