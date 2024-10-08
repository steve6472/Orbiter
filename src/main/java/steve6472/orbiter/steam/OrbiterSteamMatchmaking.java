package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import steve6472.core.log.Log;
import steve6472.orbiter.debug.Console;
import steve6472.orbiter.network.packets.lobby.LobbyListener;
import steve6472.orbiter.steam.lobby.Lobby;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class OrbiterSteamMatchmaking implements SteamMatchmakingCallback
{
    private static final Logger LOGGER = Log.getLogger(OrbiterSteamMatchmaking.class);
    private final SteamMain steamMain;

    public OrbiterSteamMatchmaking(SteamMain steamMain)
    {
        this.steamMain = steamMain;
    }

    @Override
    public void onFavoritesListChanged(int ip, int queryPort, int connPort, int appID, int flags, boolean add, int accountID)
    {
        LOGGER.finest("onFavoritesListChanged: %s, %s, %s, %s, %s, %s, %s".formatted(ip, queryPort, connPort, appID, flags, add, accountID));
    }

    @Override
    public void onLobbyInvite(SteamID steamIDUser, SteamID steamIDLobby, long gameID)
    {
        // The client has been invited to a lobby
        LOGGER.finest("onLobbyInvite: %s, %s, %s".formatted(steamIDUser, steamIDLobby, gameID));
        Lobby lobby = new Lobby(steamIDLobby, steamMain);
        steamMain.lobbyManager.lobbyInvites.add(new LobbyInvite(steamIDUser, lobby));
        Console.log("You've been invited to lobby " + steamIDLobby + " by " + steamMain.steamFriends.getFriendPersonaName(steamIDUser), Color.BLACK);
    }

    @Override
    public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response)
    {
        // Recieved upon attempting to enter a lobby. Lobby metadata is available to use immediately after receiving this.
        LOGGER.finest("onLobbyEnter: %s, %s, %s, %s".formatted(steamIDLobby, chatPermissions, blocked, response));
        steamMain.lobbyManager.setCurrentLobby(new Lobby(steamIDLobby, steamMain));
        steamMain.lobbyManager.currentLobby().updateUsers();
    }

    @Override
    public void onLobbyDataUpdate(SteamID steamIDLobby, SteamID steamIDMember, boolean success)
    {
        LOGGER.finest("onLobbyDataUpdate: %s, %s, %s".formatted(steamIDLobby, steamIDMember, success));
        if (steamMain.lobbyManager.lobbyList == null)
        {
            if (steamMain.lobbyManager.currentLobby() == null)
            {
                LOGGER.warning("Lobby Data Update recieved when no lobby is present ?");
                return;
            }

            // As per spec, if IDMember == IDLobby, update lobby data
            if (steamIDLobby.equals(steamIDMember))
            {
                steamMain.lobbyManager.currentLobby().requestData();
            } else
            {
                LOGGER.info("Updated member data, implementation missing");
            }
            return;
        }

        // Requesting lobby list
        Lobby lobby = new Lobby(steamIDLobby, steamMain);
        lobby.updateLobbyInfo();
        steamMain.lobbyManager.foundLobbies.add(lobby);
        lobby.requestData();

        steamMain.lobbyManager.matchCount--;
        if (steamMain.lobbyManager.matchCount == 0)
        {
            steamMain.lobbyManager.lobbySearchCallback.accept(List.copyOf(steamMain.lobbyManager.foundLobbies));
            steamMain.lobbyManager.lobbySearchCallback = null;
        }
    }

    @Override
    public void onLobbyChatUpdate(SteamID steamIDLobby, SteamID steamIDUserChanged, SteamID steamIDMakingChange, SteamMatchmaking.ChatMemberStateChange stateChange)
    {
        // A lobby chat room state has changed, this is usually sent when a user has joined or left the lobby.
        LOGGER.finest("onLobbyChatUpdate: %s, %s, %s, %s".formatted(steamIDLobby, steamIDUserChanged, steamIDMakingChange, stateChange));

        if (stateChange == SteamMatchmaking.ChatMemberStateChange.Entered)
        {
            if (steamMain.lobbyManager.currentLobby() == null)
            {
                LOGGER.warning("Someone changed lobby... What lobby tho ?");
                return;
            }

            if (!steamMain.lobbyManager.currentLobby().lobbyID().equals(steamIDLobby))
            {
                LOGGER.warning("Someone changed lobby... Not our current lobby tho ?");
                return;
            }

            LOGGER.info("User entered lobby " + steamMain.steamFriends.getFriendPersonaName(steamIDUserChanged));
            steamMain.lobbyManager.currentLobby()._addUser(steamIDUserChanged);
        }
//        steamMain.invites.add(new LobbyInvite(steamIDUserChanged, steamIDLobby));
    }

    @Override
    public void onLobbyChatMessage(SteamID steamIDLobby, SteamID steamIDUser, SteamMatchmaking.ChatEntryType entryType, int chatID)
    {
        LOGGER.finest("onLobbyChatMessage: %s, %s, %s, %s".formatted(steamIDLobby, steamIDUser, entryType, chatID));

        if (entryType == SteamMatchmaking.ChatEntryType.ChatMsg)
        {
            // Steam allows max of 4kb in lobby messages
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024 * 4);
            SteamMatchmaking.ChatEntry chatEntry = new SteamMatchmaking.ChatEntry();
            try
            {
                int readBytes = steamMain.steamMatchmaking.getLobbyChatEntry(steamIDLobby, chatID, chatEntry, byteBuffer);
                byte[] bytes = new byte[readBytes];
                byteBuffer.get(bytes);
                steamMain.packetManager.handleRawPacket(bytes, LobbyListener.class, steamIDUser);

            } catch (SteamException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onLobbyGameCreated(SteamID steamIDLobby, SteamID steamIDGameServer, int ip, short port)
    {
        LOGGER.finest("onLobbyGameCreated: %s, %s, %s, %s".formatted(steamIDLobby, steamIDGameServer, ip, port));
    }

    @Override
    public void onLobbyMatchList(int lobbiesMatching)
    {
        LOGGER.finest("onLobbyMatchList: %s".formatted(lobbiesMatching));
        if (steamMain.lobbyManager.lobbyList == null)
        {
            LOGGER.warning("Lobby List recieved when no requested ? (" + lobbiesMatching + ")");
            return;
        }

        steamMain.lobbyManager.foundLobbiesCount(lobbiesMatching);
    }

    @Override
    public void onLobbyKicked(SteamID steamIDLobby, SteamID steamIDAdmin, boolean kickedDueToDisconnect)
    {
        // Currently unused as per spec https://partner.steamgames.com/doc/api/ISteamMatchmaking#LobbyKicked_t
        LOGGER.finest("onLobbyKicked: %s, %s, %s".formatted(steamIDLobby, steamIDAdmin, kickedDueToDisconnect));
    }

    @Override
    public void onLobbyCreated(SteamResult result, SteamID steamIDLobby)
    {
        LOGGER.finest("onLobbyCreated: %s, %s".formatted(result, steamIDLobby));
        if (steamMain.lobbyManager.creatingLobby == null)
        {
            LOGGER.warning("Lobby created when no lobby requested ?");
            return;
        }
        steamMain.lobbyManager.lobbyCreated(steamIDLobby);
        Console.log("Lobby created " + steamIDLobby + " result: " + result, Color.GREEN);
    }

    @Override
    public void onFavoritesListAccountsUpdated(SteamResult result)
    {
        LOGGER.finest("onFavoritesListAccountsUpdated: %s".formatted(result));
    }
}