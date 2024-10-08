package steve6472.orbiter.steam.lobby;

import com.codedisaster.steamworks.*;
import steve6472.core.log.Log;
import steve6472.core.network.Packet;
import steve6472.orbiter.network.PacketManager;
import steve6472.orbiter.network.packets.game.GameListener;
import steve6472.orbiter.network.packets.game.HelloGame;
import steve6472.orbiter.network.packets.lobby.KickUser;
import steve6472.orbiter.network.packets.lobby.LobbyClosing;
import steve6472.orbiter.steam.SteamMain;

import java.util.*;
import java.util.logging.Logger;

import static steve6472.orbiter.steam.lobby.LobbyManager.MAX_MEMBERS;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class Lobby
{
    private static final Logger LOGGER = Log.getLogger(Lobby.class);

    private final SteamMain steamMain;
    private final SteamMatchmaking matchmaking;
    private final SteamFriends friends;
    private final PacketManager packetManager;
    private final SteamNetworking networking;

    /*
     * Lobby data
     */
    private final Map<String, String> lobbyData = new HashMap<>();
    private final List<SteamID> connectedUsers = new ArrayList<>();
    private final SteamID lobbyID;

    /*
     * Changeable lobby data
     */
    private SteamMatchmaking.LobbyType lobbyType;
    private int maxMembers;
    private SteamID lobbyOwner;
    public boolean isClosing;

    public Lobby(SteamID lobbyID, SteamMain steamMain)
    {
        this.lobbyID = lobbyID;
        this.steamMain = steamMain;
        this.matchmaking = steamMain.steamMatchmaking;
        this.friends = steamMain.steamFriends;
        this.packetManager = steamMain.packetManager;
        this.networking = steamMain.steamNetworking;
    }

    public Lobby(SteamID lobbyID, SteamMain steamMain, SteamMatchmaking.LobbyType lobbyType, int maxMembers, SteamID lobbyOwner)
    {
        this.lobbyID = lobbyID;
        this.steamMain = steamMain;
        this.matchmaking = steamMain.steamMatchmaking;
        this.friends = steamMain.steamFriends;
        this.packetManager = steamMain.packetManager;
        this.networking = steamMain.steamNetworking;
        this.lobbyType = lobbyType;
        this.maxMembers = maxMembers;
        this.lobbyOwner = lobbyOwner;
    }

    /*
     * Data section
     */

    public void updateDefaultData()
    {
        boolean result = matchmaking.setLobbyData(lobbyID, "AppName", "Orbiter");
        if (!result)
        {
            LOGGER.warning("Could not set default data");
        }
    }

    public void updateLobbyInfo()
    {
        maxMembers = matchmaking.getLobbyMemberLimit(lobbyID);
    }

    public void requestData()
    {
        lobbyData.clear();
        int lobbyDataCount = matchmaking.getLobbyDataCount(lobbyID);
        for (int i = 0; i < lobbyDataCount; i++)
        {
            SteamMatchmakingKeyValuePair data = new SteamMatchmakingKeyValuePair();
            if (!matchmaking.getLobbyDataByIndex(lobbyID, i, data))
            {
                LOGGER.warning("Error when getting lobby data at index " + i);
                break;
            }
            lobbyData.put(data.getKey(), data.getValue());
        }
    }

    /*
     * Lobby management
     */

    public void setLobbyType(SteamMatchmaking.LobbyType lobbyType)
    {
        if (matchmaking.setLobbyType(lobbyID, lobbyType))
        {
            this.lobbyType = lobbyType;
        } else
        {
            LOGGER.warning("Failed to set lobby type! (Not owner?)");
        }
    }

    public void setLobbyMemberLimit(int maxMembers)
    {
        if (maxMembers > MAX_MEMBERS)
        {
            LOGGER.warning("Tried to set too big lobby! (" + maxMembers + ") Setting to " + MAX_MEMBERS);
            maxMembers = MAX_MEMBERS;
        }

        if (matchmaking.setLobbyMemberLimit(lobbyID, maxMembers))
        {
            this.maxMembers = maxMembers;
        } else
        {
            LOGGER.warning("Failed to set max members! (Not owner?)");
        }
    }

    public void changeLobbyOwner(SteamID newOwner)
    {
        if (matchmaking.setLobbyOwner(lobbyID, newOwner))
        {
            lobbyOwner = newOwner;
            // TODO: might want to broadcast this change
        } else
        {
            LOGGER.warning("Failed to change lobby owner! (Not owner?)");
        }
    }

    public void close()
    {
        LOGGER.info("Closing lobby!");
        broadcastPacket(LobbyClosing.instance());
    }

    /// Broadcast packet to everyone (including sender)
    /// @param packet Packet to be broadcasted
    public <T extends Packet<T, ?>> void broadcastPacket(T packet)
    {
        try
        {
            LOGGER.fine("Sending packet " + packet.key());
            matchmaking.sendLobbyChatMsg(lobbyID, packetManager.createDataPacket(packet));
        } catch (SteamException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void startGame()
    {
        for (SteamID connectedUser : connectedUsers)
        {
            // Probably no need to establish P2P connection with yourself
            if (connectedUser.equals(lobbyOwner))
                continue;

            try
            {
                networking.sendP2PPacket(connectedUser, packetManager.createDataPacket(HelloGame.instance()), SteamNetworking.P2PSend.Reliable, 0);
            } catch (SteamException e)
            {
                throw new RuntimeException(e);
            }

            steamMain.peer = connectedUser;
            steamMain.orbiterApp.getWorld().spawnDebugPlayer(connectedUser);
        }
    }

    /*
     * Internal setters
     */

    public void _setLobbyOwner(SteamID lobbyOwner)
    {
        this.lobbyOwner = lobbyOwner;
    }

    public void _setMaxMembers(int maxMembers)
    {
        this.maxMembers = maxMembers;
    }

    public void _setLobbyType(SteamMatchmaking.LobbyType lobbyType)
    {
        this.lobbyType = lobbyType;
    }

    public void _removeUser(SteamID steamID)
    {
        connectedUsers.remove(steamID);
    }

    public void _addUser(SteamID steamID)
    {
        connectedUsers.add(steamID);
    }

    /*
     * User management
     */

    public List<SteamID> updateUsers()
    {
        int numLobbyMembers = matchmaking.getNumLobbyMembers(lobbyID);
        if (numLobbyMembers == 0)
        {
            LOGGER.warning("No data for current lobby!");
            return List.of();
        }

        connectedUsers.clear();
        for (int i = 0; i < numLobbyMembers; i++)
        {
            SteamID lobbyMemberByIndex = matchmaking.getLobbyMemberByIndex(lobbyID, i);
            connectedUsers.add(lobbyMemberByIndex);
        }

        return connectedUsers;
    }

    public void kickUser(SteamID steamID)
    {
        if (!connectedUsers.contains(steamID))
        {
            LOGGER.warning("Tried to kick user that is not in lobby " + steamID + "(" + friends.getFriendPersonaName(steamID) + ")");
            return;
        }

        // TODO: send special kick packet
        broadcastPacket(new KickUser(steamID));
//        connectedUsers.remove(steamID);
    }

    public void kickOwner()
    {
        broadcastPacket(new KickUser(lobbyOwner));
        lobbyOwner = null;
    }

    /*
     * Getters
     */

    public SteamID lobbyID()
    {
        return lobbyID;
    }

    public SteamID lobbyOwner()
    {
        return lobbyOwner;
    }

    public SteamMatchmaking.LobbyType lobbyType()
    {
        return lobbyType;
    }

    public int maxMembers()
    {
        return maxMembers;
    }

    public Map<String, String> lobbyData()
    {
        return Map.copyOf(lobbyData);
    }

    public List<SteamID> getConnectedUsers()
    {
        return List.copyOf(connectedUsers);
    }

    public SteamID getLobbyOwner()
    {
        return matchmaking.getLobbyOwner(lobbyID);
    }

    @Override
    public String toString()
    {
        return "Lobby{" + "lobbyType=" + lobbyType + ", maxMembers=" + maxMembers + '}';
    }
}
