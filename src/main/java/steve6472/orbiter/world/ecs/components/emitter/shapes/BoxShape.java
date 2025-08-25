package steve6472.orbiter.world.ecs.components.emitter.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.core.util.ExtraCodecs;
import steve6472.core.util.RandomUtil;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class BoxShape extends EmitterShape
{
    public static final Codec<BoxShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.VEC_3F.fieldOf("half_dimensions").forGetter(BoxShape::halfDimensions),
        Codec.BOOL.optionalFieldOf("surface_only", false).forGetter((BoxShape o) -> o.surfaceOnly)
    ).apply(instance, BoxShape::new));

    private final Vector3f halfDimensions;

    // TODO: make this work
    public boolean surfaceOnly;

    private BoxShape(Vector3f halfDimensions, boolean surfaceOnly)
    {
        this.halfDimensions = halfDimensions;
        this.surfaceOnly = surfaceOnly;
    }

    public Vector3f halfDimensions()
    {
        return halfDimensions;
    }

    @Override
    public Vector3f createPosition(ParticleEmitter emitter)
    {
        return new Vector3f(
            RandomUtil.randomFloat(-halfDimensions.x, halfDimensions.x),
            RandomUtil.randomFloat(-halfDimensions.y, halfDimensions.y),
            RandomUtil.randomFloat(-halfDimensions.z, halfDimensions.z)
        );
    }

    @Override
    protected EmitterShapeType<?> getType()
    {
        return EmitterShapeType.BOX_SHAPE;
    }
}
