package steve6472.orbiter.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Type;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.actions.AddComponentGroups;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public final class PropertyType<T extends Property> extends Type<T>
{
    public static final PropertyType<PropertyInt> INT = register("int", PropertyInt.CODEC);
    public static final PropertyType<PropertyDouble> DOUBLE = register("double", PropertyDouble.CODEC);
    public static final PropertyType<PropertyEnum> ENUM = register("enum", PropertyEnum.CODEC);
    public static final PropertyType<PropertyString> STRING = register("string", PropertyString.CODEC);

    public PropertyType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends Property> PropertyType<T> register(String id, Codec<T> codec)
    {
        var obj = new PropertyType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.PROPERTY_TYPE.register(obj);
        return obj;
    }

    public static void bootstrap()
    {
    }
}
