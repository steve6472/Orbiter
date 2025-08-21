package steve6472.orbiter.network.impl.dedicated;

import steve6472.core.log.Log;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.api.*;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;
import steve6472.orbiter.network.packets.configuration.clientbound.HeartbeatClientbound;
import steve6472.orbiter.network.packets.configuration.hostbound.HeartbeatHostbound;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;
import steve6472.orbiter.network.packets.login.LoginHostboundListener;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class DedicatedMain implements NetworkMain
{
    private static final Logger LOGGER = Log.getLogger(DedicatedMain.class);

    public static final int MIN_PORT = 49152;
    public static final int MAX_PORT = 65535;

    LanDetector detector;
    LanBroadcaster broadcaster;
    PacketManager packetManager;
    Lobby lobby;

    @Override
    public void setup()
    {
        packetManager = new PacketManager(Registries.PACKET);
        lobby = new DedicatedLobby(this);
        broadcaster = new LanBroadcaster(() -> "TEST");
        detector = new LanDetector();

        packetManager.registerListener(new LoginHostboundListener());
        packetManager.registerListener(new LoginClientboundListener());
        packetManager.registerListener(new ConfigurationHostboundListener());
        packetManager.registerListener(new ConfigurationClientboundListener());
    }

    long tick;

    @Override
    public void tick()
    {
        if (((DedicatedLobby) lobby()).shouldClose())
            ((DedicatedLobby) lobby).deleteLobby();

        connections().tick();

        // Broadcast Heartbeat on a timer
        if (tick % (60) == 0)
        {
            if (lobby.isHost())
                connections().broadcastPacket(HeartbeatClientbound.instance());
            else
                connections().broadcastPacket(HeartbeatHostbound.instance());
        }

        tick++;
    }

    @Override
    public void shutdown()
    {
        // Broadcast kick packet, reason - shutdown
//        connections().broadcastPacket();
//        for (ConnectedUser connectedUser : lobby.getConnectedUsers())
//        {
//            if (!(connectedUser.user() instanceof DedicatedUser dedUser)) throw new NotDedicatedUserException();
//
//            dedUser.userConnection.close();
//        }
        broadcaster.shutdown();
        lobby.closeLobby();
    }

    public LanBroadcaster getBroadcaster()
    {
        return broadcaster;
    }

    public LanDetector getDetector()
    {
        return detector;
    }

    @Override
    public Connections connections()
    {
        return lobby.connections();
    }

    @Override
    public PacketManager packetManager()
    {
        return packetManager;
    }

    @Override
    public Lobby lobby()
    {
        return lobby;
    }
}
