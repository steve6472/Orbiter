package steve6472.orbiter.world.ecs.components.emitter.lifetime;

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
public final class EmitterLifetimeType<T extends EmitterLifetime> extends Type<T>
{
    public static final EmitterLifetimeType<LoopingLifetime> LOOPING_LIFETIME = register("looping", LoopingLifetime.CODEC);
    public static final EmitterLifetimeType<OnceLifetime> ONCE_LIFETIME = register("once", OnceLifetime.CODEC);

    public EmitterLifetimeType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends EmitterLifetime> EmitterLifetimeType<T> register(String id, Codec<T> codec)
    {
        var obj = new EmitterLifetimeType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.EMITTER_LIFETIME.register(obj);
        return obj;
    }
}
