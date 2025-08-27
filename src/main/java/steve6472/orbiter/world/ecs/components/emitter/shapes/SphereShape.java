package steve6472.orbiter.world.ecs.components.emitter.shapes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class SphereShape extends EmitterShape
{
    public static final Codec<SphereShape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("radius").forGetter((SphereShape o) -> o.radius),
        Codec.BOOL.optionalFieldOf("surface_only", false).forGetter((SphereShape o) -> o.surfaceOnly)
    ).apply(instance, SphereShape::new));

    public OrNumValue radius;
    public boolean surfaceOnly;

    private SphereShape(OrNumValue radius, boolean surfaceOnly)
    {
        this.radius = radius;
        this.surfaceOnly = surfaceOnly;
    }

    public float radius(OrlangEnvironment environment)
    {
        radius.evaluate(environment);
        return radius.fget();
    }

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
            return new Vector3f((float) x, (float) y, (float) z).normalize().mul(radius(emitter.environment));
        } else
        {
            return new Vector3f((float) x, (float) y, (float) z).mul(radius(emitter.environment));
        }
    }

    @Override
    protected EmitterShapeType<?> getType()
    {
        return EmitterShapeType.SPHERE_SHAPE;
    }
}
