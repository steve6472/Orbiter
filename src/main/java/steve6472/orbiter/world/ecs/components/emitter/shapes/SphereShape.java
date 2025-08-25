package steve6472.orbiter.world.ecs.components.emitter.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class SphereShape extends EmitterShape
{
    public static final Codec<SphereShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("radius").forGetter((SphereShape o) -> o.radius),
        Codec.BOOL.optionalFieldOf("surface_only", false).forGetter((SphereShape o) -> o.surfaceOnly)
    ).apply(instance, SphereShape::new));

    private final float radius;

    public boolean surfaceOnly;

    private SphereShape(float radius, boolean surfaceOnly)
    {
        this.radius = radius;
        this.surfaceOnly = surfaceOnly;
    }

    public double radius() { return radius; }

    @Override
    public Vector3f createPosition(ParticleEmitter emitter)
    {
        // https://karthikkaranth.me/blog/generating-random-points-in-a-sphere/
        var u = Math.random();
        var v = Math.random();
        var theta = u * 2.0 * Math.PI;
        var phi = Math.acos(2.0 * v - 1.0);
        var r = Math.cbrt(Math.random());
        var sinTheta = Math.sin(theta);
        var cosTheta = Math.cos(theta);
        var sinPhi = Math.sin(phi);
        var cosPhi = Math.cos(phi);
        var x = r * sinPhi * cosTheta;
        var y = r * sinPhi * sinTheta;
        var z = r * cosPhi;

        if (surfaceOnly)
        {
            return new Vector3f((float) x, (float) y, (float) z).normalize().mul(radius);

        } else
        {
            return new Vector3f((float) x, (float) y, (float) z).mul(radius);
        }
    }

    @Override
    protected EmitterShapeType<?> getType()
    {
        return EmitterShapeType.SPHERE_SHAPE;
    }
}
