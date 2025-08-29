package steve6472.orbiter.world.ecs.components.emitter;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.OrlangValue;
import steve6472.orbiter.orlang.VarContext;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.ecs.components.emitter.rate.EmitterRate;
import steve6472.orbiter.world.ecs.components.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.ecs.components.emitter.shapes.PointShape;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;

/**
 * Totally not stolen from <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/particlesreference/particlecomponentlist?view=minecraft-bedrock-stable">Microsoft</a>
 */
public class ParticleEmitter implements Component
{
    public static final Codec<ParticleEmitter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrVec3.CODEC.optionalFieldOf("offset", new OrVec3()).forGetter(o -> o.offset),
        EmitterShape.CODEC.optionalFieldOf("shape", PointShape.INSTANCE).forGetter(o -> o.shape),
        EmitterLifetime.CODEC.fieldOf("lifetime").forGetter(o -> o.lifetime),
        EmitterRate.CODEC.fieldOf("rate").forGetter(o -> o.rate),
        ParticleBlueprint.REGISTRY_OR_INLINE_CODEC.fieldOf("particle").forGetter(o -> o.particleData)
    ).apply(instance, (offset, shape, lifetime, rate, particleData) -> {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.offset = offset;
        emitter.shape = shape;
        emitter.lifetime = lifetime;
        emitter.rate = rate;
        emitter.particleData = particleData;
        return emitter;
    }));

    public int emitterAge;
    public OrVec3 offset;

    public EmitterShape shape;
    public EmitterLifetime lifetime;
    public EmitterRate rate;
    public OrlangEnvironment environment;

    public Holder<ParticleBlueprint> particleData;

//    @ApiStatus.Internal
//    public Set<Entity> trackedParticles = new HashSet<>();

    public ParticleEmitter()
    {
        environment = new OrlangEnvironment();
    }

    @Override
    public String toString()
    {
        //noinspection StringBufferReplaceableByString
        final StringBuilder sb = new StringBuilder("ParticleEmitter{");
        sb.append("emitterAge=").append(emitterAge).append('\n');
        sb.append(", offset=").append(offset).append('\n');
        sb.append(", shape=").append(shape).append('\n');
        sb.append(", lifetime=").append(lifetime).append('\n');
        sb.append(", rate=").append(rate).append('\n');
        sb.append(", particleData=").append(particleData).append('\n');
//        sb.append(", trackedParticles=").append(trackedParticles.size()).append('\n');
        sb.append('}');
        return sb.toString();
    }

    private static final AST.Node.Identifier EMITTER_AGE = new AST.Node.Identifier(VarContext.VARIABLE, "emitter_age");

    public void updateEnvironment()
    {
        environment.setValue(EMITTER_AGE, OrlangValue.num(emitterAge));
    }
}
