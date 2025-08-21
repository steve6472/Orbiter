package steve6472.orbiter.util;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class OrbiterCodecs
{
    /// @deprecated WIP
    @Deprecated
    public static final Codec<SteamID> STEAM_USER = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("native").forGetter(SteamNativeHandle::getNativeHandle)
    ).apply(instance, SteamID::createFromNativeHandle));

    public static final Codec<DedicatedUser> DEDICATED_USER = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("username").forGetter(User::username)
    ).apply(instance, DedicatedUser::new));

    /// TODO: add Steam User
    public static final Codec<User> USER = DEDICATED_USER.xmap(u -> u, u -> (DedicatedUser) u);
}
