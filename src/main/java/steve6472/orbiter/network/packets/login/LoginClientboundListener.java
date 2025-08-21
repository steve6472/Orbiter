package steve6472.orbiter.network.packets.login;

import steve6472.core.log.Log;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.UserStage;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class LoginClientboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(LoginClientboundListener.class);

    public void loginResponse(boolean accepted, String hostUsername)
    {
        if (accepted)
        {
            sender().updateUsername(hostUsername);
            LOGGER.info("Connected to host: " + hostUsername);
            sender().changeUserStage(UserStage.CONFIGURATION_CLIENTBOUND);
        } else
        {
            LOGGER.info("Connection refused, reason: " + hostUsername);
            network().lobby().closeLobby();
        }
    }
}
