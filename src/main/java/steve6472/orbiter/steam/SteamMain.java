package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import steve6472.core.log.Log;
import steve6472.core.network.Packet;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.PacketManager;
import steve6472.orbiter.network.packets.game.GameListener;
import steve6472.orbiter.network.packets.game.TeleportToPosition;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.steam.lobby.LobbyManager;

import java.nio.ByteBuffer;
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
    public SteamID peer;
    public SteamID userID;
    public LobbyManager lobbyManager;
    public PacketManager packetManager;
    public SteamFriendNameCache friendNames;

    public SteamMain(OrbiterApp orbiterApp)
    {
        this.orbiterApp = orbiterApp;
    }

    public void setup()
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
        }

        if (!enabled) return;
        packetManager = new PacketManager();
        steamUser = new SteamUser(new OrbiterSteamUserCallback());
        userID = steamUser.getSteamID();
        steamFriends = new SteamFriends(new OrbiterSteamFriends(this));
        friendNames = new SteamFriendNameCache(steamFriends, userID);
        steamNetworking = new SteamNetworking(new OrbiterSteamNetworking(this));
        steamMatchmaking = new SteamMatchmaking(new OrbiterSteamMatchmaking(this));
        lobbyManager = new LobbyManager(this);

        createListeners();
    }

    private void createListeners()
    {
        packetManager.registerListener(new GameListener(this, orbiterApp.getWorld()));
    }

    int tick = 0;

    public void tick()
    {
        if (!enabled) return;

        try
        {
            receiveMessage();
            Vector3f vector3f = orbiterApp.getClient().player().getCenterPos();
            sendMessage(new TeleportToPosition(vector3f));
        } catch (SteamException e)
        {
            throw new RuntimeException(e);
        }
        runCallbacks();

        if (Keybinds.TEST.isActive())
        {
            steamFriends.activateGameOverlay(SteamFriends.OverlayDialog.Friends);
        }
    }

    public <T extends Packet<T, ?>> void sendMessage(T packet) throws SteamException
    {
        if (peer == null)
            return;

        if (!steamNetworking.sendP2PPacket(peer, packetManager.createDataPacket(packet), SteamNetworking.P2PSend.Reliable, 0))
        {
            LOGGER.warning("Packet was not sent!");
        }
    }

    public void receiveMessage() throws SteamException
    {
        // Buffer to hold incoming message
        int[] messageSize = new int[1];

        // Check if there is a packet waiting
        while (steamNetworking.isP2PPacketAvailable(0, messageSize))
        {
            // Read the packet
            SteamID remoteID = new SteamID();
            ByteBuffer buffer = BufferUtils.createByteBuffer(messageSize[0]);
            int i = steamNetworking.readP2PPacket(remoteID, buffer, 0);

            if (i != messageSize[0])
                LOGGER.warning("Packet size mismatch");

            packetManager.handlePacket(buffer, GameListener.class, remoteID);
        }
    }

    private void runCallbacks()
    {
        if (SteamAPI.isSteamRunning())
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

        SteamAPI.shutdown();
    }
}
