package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import steve6472.core.log.Log;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.network.PacketListener;
import steve6472.orbiter.Registries;

import java.nio.ByteBuffer;
import java.util.Arrays;
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

//            ByteBuffer byteBuffer = buffer.nioBuffer();

//            LOGGER.fine("Creating packet: " + Registries.PACKET.getPacketKeyByIntKey(packetId) + " " + Arrays.toString());
            byte[] byteArray = getByteArrayWithoutAffecting(buffer);
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(byteArray.length);
            directBuffer.put(byteArray);
//            LOGGER.fine("Creating packet: " + Registries.PACKET.getPacketKeyByIntKey(packetId) + " " + Arrays.toString(byteArray));
            return directBuffer;
        } finally
        {
            buffer.release();
        }
    }

    public static byte[] getByteArrayWithoutAffecting(ByteBuffer byteBuffer)
    {
        // Save the current position and limit
        int position = byteBuffer.position();
        int limit = byteBuffer.limit();

        // Create a new byte array with the remaining bytes' length
        byte[] bytes = new byte[byteBuffer.remaining()];

        // Copy the buffer's content into the array
        byteBuffer.get(bytes);

        // Restore the original position and limit to avoid affecting the buffer's state
        byteBuffer.position(position);
        byteBuffer.limit(limit);

        return bytes;
    }

    public static byte[] getByteArrayWithoutAffecting(ByteBuf byteBuf)
    {
        // Create a new byte array with the readable bytes' length
        byte[] bytes = new byte[byteBuf.readableBytes()];

        // Store current reader and writer indices
        int readerIndex = byteBuf.readerIndex();
        int writerIndex = byteBuf.writerIndex();

        // Copy the data into the new array
        byteBuf.getBytes(readerIndex, bytes);

        // Restore the original indices to avoid affecting the buffer state
        byteBuf.setIndex(readerIndex, writerIndex);

        return bytes;
    }

    public <T extends PacketListener> void handleRawPacket(byte[] bytes, Class<T> listenerClass, SteamID lastSender)
    {
        this.lastSender = lastSender;

        if (bytes.length == 0)
            return;

        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        int packetId = buffer.readInt();
//        LOGGER.fine("Handling packet: " + Registries.PACKET.getPacketKeyByIntKey(packetId));

        if (packetId == 0)
            return;

        T packetListener = (T) listeners.get(listenerClass);
        if (packetListener == null)
        {
            throw new RuntimeException("Packet Listener for " + listenerClass + " not found!");
        }

        var packetCodec = Registries.PACKET.getPacketFromIntKey(packetId);
        Packet<?, T> decode = (Packet<?, T>) packetCodec.decode(buffer);
        decode.handlePacket(packetListener);
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
