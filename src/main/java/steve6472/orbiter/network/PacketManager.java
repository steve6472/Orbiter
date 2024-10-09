package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import steve6472.core.log.Log;
import steve6472.core.network.Packet;
import steve6472.core.network.PacketListener;
import steve6472.orbiter.Registries;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class PacketManager
{
    private static final Logger LOGGER = Log.getLogger(PacketManager.class);

    private static final int INITIAL_BYTES = 128;

    private final PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
    private final Map<Class<? extends PacketListener>, PacketListener> listeners = new HashMap<>();
    private SteamID lastSender;

    public SteamID lastSender()
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
            packetId = Registries.PACKET.getPacketIntKey(packet.key());
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

    public <T extends PacketListener> void handlePacket(ByteBuffer buffer, Class<T> listenerClass, SteamID lastSender)
    {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        handleRawPacket(bytes, listenerClass, lastSender);
    }

    public <T extends PacketListener> void handleRawPacket(byte[] bytes, Class<T> listenerClass, SteamID lastSender)
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

            var packetCodec = Registries.PACKET.getPacketFromIntKey(packetId);
            //noinspection unchecked but this may actually throw an error
            Packet<?, T> decode = (Packet<?, T>) packetCodec.decode(buffer);
            decode.handlePacket(packetListener);
        } catch (Exception e)
        {
            LOGGER.severe("Exception caught when processing packet id " + packetId + " (" + Registries.PACKET.getPacketKeyByIntKey(packetId) + ")");
            throw e;
        }
    }

    public void registerListener(PacketListener packetListener)
    {
        listeners.put(packetListener.getClass(), packetListener);
    }

    public void unregisterListener(Class<? extends PacketListener> listenerClass)
    {
        listeners.remove(listenerClass);
    }
}
