package steve6472.orbiter.network.packets.login;

import steve6472.core.log.Log;
import steve6472.flare.settings.VisualSettings;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.ConnectedUser;
import steve6472.orbiter.network.api.UserStage;
import steve6472.orbiter.network.impl.dedicated.DedicatedLobby;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;
import steve6472.orbiter.network.packets.configuration.clientbound.FinishConfiguration;
import steve6472.orbiter.network.packets.login.clientbound.LoginResponse;
import steve6472.orbiter.network.packets.login.hostbound.LoginStart;
import steve6472.orbiter.network.packets.play.clientbound.EnterWorld;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class LoginHostboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(LoginHostboundListener.class);

    public void loginStart(LoginStart start)
    {
        String username = start.username();

        // Reject if user with this name already exists
        for (ConnectedUser connectedUser : network().lobby().getConnectedUsers())
        {
            if (connectedUser.user().username().equals(username))
            {
                ByteBuffer dataPacket = network()
                    .packetManager()
                    .createDataPacket(new LoginResponse(false, "Duplicate username"));
                try
                {
                    LOGGER.info("Refused connection from: " + username + " Reason: duplicated username");
                    ((DedicatedLobby) network().lobby()).getChannel().send(dataPacket, ((DedicatedUser) sender()).getUserConnection()
                        .getPeerAddress());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                return;
            }
        }

        sender().updateUsername(username);

        // If user was allowed to join:
        LOGGER.info("Accepted connection from: " + username);
        network().lobby().joinUser(sender());
        network().connections().sendPacket(sender(), new LoginResponse(true, VisualSettings.USERNAME.get()));
        sender().changeUserStage(UserStage.CONFIGURATION);

        // TODO: because we currently do not have any configuration, switch right to play

        network().connections().sendPacket(sender(), FinishConfiguration.instance());
        sender().changeUserStage(UserStage.PLAY);

        if (OrbiterApp.getInstance().getClient().getWorld() != null)
            network().connections().sendPacket(sender(), new EnterWorld());
    }
}
