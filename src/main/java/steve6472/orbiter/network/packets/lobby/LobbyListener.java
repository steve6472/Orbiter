package steve6472.orbiter.network.packets.lobby;

import com.codedisaster.steamworks.SteamID;
import steve6472.core.log.Log;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.lobby.Lobby;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class LobbyListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(LobbyListener.class);

    private final Lobby lobby;
    private final SteamMain steamMain;

    public LobbyListener(Lobby lobby, SteamMain steamMain)
    {
        this.lobby = lobby;
        this.steamMain = steamMain;
    }

    public void kickUser(SteamID toKick)
    {
        LOGGER.finest("Recieved kickUser " + steamMain.friendNames.getUserName(toKick));

        if (!lobby.getLobbyOwner().equals(sender()))
        {
            LOGGER.warning(sender() + " tried to kick someone but they are not the owner");
            return;
        }

        if (steamMain.lobbyManager.currentLobby().getLobbyOwner().equals(toKick))
        {
            LOGGER.warning("Tried to kick owner?");
            return;
        }

        lobby._removeUser(toKick);

        if (toKick.equals(steamMain.userID))
        {
            steamMain.steamMatchmaking.leaveLobby(steamMain.lobbyManager.currentLobby().lobbyID());
            steamMain.lobbyManager.resetCurrentLobby();
        }

        if (lobby.isClosing)
        {
            steamMain.packetManager.unregisterListener(LobbyListener.class);
        }
    }

    public void lobbyClosing()
    {
        LOGGER.finest("Recieved lobbyClosing");
        if (!lobby.getLobbyOwner().equals(sender()))
        {
            LOGGER.warning(sender() + " tried to send lobby_closing packet but they are not the owner");
            return;
        }

        lobby.isClosing = true;
        steamMain.steamMatchmaking.leaveLobby(steamMain.lobbyManager.currentLobby().lobbyID());
        steamMain.lobbyManager.resetCurrentLobby();
    }

    public void helloWorld()
    {
        LOGGER.info("Got hello world from " + sender());
    }

    public void chatMessage(String message)
    {
        String friendPersonaName = sender().username();
//        Console.log(friendPersonaName + "> " + message, CommandSource.ResponseStyle.BLACK);
    }
}
