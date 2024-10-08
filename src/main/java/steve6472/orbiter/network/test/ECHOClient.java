package steve6472.orbiter.network.test;

import java.io.IOException;
import java.net.*;

public class ECHOClient
{
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public ECHOClient(String serverAddress, int serverPort)
    {
        // Create the socket
        try
        {
            socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(serverAddress);
        } catch (SocketException | UnknownHostException e)
        {
            throw new RuntimeException(e);
        }
        this.serverPort = serverPort;
    }

    public void sendMessage(byte[] message)
    {
        // Send the message to the server
        try
        {
            DatagramPacket sendPacket = new DatagramPacket(message, message.length, serverAddress, serverPort);
            socket.send(sendPacket);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public byte[] receiveMessage()
    {
        // Receive the echoed message from the server
        try
        {
            byte[] receiveData = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            return receiveData;

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void stop()
    {
        if (socket != null && !socket.isClosed())
        {
            socket.close();
        }
    }

    /*public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        ECHOClient client = null;

        try
        {
            client = new ECHOClient("localhost", 9876); // Connect to the server on localhost at port 9876

            while (true)
            {
                LOGGER.fine("Enter message (or 'exit' to quit): ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit"))
                {
                    break;
                }

                // Send the message to the server
                client.sendMessage(message);

                // Receive and display the echoed message
                String echoedMessage = client.receiveMessage();
                LOGGER.fine("Received from server: " + echoedMessage);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (client != null)
            {
                client.stop();
            }
            scanner.close();
        }
    }*/
}