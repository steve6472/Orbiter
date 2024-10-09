package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import steve6472.core.log.Log;
import steve6472.orbiter.debug.Console;
import steve6472.orbiter.steam.lobby.Lobby;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamFriends implements SteamFriendsCallback
{
    private static final Logger LOGGER = Log.getLogger(OrbiterSteamFriends.class);
    public SteamMain steamMain;

    public OrbiterSteamFriends(SteamMain steamMain)
    {
        this.steamMain = steamMain;
    }

    @Override
    public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result)
    {
        LOGGER.finest("onSetPersonaNameResponse: %s, %s, %s".formatted(success, localSuccess, result));
    }

    @Override
    public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change)
    {
        LOGGER.finest("onPersonaStateChange: %s, %s".formatted(steamID, change));

        if (change == SteamFriends.PersonaChange.GamePlayed)
        {
            SteamFriends.FriendGameInfo friendGameInfo = new SteamFriends.FriendGameInfo();
            steamMain.steamFriends.getFriendGamePlayed(steamID, friendGameInfo);
            LOGGER.finest(steamMain.friendNames.getUserName(steamID) + " GamePlayed: " + friendGameInfo.getGameID() + ", " + friendGameInfo.getGameIP() + ", " + friendGameInfo.getSteamIDLobby() + ", " + friendGameInfo.getGamePort() + ", " + friendGameInfo.getQueryPort());
        }
        else if (change == SteamFriends.PersonaChange.Name)
        {
            String name = steamMain.steamFriends.getFriendPersonaName(steamID);
            LOGGER.info(name + " changed their name!");
            steamMain.friendNames.updateName(steamID, name);
        }
        else if (change == SteamFriends.PersonaChange.Nickname)
        {
            String name = steamMain.steamFriends.getFriendPersonaName(steamID);
            LOGGER.info(name + " changed their nickname!");
            steamMain.friendNames.updateName(steamID, name);
        }
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

        if (steamMain.lobbyManager.currentLobby() != null)
        {
            LOGGER.warning("Tried to join a different lobby, this is not supported yet!");
            return;
        }

        steamMain.lobbyManager.setCurrentLobby(new Lobby(steamIDLobby, steamMain));

        Console.log("Joining lobby " + steamIDLobby + ", requested by " + steamMain.friendNames.getUserName(steamIDFriend), new Color(0xFF056B42, true));
        steamMain.steamMatchmaking.joinLobby(steamIDLobby);
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
        LOGGER.info(steamMain.friendNames.getUserName(steamIDFriend) + " requested rich join via rich presence (" + connect + ")");
    }

    @Override
    public void onGameServerChangeRequested(String server, String password)
    {
        LOGGER.finest("onGameServerChangeRequested: %s, %s".formatted(server, password));
    }
}
