package steve6472.orbiter.network.api;

import java.util.List;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public interface Lobby
{
    Connections connections();
    Map<String, String> getLobbyData();

    List<ConnectedUser> getConnectedUsers();
    void joinUser(User user);
    void kickUser(User userToKick, String reason);

    boolean isHost();
    boolean isLobbyOpen();
    void openLobby(int port, boolean asHost);

    void closeLobby();
}
