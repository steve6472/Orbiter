package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import steve6472.core.log.Log;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Tag;

import java.nio.ByteBuffer;
import java.util.UUID;
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
    public SteamID peer;

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
        steamFriends = new SteamFriends(new OrbiterSteamFriends());
        steamNetworking = new SteamNetworking(new OrbiterSteamNetworking(this));
    }

    public void tick()
    {
        if (!enabled) return;

        try
        {
            receiveMessage();
            Vector3f vector3f = orbiterApp.getClient().player().getCenterPos();
            sendMessage(vector3f.x + "," + vector3f.y + "," + vector3f.z);
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

    public void sendMessage(String message) throws SteamException
    {
        if (peer == null)
        {
            return;
        }

        byte[] data = message.getBytes();
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        steamNetworking.sendP2PPacket(peer, buffer, SteamNetworking.P2PSend.Reliable, 0);
        LOGGER.finest("Message sent: " + message);
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
            steamNetworking.readP2PPacket(remoteID, buffer, 0);

            // Echo the message back to the sender
            String receivedMessage = new String(buffer.array());
            LOGGER.finest("Message received: " + receivedMessage);

            processMessage(remoteID, receivedMessage);
        }
    }

    private void processMessage(SteamID remoteID, String message)
    {
        var entityList = orbiterApp
            .getWorld()
            .ecs()
            .findEntitiesWith(MPControlled.class, UUID.class, Tag.Physics.class);

        for (var entityData : entityList)
        {
            MPControlled mpControlled = entityData.comp1();
            if (!mpControlled.controller().equals(remoteID))
                continue;

            UUID uuid = entityData.comp2();

            String[] split = message.split(",");
            Vector3f vector3f = new Vector3f(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
            orbiterApp.getWorld().bodyMap.get(uuid).setPhysicsLocation(Convert.jomlToPhys(vector3f));
        }
    }

    private void runCallbacks()
    {
        if (SteamAPI.isSteamRunning())
        {
            SteamAPI.runCallbacks();
        }
    }

    private SteamID findFriendByName(String name)
    {
        int friendCount = steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
        for (int i = 0; i < friendCount; i++)
        {
            SteamID friendByIndex = steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
            if (steamFriends.getFriendPersonaName(friendByIndex).equals(name))
                return friendByIndex;
        }

        return null;
    }

    public void listFriends()
    {
    }

    public void shutdown()
    {
        if (!enabled) return;

        SteamAPI.shutdown();
    }
}
