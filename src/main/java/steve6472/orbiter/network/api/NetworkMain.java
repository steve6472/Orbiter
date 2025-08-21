package steve6472.orbiter.network.api;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public interface NetworkMain
{
    void setup();
    void tick();
    void shutdown();

    Connections connections();
    PacketManager packetManager();
    Lobby lobby();
}
