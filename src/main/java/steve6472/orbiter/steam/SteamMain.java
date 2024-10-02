package steve6472.orbiter.steam;

import com.codedisaster.steamworks.*;
import steve6472.core.log.Log;
import steve6472.orbiter.settings.Keybinds;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class SteamMain
{
    private static final Logger LOGGER = Log.getLogger(SteamMain.class);
    private boolean enabled;

    private SteamFriends steamFriends;
    private SteamNetworking steamNetworking;

    public void setup()
    {
        try
        {
            SteamAPI.loadLibraries();
            enabled = SteamAPI.init();
            if (!enabled)
                LOGGER.warning("Failed to start SteamAPI");
            else
                LOGGER.info("Started SteamAPI");
        } catch (SteamException e)
        {
            LOGGER.log(Level.WARNING, e, () -> "Error");
        }

        if (!enabled) return;
        steamFriends = new SteamFriends(new OrbiterSteamFriends());
        steamNetworking = new SteamNetworking(new OrbiterSteamNetworking());
    }

    public void tick()
    {
        if (!enabled) return;

        runCallbacks();
        if (Keybinds.TEST.isActive())
        {

        }
    }

    private void runCallbacks()
    {
        if (SteamAPI.isSteamRunning())
        {
            SteamAPI.runCallbacks();
        }
    }

    private SteamID findFriendByName(String name)
    {
        int friendCount = steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
        for (int i = 0; i < friendCount; i++)
        {
            SteamID friendByIndex = steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
            if (steamFriends.getFriendPersonaName(friendByIndex).equals(name))
                return friendByIndex;
        }

        return null;
    }

    public void listFriends()
    {
    }

    public void shutdown()
    {
        if (!enabled) return;

        SteamAPI.shutdown();
    }
}
