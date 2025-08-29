package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.ObjectRegistry;
import steve6472.orbiter.Constants;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class Holder<T extends Keyable>
{
    private final Key key;
    private final ObjectRegistry<T> registry;
    T value;

    private Holder(Key key, ObjectRegistry<T> registry)
    {
        this.key = key;
        this.registry = registry;
    }

    public T get()
    {
        if (value == null)
        {
            value = registry.get(key);
            if (value == null)
                throw new NullPointerException("Entry for '%s' in registry '%s' not found".formatted(key, registry.getRegistryKey()));
        }
        return value;
    }

    public Key key()
    {
        return key;
    }

    public static <T extends Keyable> Codec<Holder<T>> create(ObjectRegistry<T> registry)
    {
        return Constants.KEY_CODEC.flatXmap(key -> DataResult.success(new Holder<>(key, registry)), object -> DataResult.success(object.key()));
    }
}
