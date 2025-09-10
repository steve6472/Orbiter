package steve6472.orbiter.world.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Type;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 5/4/2024
 * Project: Domin <br>
 */
public final class EmitterRateType<T extends EmitterRate> extends Type<T>
{
    public static final EmitterRateType<InstantRate> INSTANT_RATE = register("instant", InstantRate.CODEC);
    public static final EmitterRateType<SteadyRate> STEADY_RATE = register("steady", SteadyRate.CODEC);

    public EmitterRateType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends EmitterRate> EmitterRateType<T> register(String id, Codec<T> codec)
    {
        var obj = new EmitterRateType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.EMITTER_RATE.register(obj);
        return obj;
    }
}
