package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.network.PacketManager;
import steve6472.orbiter.network.PeerConnections;
import steve6472.orbiter.network.packets.game.GameListener;
import steve6472.orbiter.network.packets.game.Heartbeat;
import steve6472.orbiter.network.packets.game.TeleportToPosition;
import steve6472.orbiter.network.test.FakeP2PConstants;
import steve6472.orbiter.network.test.FakeSteamPeerConnections;
import steve6472.orbiter.steam.lobby.Lobby;
import steve6472.orbiter.steam.lobby.LobbyManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class SteamMain
{
    private static final Logger LOGGER = Log.getLogger(SteamMain.class);
    public final OrbiterApp orbiterApp;
    private boolean enabled;

    public SteamFriends steamFriends;
    public SteamNetworking steamNetworking;
    public SteamMatchmaking steamMatchmaking;
    public SteamUser steamUser;
    public SteamID userID;
    public LobbyManager lobbyManager;
    public PacketManager packetManager;
    public SteamFriendNameCache friendNames;
    public PeerConnections<SteamPeer> connections;

    public SteamMain(OrbiterApp orbiterApp)
    {
        this.orbiterApp = orbiterApp;
    }

    public void setup()
    {
        if (!OrbiterMain.FAKE_P2P && OrbiterMain.ENABLE_STEAM)
        {
            try
            {
                SteamAPI.loadLibraries();
                enabled = SteamAPI.init();
                if (!enabled)
                    LOGGER.warning("Failed to start SteamAPI");
                else
                    LOGGER.info("Started SteamAPI");
            } catch (SteamException e)
            {
                LOGGER.log(Level.WARNING, e, () -> "Error");
                throw new RuntimeException(e);
            }
        } else
        {
            enabled = true;
        }

        if (!enabled) return;

        packetManager = new PacketManager();

        if (OrbiterMain.FAKE_P2P)
        {
            connections = new FakeSteamPeerConnections(this);
            if (OrbiterMain.FAKE_PEER)
                userID = FakeP2PConstants.FAKE_PEER;
            else
                userID = FakeP2PConstants.USER_ID;
            lobbyManager = new LobbyManager(this);
            Lobby lobby = new Lobby(FakeP2PConstants.LOBBY_ID, this);
            lobbyManager.setCurrentLobby(lobby);
            lobby._setLobbyOwner(FakeP2PConstants.USER_ID);
        } else
        {
            steamUser = new SteamUser(new OrbiterSteamUserCallback());
            userID = steamUser.getSteamID();
            steamFriends = new SteamFriends(new OrbiterSteamFriends(this));
            friendNames = new SteamFriendNameCache(steamFriends, userID);
            steamNetworking = new SteamNetworking(new OrbiterSteamNetworking(this));
            steamMatchmaking = new SteamMatchmaking(new OrbiterSteamMatchmaking(this));
            lobbyManager = new LobbyManager(this);
            connections = new SteamPeerConnections(this);
        }

        createListeners();
    }

    public boolean isHost()
    {
        return lobbyManager.currentLobby() == null || lobbyManager.currentLobby().lobbyOwner() == userID;
    }

    private void createListeners()
    {
        packetManager.registerListener(new GameListener(this, orbiterApp.getWorld()));

        connections.setListener(GameListener.class);
    }

    int tick = 0;

    public void tick()
    {
        if (!enabled) return;

        try
        {
            connections.tick();

            Vector3f vector3f = orbiterApp.getClient().player().getCenterPos();
            connections.broadcastMessage(new TeleportToPosition(vector3f));

            // Every second send heartbeat
            if (tick == 30)
            {
                connections.broadcastMessage(Heartbeat.instance());
            }

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        runCallbacks();
    }

    private void runCallbacks()
    {
        if (!OrbiterMain.FAKE_P2P && SteamAPI.isSteamRunning())
        {
            SteamAPI.runCallbacks();
        }

        if (tick == 0)
            tick = 60;
        tick--;
    }

    public void shutdown()
    {
        if (!enabled) return;

        if (!OrbiterMain.FAKE_P2P)
            SteamAPI.shutdown();
    }
}
