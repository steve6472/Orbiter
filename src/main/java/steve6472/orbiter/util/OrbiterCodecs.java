package steve6472.orbiter.util;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class OrbiterCodecs
{
    public static final Codec<SteamID> STEAM_ID = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("native").forGetter(SteamNativeHandle::getNativeHandle)
    ).apply(instance, SteamID::createFromNativeHandle));
}
