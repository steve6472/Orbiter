package steve6472.orbiter.network.impl.dedicated;

import steve6472.core.network.Packet;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.*;
import steve6472.orbiter.settings.Settings;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.*;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public class DedicatedConnections implements Connections
{
    DedicatedLobby lobby;
    PacketManager packetManager;
    BandwidthTracker tracker;

    public DedicatedConnections(DedicatedLobby lobby, PacketManager packetManager)
    {
        this.lobby = lobby;
        this.packetManager = packetManager;
        this.tracker = new BandwidthTracker();
    }

    @Override
    public void tick()
    {
        if (!lobby.isLobbyOpen())
            return;

        tracker.tick();
        readPackets();
        checkForTimeouts();

        // Host is gone
        if (!lobby.isHost() && lobby.getConnectedUsers().isEmpty())
            lobby.closeLobby();
    }

    @Override
    public boolean readPackets()
    {
        try
        {
            while (lobby.getSelector().selectNow() != 0)
            {
                Set<SelectionKey> selectedKeys = lobby.getSelector().selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext())
                {
                    SelectionKey key = keyIterator.next();

                    if (key.isReadable())
                    {
                        receiveData();
                    }

                    keyIterator.remove(); // Remove the key after handling it
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return true;
    }

    private static final UUID NEW_CONNECTION = new UUID(1111111111111111111L, 1111111111111111111L);
    // Receive data from the channel
    private void receiveData() throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocate(8192); // Adjust size as needed
        SocketAddress sender = lobby.getChannel().receive(buffer);

        buffer.flip();

        byte[] receivedData = new byte[buffer.remaining()];
        buffer.get(receivedData);

        tracker.addReadBytes(receivedData.length);

        for (ConnectedUser connectedUser : lobby.getConnectedUsers())
        {
            if (!(connectedUser.user() instanceof DedicatedUser dedUser)) throw new NotDedicatedUserException();

            if (dedUser.userConnection.getPeerAddress().equals(sender))
            {
                connectedUser.updatePacketTime();
                packetManager.handleRawPacket(receivedData, connectedUser.user().getUserStage().pickListener(lobby.isHost()), connectedUser.user());
                return;
            }
        }

        // TODO: automatic rate limit for new connection
        LOGGER.info("Potential new connection from: " + sender);

        //TODO: For now assign random UUID
        DedicatedUser user = new DedicatedUser(NEW_CONNECTION, new DedicatedUserConnection(((DedicatedMain) OrbiterApp.getInstance().getNetwork()), sender));
        user.changeUserStage(UserStage.LOGIN);
        packetManager.handleRawPacket(receivedData, user.getUserStage().pickListener(lobby.isHost()), user);
    }

    @Override
    public boolean sendPacket(User user, ByteBuffer packetBuffer)
    {
        if (!(user instanceof DedicatedUser dedUser)) throw new NotDedicatedUserException();
        assert dedUser.userConnection != null : "User Connection is null somehow";

        tracker.addSendBytes(packetBuffer.limit());

        dedUser.userConnection.sendPacket(packetBuffer);

        return true;
    }

    @Override
    public <T extends Packet<T, ?>> void sendPacket(User user, @Nonnull T packet)
    {
        Objects.requireNonNull(packet, "Packet can not be null");
        ByteBuffer dataPacket = packetManager.createDataPacket(packet);
        if (Settings.LOG_PACKETS.get())
            LOGGER.info("[PKT] Send: " + packet + " to " + user);
        if (!sendPacket(user, dataPacket))
        {
            LOGGER.severe("Packet failed to send to user " + user + " packet: " + packet);
        }
    }

    // When a peer does something, that action gets added to a list on their end and a packet is sent
    // if the host confirms this action is valid, they resend this packet (if that's what should happen) and they send ACK to the peer that originally sent this packet
    // otherwise the peer gets a NAK, the actions get reversed and their list cleared
    // I wanna die omg networking is hell
    @Override
    public <T extends Packet<T, ?>> void broadcastPacket(@Nonnull T packet)
    {
        Objects.requireNonNull(packet, "Packet can not be null");
        if (Settings.LOG_PACKETS.get())
            LOGGER.info("[PKT] Broadcast: " + packet);
        for (ConnectedUser connectedUser : lobby.getConnectedUsers())
        {
            ByteBuffer dataPacket = packetManager.createDataPacket(packet);
            if (!sendPacket(connectedUser.user(), dataPacket))
            {
                LOGGER.severe("Packet failed to send to user " + connectedUser.user() + " packet: " + packet);
            }
        }
    }

    @Override
    public <T extends Packet<T, ?>> void broadcastPacketExclude(T packet, Set<User> excludedUsers)
    {
        for (ConnectedUser connectedUser : lobby.getConnectedUsers())
        {
            if (excludedUsers.contains(connectedUser.user()))
                continue;

            ByteBuffer dataPacket = packetManager.createDataPacket(packet);
            if (!sendPacket(connectedUser.user(), dataPacket))
            {
                LOGGER.severe("Packet failed to send to user " + connectedUser.user() + " packet: " + packet);
            }
        }
    }

    @Override
    public BandwidthTracker bandwidthTracker()
    {
        return tracker;
    }

    /*
     * Impl stuff
     */

    public boolean verifySender(Lobby currentLobby, User user)
    {
        for (ConnectedUser connectedUser : currentLobby.getConnectedUsers())
        {
            if (connectedUser.user().equals(user))
            {
                return true;
            }
        }

        LOGGER.warning("Recieved packet from a user not in lobby!");
        return false;
    }

    private void checkForTimeouts()
    {
        List<ConnectedUser> toKick = null;

        for (ConnectedUser connectedUser : lobby.getConnectedUsers())
        {
            if (connectedUser.hasTimedOut())
            {
                if (toKick == null)
                    toKick = new ArrayList<>();
                toKick.add(connectedUser);
                LOGGER.warning("User " + connectedUser.user() + " has timed out!");
            }
        }

        if (toKick != null)
        {
            for (ConnectedUser connectedUser : toKick)
            {
                lobby.kickUser(connectedUser.user(), "Timed Out");
            }
        }
    }
}
