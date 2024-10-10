package steve6472.orbiter.world.ecs.core;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class BlueprintEntry<T extends Blueprint<?>> implements Keyable, Serializable<T>
{
    private final Key key;
    private final Codec<T> codec;

    public BlueprintEntry(Key key, Codec<T> codec)
    {
        this.key = key;
        this.codec = codec;
    }

    @Override
    public Key key()
    {
        return key;
    }

    @Override
    public Codec<T> codec()
    {
        return codec;
    }
}
