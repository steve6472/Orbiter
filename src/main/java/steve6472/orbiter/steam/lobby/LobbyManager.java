package steve6472.orbiter.steam.lobby;

import com.codedisaster.steamworks.*;
import steve6472.core.log.Log;
import steve6472.orbiter.network.packets.lobby.LobbyListener;
import steve6472.orbiter.steam.LobbyInvite;
import steve6472.orbiter.steam.SteamMain;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class LobbyManager
{
    public static final int MAX_MEMBERS = 16;
    private static final Logger LOGGER = Log.getLogger(LobbyManager.class);

    private final SteamMain steamMain;
    private final SteamMatchmaking matchmaking;

    // State variables for multi-step operations
    public SteamAPICall creatingLobby;
    public SteamAPICall lobbyList;
    private SteamMatchmaking.LobbyType creatingLobbyType;
    private int creatingLobbyMaxMembers;
    public Consumer<List<Lobby>> lobbySearchCallback;
    public Consumer<Lobby> lobbyCreateCallback;
    public int matchCount;

    public final List<LobbyInvite> lobbyInvites = new ArrayList<>();

    public final List<Lobby> foundLobbies = new ArrayList<>();
    private Lobby currentLobby;

    public LobbyManager(SteamMain steamMain)
    {
        this.steamMain = steamMain;
        this.matchmaking = steamMain.steamMatchmaking;
    }

    /*
     * Lobby list
     */

    public void findLobbies(Consumer<List<Lobby>> callback)
    {
        matchmaking.addRequestLobbyListResultCountFilter(8);
        matchmaking.addRequestLobbyListStringFilter("AppName", "Orbiter", SteamMatchmaking.LobbyComparison.Equal);
        lobbyList = matchmaking.requestLobbyList();
        lobbySearchCallback = callback;
    }

    public void foundLobbiesCount(int matchCount)
    {
        foundLobbies.clear();
        this.matchCount = matchCount;

        for (int i = 0; i < matchCount; i++)
        {
            SteamID lobbyByIndex = matchmaking.getLobbyByIndex(i);
            matchmaking.requestLobbyData(lobbyByIndex);
        }
    }

    public Lobby currentLobby()
    {
        return currentLobby;
    }

    public void setCurrentLobby(Lobby lobby)
    {
        if (lobby == null)
            throw new RuntimeException("Use a different method to destroy a lobby");

        if (currentLobby != null)
        {
            LOGGER.warning("Tried to set current lobby when lobby already exists!");
            return;
        }

        currentLobby = lobby;
        steamMain.packetManager.registerListener(new LobbyListener(currentLobby, steamMain));
    }

    public void resetCurrentLobby()
    {
        if (currentLobby == null)
        {
            LOGGER.warning("Tried to reset currentLobby when no lobby exists!");
            return;
        }

        currentLobby = null;
        steamMain.packetManager.unregisterListener(LobbyListener.class);
    }

    /*
     * Lobby creating
     */

    public void createLobby(SteamMatchmaking.LobbyType lobbyType, int maxMembers, Consumer<Lobby> callback)
    {
        if (creatingLobby != null || currentLobby != null)
        {
            LOGGER.warning("Tried to create multiple lobbies!");
            return;
        }

        if (maxMembers > MAX_MEMBERS)
        {
            LOGGER.warning("Tried to create too big lobby! (" + maxMembers + ") Setting to " + MAX_MEMBERS);
            maxMembers = MAX_MEMBERS;
        }

        lobbyCreateCallback = callback;
        creatingLobbyType = lobbyType;
        creatingLobbyMaxMembers = maxMembers;
        creatingLobby = matchmaking.createLobby(lobbyType, maxMembers);
    }

    public void lobbyCreated(SteamID lobbyID)
    {
        currentLobby = new Lobby(lobbyID, steamMain, creatingLobbyType, creatingLobbyMaxMembers, steamMain.userID);
        currentLobby.updateDefaultData();
        creatingLobby = null;
        creatingLobbyType = null;
        creatingLobbyMaxMembers = 0;
        lobbyCreateCallback.accept(currentLobby);
        lobbyCreateCallback = null;

        steamMain.packetManager.registerListener(new LobbyListener(currentLobby, steamMain));
    }

    /*
     * Lobby closing
     */

    public void closeLobby()
    {
        if (currentLobby == null)
        {
            LOGGER.warning("Tried to close lobby when none is open!");
            return;
        }

        currentLobby.close();
    }
}