package steve6472.orbiter.network.api;

import steve6472.core.log.Log;
import steve6472.core.network.Packet;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public interface Connections
{
    Logger LOGGER = Log.getLogger(Connections.class);
    int TIMEOUT_MS = 10_000;

    void tick();

    // Low-level
    boolean readPackets();
    boolean sendPacket(User user, ByteBuffer packetBuffer);

    <T extends Packet<T, ?>> void sendPacket(User user, T packet);
    <T extends Packet<T, ?>> void broadcastPacket(T packet);
    <T extends Packet<T, ?>> void broadcastPacketExclude(T packet, Set<User> excludedUsers);

    BandwidthTracker bandwidthTracker();
}
