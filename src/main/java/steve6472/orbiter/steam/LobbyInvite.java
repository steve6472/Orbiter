package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamID;
import steve6472.orbiter.steam.lobby.Lobby;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public record LobbyInvite(SteamID invitee, Lobby lobby)
{
}
