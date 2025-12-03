package steve6472.orbiter.actions.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Type;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public final class FilterType<T extends Filter> extends Type<T>
{
    public static final FilterType<Empty> EMPTY = register("empty", Empty.CODEC);
    public static final FilterType<AllOf> ALL_OF = register("all_of", AllOf.CODEC);
    public static final FilterType<HasProperties> HAS_PROPERTIES = register("has_properties", HasProperties.CODEC);
    public static final FilterType<TestProperties> TEST_PROPERTIES = register("test_properties", TestProperties.CODEC);
    public static final FilterType<ShapeSphere> SHAPE_SPHERE = register("shape_sphere", ShapeSphere.CODEC);
    public static final FilterType<ShapeCuboid> SHAPE_CUBOID = register("shape_cuboid", ShapeCuboid.CODEC);

    public FilterType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends Filter> FilterType<T> register(String id, Codec<T> codec)
    {
        var obj = new FilterType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.FILTER.register(obj);
        return obj;
    }

    public static void bootstrap()
    {
    }
}
