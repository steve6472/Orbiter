package steve6472.orbiter.network.test;

import steve6472.core.network.PacketListener;
import steve6472.orbiter.network.PacketManager;
import steve6472.orbiter.network.packets.lobby.SimpleTestMessage;

import java.nio.ByteBuffer;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class PacketTest
{
    PacketManager packetManager;
    public ECHOServer echoServer;
    public ECHOClient echoClient;

    public PacketTest()
    {
        packetManager = new PacketManager();
        packetManager.registerListener(new TestListener());

        echoServer = new ECHOServer(9876);
        echoClient = new ECHOClient("localhost", 9876);
    }

    int tickTime = 0;

    public void tick()
    {
        ByteBuffer dataPacket = packetManager.createDataPacket(new SimpleTestMessage("Tick called: " + tickTime + " times."));

        byte[] bytes = new byte[dataPacket.limit()];
        dataPacket.get(bytes);

        echoClient.sendMessage(bytes);

        echoServer.listen();

        bytes = echoClient.receiveMessage();
        packetManager.handleRawPacket(bytes, TestListener.class, null);

        tickTime++;
    }

    public static class TestListener implements PacketListener
    {
        public void simpleMessage(String message)
        {
            System.out.println(message);
        }
    }
}
