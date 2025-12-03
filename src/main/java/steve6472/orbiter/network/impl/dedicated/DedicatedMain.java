package steve6472.orbiter.network.impl.dedicated;

import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.BodyInterface;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.api.*;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;
import steve6472.orbiter.network.packets.configuration.clientbound.HeartbeatClientbound;
import steve6472.orbiter.network.packets.configuration.hostbound.HeartbeatHostbound;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;
import steve6472.orbiter.network.packets.login.LoginHostboundListener;
import steve6472.orbiter.network.packets.game.GameClientboundListener;
import steve6472.orbiter.network.packets.game.GameHostboundListener;
import steve6472.orbiter.network.packets.game.clientbound.CreateCustomEntity;
import steve6472.orbiter.network.packets.game.clientbound.EnterWorld;
import steve6472.orbiter.network.packets.game.clientbound.GameHeartbeatClientbound;
import steve6472.orbiter.network.packets.game.clientbound.KickUser;
import steve6472.orbiter.network.packets.game.hostbound.Disconnect;
import steve6472.orbiter.network.packets.game.hostbound.GameHeartbeatHostbound;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class DedicatedMain implements NetworkMain
{
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
        packetManager.registerListener(new GameHostboundListener());
        packetManager.registerListener(new GameClientboundListener());
    }

    public void newPlayer(World world, User sender)
    {
        connections().sendPacket(sender, new EnterWorld());

        Entity playerEntity = world.addEntity(Registries.ENTITY_BLUEPRINT.get(Constants.key("mp_player")), sender.uuid(), Map.of(), false);
        world.addComponent(playerEntity, new MPControlled(sender.uuid()));
        BodyInterface bodyInterface = world.physics().getBodyInterface();
        bodyInterface.setUserData(world.bodyMap().getIdByUUID(sender.uuid()), Constants.PhysicsFlags.MP_PLAYER);

        for (ConnectedUser connectedUser : lobby().getConnectedUsers())
        {
            if (connectedUser.user().equals(sender))
                continue;
            connections().sendPacket(connectedUser.user(), new CreateCustomEntity(playerEntity));
        }
    }

    long tick;

    @Override
    public void tick()
    {
        if (((DedicatedLobby) lobby()).shouldClose())
            ((DedicatedLobby) lobby).deleteLobby();

        connections().tick();

        // Broadcast Heartbeat on a timer
        sendHeartbeat();

        tick++;
    }

    private void sendHeartbeat()
    {
        if (tick % (60) != 0)
            return;

        for (ConnectedUser connectedUser : lobby().getConnectedUsers())
        {
            switch (connectedUser.user().getUserStage())
            {
                case CONFIGURATION ->
                {
                    if (lobby.isHost())
                        connections().sendPacket(connectedUser.user(), HeartbeatClientbound.instance());
                    else
                        connections().sendPacket(connectedUser.user(), HeartbeatHostbound.instance());
                }
                case PLAY ->
                {
                    if (lobby.isHost())
                        connections().sendPacket(connectedUser.user(), GameHeartbeatClientbound.instance());
                    else
                        connections().sendPacket(connectedUser.user(), GameHeartbeatHostbound.instance());
                }
            }
        }
    }

    @Override
    public void shutdown()
    {
        if (lobby.isLobbyOpen())
        {
            if (lobby.isHost())
            {
                for (ConnectedUser connectedUser : lobby.getConnectedUsers())
                {
                    connections().sendPacket(connectedUser.user(), new KickUser(connectedUser.user(), "Host Shutdown"));
                }
            } else
            {
                connections().broadcastPacket(Disconnect.instance());
            }
        }

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
