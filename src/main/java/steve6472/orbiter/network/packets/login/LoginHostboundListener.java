package steve6472.orbiter.network.packets.login;

import com.badlogic.ashley.core.Entity;
import steve6472.core.log.Log;
import steve6472.flare.settings.VisualSettings;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.ConnectedUser;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.api.UserStage;
import steve6472.orbiter.network.impl.dedicated.DedicatedLobby;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;
import steve6472.orbiter.network.packets.configuration.clientbound.FinishConfiguration;
import steve6472.orbiter.network.packets.login.clientbound.LoginResponse;
import steve6472.orbiter.network.packets.login.hostbound.LoginStart;
import steve6472.orbiter.network.packets.play.clientbound.CreateCustomEntity;
import steve6472.orbiter.network.packets.play.clientbound.EnterWorld;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.physics.AngularFactor;

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
            connections().sendPacket(sender, new EnterWorld());

            for (Entity entity : world.ecsEngine().getEntities())
            {
                connections().sendPacket(sender, new CreateCustomEntity(entity));
            }

            Entity playerEntity = world.addEntity(Registries.ENTITY_BLUEPRINT.get(Constants.key("client_player")), uuid, false);
            world.addComponent(playerEntity, new MPControlled(sender));
            world.addComponent(playerEntity, new AngularFactor(0f, 0f, 0f));

            for (ConnectedUser connectedUser : network().lobby().getConnectedUsers())
            {
                if (connectedUser.user().equals(sender))
                    continue;
                connections().sendPacket(connectedUser.user(), new CreateCustomEntity(playerEntity));
            }
        }
    }
}
