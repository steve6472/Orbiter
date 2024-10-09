package steve6472.orbiter.network.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class P2PNode
{
    private DatagramChannel channel;
    private Selector selector;
    private InetSocketAddress peerAddress;
    private int localPort;

    public Consumer<byte[]> readPacket;

    public P2PNode(String peerHost, int peerPort, int localPort) throws IOException
    {
        this.localPort = localPort;
        // Set up a non-blocking DatagramChannel and bind it to a local port
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(localPort));

        // Initialize peer address
        peerAddress = new InetSocketAddress(peerHost, peerPort);

        // Set up a selector to handle non-blocking I/O
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }

    public void sendPacket(ByteBuffer buffer)
    {
        try
        {
            channel.send(buffer, peerAddress);
//            System.out.println("Sent packet to " + peerAddress);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // This method can be called to read incoming data from the peer

    // Non-blocking listen method (to be called from your tick method)
    public void listen() throws IOException
    {
        // Non-blocking selectNow() to check for ready channels
        int readyChannels = selector.selectNow(); // Returns immediately

        if (readyChannels == 0)
        {
            // No channels are ready, return immediately
            return;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
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

    // Receive data from the channel
    private void receiveData() throws IOException
    {
        ByteBuffer buffer = ByteBuffer.allocate(8192); // Adjust size as needed
        channel.receive(buffer);
        buffer.flip();

        byte[] receivedData = new byte[buffer.remaining()];
        buffer.get(receivedData);

//        System.out.println("Received data from " + senderAddress + " from " + localPort + " " + Arrays.toString(receivedData));
        // Here you can call your readPacket() method and handle the data
        readPacket.accept(receivedData); // Implement this to process the received data
    }

    // Close the channel and selector when done
    public void close() throws IOException
    {
        channel.close();
        selector.close();
    }
}