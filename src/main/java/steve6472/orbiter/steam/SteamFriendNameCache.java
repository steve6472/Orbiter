package steve6472.orbiter.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class SteamFriendNameCache
{
    private final Map<SteamID, String> names = new HashMap<>();
    private final SteamFriends steamFriends;

    public SteamFriendNameCache(SteamFriends steamFriends, SteamID appUser)
    {
        this.steamFriends = steamFriends;
        initList();
        addUser(appUser, steamFriends.getFriendPersonaName(appUser));
    }

    private void initList()
    {
        int friendCount = steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
        for (int i = 0; i < friendCount; i++)
        {
            SteamID friendByIndex = steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
            String friendPersonaName = steamFriends.getFriendPersonaName(friendByIndex);
            addUser(friendByIndex, friendPersonaName);
        }
    }

    public String getUserName(SteamID user)
    {
        String name = names.get(user);
        if (name == null)
            return "*null*";
        else if (name.isBlank())
            return "*blank_name*";
        return name;
    }

    void updateName(SteamID user, String newName)
    {
        names.replace(user, newName);
    }

    void removeUser(SteamID user)
    {
        names.remove(user);
    }

    void addUser(SteamID user, String name)
    {
        names.put(user, name);
    }
}
