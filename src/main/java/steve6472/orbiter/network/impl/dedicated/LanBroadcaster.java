package steve6472.orbiter.network.impl.dedicated;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import steve6472.core.log.Log;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.network.ExtraBufferCodecs;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/21/2025
 * Project: Orbiter <br>
 */
public class LanBroadcaster implements Runnable
{
    private static final Logger LOGGER = Log.getLogger(LanBroadcaster.class);

    public static final int PORT = 4447;
    private static final long BROADCAST_PERIOD = 2000;
    private static final InetAddress BROADCAST_ADDRESS = createAddress();

    private ScheduledExecutorService executor;
    private DatagramSocket socket;
    private boolean running;
    private final Supplier<String> motd;
    private ScheduledFuture<?> future;

    public LanBroadcaster(Supplier<String> motd)
    {
        this.motd = motd;
    }

    private static DatagramSocket createSocket()
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket();
            socket.setSoTimeout(4000);
        } catch (SocketException e)
        {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return socket;
    }

    static InetAddress createAddress()
    {
        try
        {
            return InetAddress.getByName("224.0.2.60");
        } catch (UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    public void start()
    {
        LOGGER.info("Starting LAN Broadcaster");
        executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "LanBroadcasterExecutor"));
        socket = createSocket();
        running = true;
        future = executor.scheduleAtFixedRate(this, 0, BROADCAST_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void shutdown()
    {
        if (!running)
            return;

        LOGGER.info("Shutting down LAN Broadcaster");
        running = false;
        try
        {
            executor.shutdown();
            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            socket.close();
            LOGGER.info("Closed socket");
        }
    }

    @Override
    public void run()
    {
        if (!running)
        {
            future.cancel(true);
            return;
        }

        try
        {
            final byte[] packetBytes = getPacket();
            final DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length, BROADCAST_ADDRESS, PORT);
            socket.send(packet);
        } catch (IOException e)
        {
            if (this.future != null)
            {
                this.future.cancel(true);
            }
        }
    }

    private byte[] getPacket()
    {
        BroadcastPacket broadcastPacket = new BroadcastPacket(motd.get(), 50000);
        ByteBuf buffer = Unpooled.buffer(64);
        BroadcastPacket.BUFFER_CODEC.encode(buffer, broadcastPacket);

        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(0, bytes);

        return bytes;
    }

    public record BroadcastPacket(String motd, int port)
    {
        public static final BufferCodec<ByteBuf, BroadcastPacket> BUFFER_CODEC = BufferCodec.of(
            BufferCodecs.stringUTF8(64), BroadcastPacket::motd,
            ExtraBufferCodecs.VAR_INT, BroadcastPacket::port,
            BroadcastPacket::new
        );
    }
}
