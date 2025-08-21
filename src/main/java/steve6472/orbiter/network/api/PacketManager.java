package steve6472.orbiter.network.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;
import steve6472.core.log.Log;
import steve6472.core.network.Packet;
import steve6472.core.network.PacketListener;
import steve6472.core.registry.PacketRegistry;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public final class PacketManager
{
    private static final Logger LOGGER = Log.getLogger(PacketManager.class);
    private static final int INITIAL_BYTES = 128;

    private final PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
    private final Map<Class<? extends PacketListener>, PacketListener> listeners = new HashMap<>();
    private final PacketRegistry registry;

    private User lastSender;

    public PacketManager(PacketRegistry registry)
    {
        this.registry = registry;
    }

    /// Nullable - returns null if the sender was the "host"
    public @Nullable User lastSender()
    {
        return lastSender;
    }

    public <T extends Packet<T, ?>> ByteBuffer createDataPacket(T packet)
    {
        Objects.requireNonNull(packet);
        ByteBuf buffer = allocator.buffer(INITIAL_BYTES);

        int packetId;
        try
        {
            packetId = registry.getPacketIntKey(packet.key());
        } catch (NullPointerException e)
        {
            LOGGER.severe("Packet ID for " + packet.key() + " was not found!");
            throw new RuntimeException(e);
        }

        try
        {
            buffer.writeInt(packetId);
            packet.codec().encode(buffer, packet);

            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.getBytes(0, bytes);
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(bytes.length);
            directBuffer.put(bytes);
            directBuffer.flip();
            return directBuffer;
        } finally
        {
            buffer.release();
        }
    }

    public <T extends PacketListener> void handlePacket(ByteBuffer buffer, Class<T> listenerClass, User lastSender)
    {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        handleRawPacket(bytes, listenerClass, lastSender);
    }

    public <T extends PacketListener> void handleRawPacket(byte[] bytes, Class<T> listenerClass, User lastSender)
    {
        this.lastSender = lastSender;

        if (bytes.length == 0)
            return;

        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        int packetId = buffer.readInt();

        try
        {
            //noinspection unchecked
            T packetListener = (T) listeners.get(listenerClass);
            if (packetListener == null)
            {
                throw new RuntimeException("Packet Listener for " + listenerClass + " not found!");
            }

            var packetCodec = registry.getPacketFromIntKey(packetId);
            //noinspection unchecked but this may actually throw an error
            Packet<?, T> decode = (Packet<?, T>) packetCodec.decode(buffer);
            decode.handlePacket(packetListener);
        } catch (Exception e)
        {
            LOGGER.severe("Exception caught when processing packet id " + packetId + " (" + registry.getPacketKeyByIntKey(packetId) + ")");
            throw e;
        }
    }

    public void registerListener(PacketListener packetListener)
    {
        listeners.put(packetListener.getClass(), packetListener);
    }
}
