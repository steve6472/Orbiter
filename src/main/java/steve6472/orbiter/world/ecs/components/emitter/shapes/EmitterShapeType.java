package steve6472.orbiter.world.ecs.components.emitter.shapes;

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
public final class EmitterShapeType<T extends EmitterShape> extends Type<T>
{
    public static final EmitterShapeType<BoxShape> BOX_SHAPE = register("box", BoxShape.CODEC);
    public static final EmitterShapeType<SphereShape> SPHERE_SHAPE = register("sphere", SphereShape.CODEC);
    public static final EmitterShapeType<PointShape> POINT_SHAPE = register("point", PointShape.CODEC);

    public EmitterShapeType(Key key, MapCodec<T> codec)
    {
        super(key, codec);
    }

    private static <T extends EmitterShape> EmitterShapeType<T> register(String id, Codec<T> codec)
    {
        var obj = new EmitterShapeType<>(Constants.key(id), MapCodec.assumeMapUnsafe(codec));
        Registries.EMITTER_SHAPE.register(obj);
        return obj;
    }
}
