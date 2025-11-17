package steve6472.orbiter.network;

import steve6472.orbiter.network.api.NetworkMain;

/**
 * Created by steve6472
 * Date: 11/16/2025
 * Project: Orbiter <br>
 */
public class NetworkTicker
{
    private NetworkMain network;
    public TickResponsibility tickResponsibility = TickResponsibility.FRAME;

    public NetworkTicker()
    {

    }

    public void setNetwork(NetworkMain network)
    {
        this.network = network;
        this.network.setup();
    }

    public NetworkMain getNetwork()
    {
        return network;
    }

    public void shutdown()
    {
        if (network != null)
            network.shutdown();
    }

    public void frameTick()
    {
        if (tickResponsibility != TickResponsibility.FRAME)
            return;
        network.tick();
    }

    public void worldTick()
    {
        if (tickResponsibility != TickResponsibility.WORLD)
            return;
        network.tick();
    }

    public enum TickResponsibility
    {
        FRAME, WORLD
    }
}
