package steve6472.orbiter.network.impl.dedicated;

import steve6472.orbiter.network.api.*;
import steve6472.orbiter.ui.panel.LobbyMenuDedicated;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public class DedicatedLobby implements Lobby
{
    private final List<ConnectedUser> connectedUsers = new ArrayList<>();
    private final DedicatedConnections connections;

    private final Map<String, String> lobbyData = new HashMap<>();

    private final DedicatedMain network;
    private DatagramChannel channel;
    private Selector selector;
    private boolean isHost;
    private boolean shouldClose;

    public DedicatedLobby(DedicatedMain network)
    {
        this.network = network;
        connections = new DedicatedConnections(this, network.packetManager());
    }

    @Override
    public Connections connections()
    {
        return connections;
    }

    @Override
    public Map<String, String> getLobbyData()
    {
        return Map.copyOf(lobbyData);
    }

    @Override
    public List<ConnectedUser> getConnectedUsers()
    {
        return connectedUsers;
    }

    @Override
    public void joinUser(User user)
    {
        connectedUsers.add(new ConnectedUser(user));
    }

    @Override
    public void kickUser(User userToKick)
    {
        connectedUsers.removeIf(c -> c.user().equals(userToKick));
        // TODO: notify others about kick
//        connections().broadcastPacket();
    }

    @Override
    public boolean isHost()
    {
        return isHost;
    }

    public DatagramChannel getChannel()
    {
        return channel;
    }

    public Selector getSelector()
    {
        return selector;
    }

    @Override
    public boolean isLobbyOpen()
    {
        return !shouldClose && channel != null && channel.isOpen();
    }

    @Override
    public void openLobby(int port, boolean asHost)
    {
        this.isHost = asHost;
        try
        {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeLobby()
    {
        if (!isLobbyOpen())
            return;

        shouldClose = true;
    }

    public boolean shouldClose()
    {
        return shouldClose;
    }

    public void deleteLobby()
    {
        isHost = false;
        getConnectedUsers().clear();
        shouldClose = false;
        LobbyMenuDedicated.lobbyOpen.set(isLobbyOpen());

        try
        {
            channel.close();
            selector.close();
            channel = null;
            selector = null;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /*
     * Impl stuff
     */

    public User expectConnection(DedicatedUserConnection connection)
    {
        ConnectedUser user = new ConnectedUser(new DedicatedUser(connection));
        connectedUsers.add(user);
        return user.user();
    }
}
