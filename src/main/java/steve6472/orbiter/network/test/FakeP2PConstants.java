package steve6472.orbiter.network.test;

import com.codedisaster.steamworks.SteamID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class FakeP2PConstants
{
    public static final SteamID USER_ID = SteamID.createFromNativeHandle(42);
    public static final SteamID FAKE_PEER = SteamID.createFromNativeHandle(7);
    public static final SteamID LOBBY_ID = SteamID.createFromNativeHandle(777);

    public static final int SERVER_PORT = 8888;
    public static final int PEER_PORT = 9999;
}
