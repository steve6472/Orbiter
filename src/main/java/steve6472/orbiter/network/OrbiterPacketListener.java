package steve6472.orbiter.network;

import steve6472.core.network.PacketListener;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.Connections;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.api.User;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public abstract class OrbiterPacketListener implements PacketListener
{
    // TODO: use DI from DedicatedMain lol
    protected NetworkMain network()
    {
        return OrbiterApp.getInstance().getNetwork();
    }

    protected User sender()
    {
        return network().packetManager().lastSender();
    }

    protected Connections connections()
    {
        return network().connections();
    }
}
