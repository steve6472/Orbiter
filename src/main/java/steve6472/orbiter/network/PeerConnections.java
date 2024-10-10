package steve6472.orbiter.network;

import steve6472.core.log.Log;
import steve6472.core.network.Packet;
import steve6472.core.network.PacketListener;
import steve6472.orbiter.steam.SteamMain;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public abstract class PeerConnections<P extends Peer>
{
    private static final int TIMEOUT_MS = 15_000;

    public static final Logger LOGGER = Log.getLogger(PeerConnections.class);

    private final List<ConnectedPeer> peers = new ArrayList<>();
    protected final PacketManager packetManager;
    private Class<? extends PacketListener> listener;

    public PeerConnections(SteamMain steamMain)
    {
        this.packetManager = steamMain.packetManager;
    }

    public final void tick() throws Exception
    {
        if (!readPackets())
        {
            LOGGER.severe("Reading packets failed!");
        }

        checkForTimeouts();
    }

    /// Used by [steve6472.orbiter.network.test.FakeSteamPeerConnections], use only for testing!
    protected boolean disableTimeoutCheck()
    {
        return false;
    }

    private void checkForTimeouts()
    {
        if (disableTimeoutCheck())
            return;

        for (Iterator<ConnectedPeer> iterator = peers.iterator(); iterator.hasNext(); )
        {
            ConnectedPeer peer = iterator.next();
            if (peer.hasTimedOut())
            {
                // TODO: add callback
                LOGGER.warning("Peer " + peer.peer + " has timed out!");
                iterator.remove();
            }
        }
    }

    public final void addPeer(P peer)
    {
        ConnectedPeer connectedPeer = new ConnectedPeer(peer);
        if (peers.contains(connectedPeer))
        {
            LOGGER.warning("Tried to add a peer but peer already in list!");
            return;
        }

        peers.add(connectedPeer);
    }

    public final void removePeer(P peer)
    {
        ConnectedPeer connectedPeer = new ConnectedPeer(peer);
        if (!peers.remove(connectedPeer))
        {
            LOGGER.warning("Tried to remove peer, but peer not in list!");
        }
    }

    /// Returns copy of peers
    public final List<P> listPeers()
    {
        List<P> peers = new ArrayList<>(this.peers.size());
        for (ConnectedPeer peer : this.peers)
        {
            peers.add(peer.peer);
        }
        return peers;
    }

    public final <T extends Packet<T, ?>> void broadcastMessageExclude(T packet, P excludedPeer)
    {
        broadcastMessageExclude(packet, Set.of(excludedPeer));
    }

    public final <T extends Packet<T, ?>> void broadcastMessageExclude(T packet, Set<P> excludedPeers)
    {
        try
        {
            ByteBuffer dataPacket = packetManager.createDataPacket(packet);
            for (ConnectedPeer connectedPeer : peers)
            {
                if (excludedPeers.contains(connectedPeer.peer))
                    continue;

                if (!sendPacket(connectedPeer.peer, dataPacket))
                {
                    LOGGER.severe("Packet was not sent!");
                }
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public final <T extends Packet<T, ?>> void broadcastMessage(T packet)
    {
        try
        {
            ByteBuffer dataPacket = packetManager.createDataPacket(packet);
            for (ConnectedPeer connectedPeer : peers)
            {
                if (!sendPacket(connectedPeer.peer, dataPacket))
                {
                    LOGGER.severe("Packet was not sent!");
                }
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public final <T extends Packet<T, ?>> void sendMessage(P peer, T packet)
    {
        try
        {
            if (!sendPacket(peer, packetManager.createDataPacket(packet)))
            {
                LOGGER.severe("Packet was not sent!");
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected boolean verifySender(P peer)
    {
        for (ConnectedPeer connectedPeer : peers)
        {
            if (connectedPeer.peer.equals(peer))
            {
                connectedPeer.updatePacketTime();
                return true;
            }
        }

        LOGGER.warning("Recieved packet from an unknown peer!");
        return false;
    }

    protected final Class<? extends PacketListener> listener()
    {
        return listener;
    }

    public final void setListener(Class<? extends PacketListener> listener)
    {
        this.listener = listener;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected abstract boolean sendPacket(P peer, ByteBuffer packetBuffer) throws Exception;
    protected abstract boolean readPackets() throws Exception;

    private final class ConnectedPeer
    {
        private final P peer;
        private long lastPacket;

        ConnectedPeer(P peer)
        {
            this.peer = peer;
            lastPacket = System.currentTimeMillis();
        }

        void updatePacketTime()
        {
            lastPacket = System.currentTimeMillis();
        }

        boolean hasTimedOut()
        {
            return lastPacket + TIMEOUT_MS < System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            //noinspection unchecked
            var that = (ConnectedPeer) o;
            return Objects.equals(peer, that.peer);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(peer);
        }
    }
}
