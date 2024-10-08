package steve6472.orbiter.network.test;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ECHOServer
{
    private DatagramSocket socket;

    public ECHOServer(int port)
    {
        // Create a DatagramSocket on the specified port
        try
        {
            socket = new DatagramSocket(port);
        } catch (SocketException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void listen()
    {
        try
        {
            byte[] receiveData = new byte[4096];

            // Receive a packet from the client
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);

            // Process the received packet
            processReceivedPacket(receivePacket);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void processReceivedPacket(DatagramPacket receivePacket) throws IOException
    {
        // Extract message and client's address/port
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        // Echo the message back to the client
        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), clientAddress, clientPort);
        socket.send(sendPacket);
    }

    public void stop()
    {
        if (socket != null && !socket.isClosed())
        {
            socket.close();
        }
    }
}