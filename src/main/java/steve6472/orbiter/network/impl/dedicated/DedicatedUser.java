package steve6472.orbiter.network.impl.dedicated;

import steve6472.core.log.Log;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.api.UserStage;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public class DedicatedUser implements User
{
    private static final Logger LOGGER = Log.getLogger(DedicatedUser.class);
    private final UUID uuid;
    private String username = "unknown";
    final DedicatedUserConnection userConnection;
    private UserStage userStage = UserStage.UNSET;

    public DedicatedUser(UUID uuid, DedicatedUserConnection userConnection)
    {
        this.uuid = uuid;
        this.userConnection = userConnection;
    }

    public DedicatedUser(UUID uuid, String username)
    {
        this.uuid = uuid;
        this.userConnection = null;
        this.username = username;
    }

    public DedicatedUserConnection getUserConnection()
    {
        return userConnection;
    }

    @Override
    public UUID uuid()
    {
        return uuid;
    }

    @Override
    public String username()
    {
        return username;
    }

    @Override
    public void updateUsername(String newUsername)
    {
        this.username = newUsername;
    }

    @Override
    public UserStage getUserStage()
    {
        return userStage;
    }

    @Override
    public void changeUserStage(UserStage newStage)
    {
        this.userStage = newStage;
        LOGGER.info("Changed " + (OrbiterApp.getInstance().getNetwork().lobby().isHost() ? "Clients " + username : " Host ") + "Stage to " + newStage);
    }

    @Override
    public String toString()
    {
        return "DedicatedUser{" + "username='" + username + '\'' + ", userStage=" + userStage + ", uuid=" + uuid + '}';
    }
}
