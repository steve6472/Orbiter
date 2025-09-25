package steve6472.orbiter.world.emitter;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.emitter.rate.EmitterRate;
import steve6472.orbiter.world.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.emitter.shapes.PointShape;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;
import steve6472.orlang.*;
import steve6472.orlang.codec.OrCode;
import steve6472.orlang.codec.OrVec3;

import java.util.Map;
import java.util.Optional;

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
        ParticleBlueprint.REGISTRY_OR_INLINE_CODEC.fieldOf("particle").forGetter(o -> o.particleData),
        EnvData.CODEC.optionalFieldOf("environment").forGetter(o -> Optional.ofNullable(o.environmentData))
    ).apply(instance, (offset, shape, lifetime, rate, particleData, env) -> {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.offset = offset;
        emitter.shape = shape;
        emitter.lifetime = lifetime;
        emitter.rate = rate;
        emitter.particleData = particleData;
        env.ifPresent(envData -> emitter.environmentData = envData);
        return emitter;
    }));

    public long lastEmitterTick;
    public OrVec3 offset;

    public EmitterShape shape;
    public EmitterLifetime lifetime;
    public EmitterRate rate;

    public EnvData environmentData;
    public OrlangEnvironment environment;

    public Holder<ParticleBlueprint> particleData;
    public String locator = null;

//    @ApiStatus.Internal
//    public Set<Entity> trackedParticles = new HashSet<>();

    public ParticleEmitter()
    {
        environment = new OrlangEnvironment();
    }

    private static final AST.Node.Identifier EMITTER_AGE = new AST.Node.Identifier(VarContext.VARIABLE, "emitter_age");

    public double calculateAge(long now)
    {
        return (now - lastEmitterTick) / 1e3d;
    }

    public void emitterTick()
    {
        // First set "constants"
        environment.setValue(EMITTER_AGE, OrlangValue.num(calculateAge(System.currentTimeMillis())));

        // Then evaluate expressions
        if (environmentData != null)
            environmentData.emitterTick.ifPresent(code -> Orlang.interpreter.interpret(code, environment));
    }

    public void particleTick()
    {
        if (environmentData != null)
        {
            environmentData.particleTick.ifPresent(code -> Orlang.interpreter.interpret(code, environment));
            environmentData.curves.ifPresent(curves -> curves.forEach((name, curve) -> curve.calculate(name, environment)));
        }
    }

    public record EnvData(Optional<OrCode> init, Optional<OrCode> emitterTick, Optional<OrCode> particleTick, Optional<Map<AST.Node.Identifier, Curve>> curves)
    {
        public static final Codec<EnvData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OrCode.CODEC.optionalFieldOf("init").forGetter(EnvData::init),
            OrCode.CODEC.optionalFieldOf("tick").forGetter(EnvData::emitterTick),
            OrCode.CODEC.optionalFieldOf("particle").forGetter(EnvData::particleTick),
            Codec.unboundedMap(AST.Node.Identifier.CODEC, Curve.CODEC).optionalFieldOf("curves").forGetter(EnvData::curves)
        ).apply(instance, EnvData::new));
    }
}
