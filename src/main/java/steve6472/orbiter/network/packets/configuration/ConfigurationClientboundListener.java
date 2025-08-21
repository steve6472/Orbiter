package steve6472.orbiter.network.packets.configuration;

import steve6472.core.log.Log;
import steve6472.orbiter.network.OrbiterPacketListener;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class ConfigurationClientboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(ConfigurationClientboundListener.class);

    public void heartbeat()
    {
//        LOGGER.info("<3 from " + sender());
    }
}
