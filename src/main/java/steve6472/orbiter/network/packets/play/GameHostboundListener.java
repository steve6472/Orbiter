package steve6472.orbiter.network.packets.play;

import steve6472.core.log.Log;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.User;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class GameHostboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(GameHostboundListener.class);

    public void heartbeat()
    {
//        LOGGER.info("<3 from " + sender());
    }

    public void disconnect()
    {
        network().lobby().kickUser(sender(), "Disconnected");
        LOGGER.info("User " + sender() + " disconnected!");
    }
}
