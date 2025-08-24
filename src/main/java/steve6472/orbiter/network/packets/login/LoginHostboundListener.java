package steve6472.orbiter.network.packets.login;

import steve6472.core.log.Log;
import steve6472.flare.settings.VisualSettings;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.ConnectedUser;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.api.UserStage;
import steve6472.orbiter.network.impl.dedicated.DedicatedLobby;
import steve6472.orbiter.network.impl.dedicated.DedicatedMain;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;
import steve6472.orbiter.network.packets.configuration.clientbound.FinishConfiguration;
import steve6472.orbiter.network.packets.login.clientbound.LoginResponse;
import steve6472.orbiter.network.packets.login.hostbound.LoginStart;
import steve6472.orbiter.world.World;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
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
        UUID uuid = start.uuid();

        // Reject if user with this name already exists
        for (ConnectedUser connectedUser : network().lobby().getConnectedUsers())
        {
            if (connectedUser.user().uuid().equals(uuid))
            {
                ByteBuffer dataPacket = network()
                    .packetManager()
                    .createDataPacket(new LoginResponse(false, "Duplicate UUID"));
                try
                {
                    LOGGER.info("Refused connection from: " + username + " Reason: duplicated UUID");
                    ((DedicatedLobby) network().lobby()).getChannel().send(dataPacket, ((DedicatedUser) sender()).getUserConnection().getPeerAddress());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                return;
            }
        }

        User sender = new DedicatedUser(uuid, ((DedicatedUser) sender()).getUserConnection());
        sender.updateUsername(username);

        // If user was allowed to join:
        LOGGER.info("Accepted connection from: " + username);
        network().lobby().joinUser(sender);
        network().connections().sendPacket(sender, new LoginResponse(true, VisualSettings.USERNAME.get()));
        sender.changeUserStage(UserStage.CONFIGURATION);

        // TODO: because we currently do not have any configuration, switch right to play

        connections().sendPacket(sender, FinishConfiguration.instance());
        sender.changeUserStage(UserStage.PLAY);

        World world = OrbiterApp.getInstance().getClient().getWorld();

        if (world != null)
        {
            // TODO: this can NOT be cast here!
            ((DedicatedMain) network()).newPlayer(world, sender);
        }
    }
}
