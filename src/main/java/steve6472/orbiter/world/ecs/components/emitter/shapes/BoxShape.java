package steve6472.orbiter.world.ecs.components.emitter.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.core.util.RandomUtil;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class BoxShape extends EmitterShape
{
    public static final Codec<BoxShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrVec3.CODEC.fieldOf("half_dimensions").forGetter(BoxShape::halfDimensions),
        Codec.BOOL.optionalFieldOf("surface_only", false).forGetter((BoxShape o) -> o.surfaceOnly)
    ).apply(instance, BoxShape::new));

    private final OrVec3 halfDimensions;

    // TODO: make this work
    public boolean surfaceOnly;

    private BoxShape(OrVec3 halfDimensions, boolean surfaceOnly)
    {
        this.halfDimensions = halfDimensions;
        this.surfaceOnly = surfaceOnly;
    }

    public OrVec3 halfDimensions()
    {
        return halfDimensions;
    }

    @Override
    public Vector3f createPosition(ParticleEmitter emitter)
    {
        halfDimensions.evaluate(emitter.environment);
        float x = halfDimensions.fx();
        float y = halfDimensions.fy();
        float z = halfDimensions.fz();
        return new Vector3f(
            RandomUtil.randomFloat(-x, x),
            RandomUtil.randomFloat(-y, y),
            RandomUtil.randomFloat(-z, z)
        );
    }

    @Override
    protected EmitterShapeType<?> getType()
    {
        return EmitterShapeType.BOX_SHAPE;
    }
}
