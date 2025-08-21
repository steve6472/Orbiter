package steve6472.orbiter.network.impl.dedicated;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import steve6472.core.log.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/21/2025
 * Project: Orbiter <br>
 */
public class LanDetector implements Runnable
{
    private static final Logger LOGGER = Log.getLogger(LanDetector.class);

    private static final long DETECT_PERIOD = 500;
    private ScheduledExecutorService executor;
    private MulticastSocket socket;
    private InetAddress pingGroup;
    private boolean running;
    private ScheduledFuture<?> future;

    public LanDetector()
    {
    }

    public boolean isRunning()
    {
        return running;
    }

    public void start()
    {
        LOGGER.info("Starting LAN Detector");
        executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "LanDetectorExecutor"));
        socket = createSocket();
        pingGroup = LanBroadcaster.createAddress();
        try
        {
            socket.setSoTimeout(6000);
            //noinspection deprecation
            socket.joinGroup(pingGroup);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        running = true;
        future = executor.scheduleAtFixedRate(this, 0, DETECT_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void shutdown()
    {
        if (!running)
            return;

        LOGGER.info("Shutting down LAN Detector");
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
            try
            {
                //noinspection deprecation
                this.socket.leaveGroup(this.pingGroup);
            } catch (IOException ignored)
            {}

            socket.close();
            LOGGER.info("Closed socket");
        }
    }

    private static MulticastSocket createSocket()
    {
        try
        {
            return new MulticastSocket(LanBroadcaster.PORT);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
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
            byte[] bs = new byte[512];
            DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length);
            socket.receive(datagramPacket);
            byte[] result = new byte[datagramPacket.getLength()];
            System.arraycopy(bs, datagramPacket.getOffset(), result, 0, datagramPacket.getLength());
            ByteBuf buffer = Unpooled.wrappedBuffer(result);
            LanBroadcaster.BroadcastPacket decode = LanBroadcaster.BroadcastPacket.BUFFER_CODEC.decode(buffer);
            System.out.println(decode);
        } catch (IOException ex)
        {
            if (this.future != null)
            {
                this.future.cancel(true);
            }
        }
    }
}
