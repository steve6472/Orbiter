package steve6472.orbiter.world.ecs.components.emitter.shapes;

import com.mojang.serialization.Codec;
import org.joml.Vector3f;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class PointShape extends EmitterShape
{
    public static final PointShape INSTANCE = new PointShape();
    public static final Codec<PointShape> CODEC = Codec.unit(INSTANCE);

    private PointShape() { }

    @Override
    public Vector3f createPosition(ParticleEmitter emitter)
    {
        return new Vector3f(0, 0, 0);
    }

    @Override
    protected EmitterShapeType<?> getType()
    {
        return EmitterShapeType.POINT_SHAPE;
    }

    @Override
    public String toString()
    {
        return "PointShape{}";
    }
}
