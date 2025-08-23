package steve6472.orbiter.network.api;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public interface User
{
    // User info
    UUID uuid();
    String username();
    void updateUsername(String newUsername);

    UserStage getUserStage();
    void changeUserStage(UserStage newStage);
}
