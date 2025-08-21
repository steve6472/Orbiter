package steve6472.orbiter.network.api;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public interface User
{
    // User info
    String username();
    void updateUsername(String newUsername);

    UserStage getUserStage();
    void changeUserStage(UserStage newStage);
}
