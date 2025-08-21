package steve6472.orbiter.network.impl.dedicated;

import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.api.UserStage;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public class DedicatedUser implements User
{
    private String username = "unknown";
    final DedicatedUserConnection userConnection;
    private UserStage userStage = UserStage.UNSET;

    public DedicatedUser(DedicatedUserConnection userConnection)
    {
        this.userConnection = userConnection;
    }

    public DedicatedUser(String username)
    {
        this.userConnection = null;
        this.username = username;
    }

    public DedicatedUserConnection getUserConnection()
    {
        return userConnection;
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
    }

    @Override
    public String toString()
    {
        return "DedicatedUser{" + "username='" + username + '\'' + ", userStage=" + userStage + '}';
    }
}
