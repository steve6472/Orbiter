package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.*;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.Orlang;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitters;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.LoopingLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.OnceLifetime;
import steve6472.orbiter.world.ecs.components.emitter.rate.EmitterRate;
import steve6472.orbiter.world.ecs.components.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleEmittersBlueprint(List<Emitter> emitters) implements Blueprint<ParticleEmittersBlueprint>
{
    private record Emitter(OrVec3 offset, EmitterShape shape, EmitterLifetime lifetime, EmitterRate rate, Holder<ParticleBlueprint> particle, Optional<ParticleEmitter.EnvData> envData)
    {
        public static final Codec<Emitter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OrVec3.CODEC.optionalFieldOf("offset", new OrVec3()).forGetter(Emitter::offset),
            EmitterShape.CODEC.fieldOf("shape").forGetter(Emitter::shape),
            EmitterLifetime.CODEC.fieldOf("lifetime").forGetter(Emitter::lifetime),
            EmitterRate.CODEC.fieldOf("rate").forGetter(Emitter::rate),
            ParticleBlueprint.REGISTRY_OR_INLINE_CODEC.fieldOf("particle").forGetter(Emitter::particle),
            ParticleEmitter.EnvData.CODEC.optionalFieldOf("environment").forGetter(Emitter::envData)
        ).apply(instance, Emitter::new));
    }

    public static final Key KEY = Constants.key("particle_emitters");
    public static final Codec<ParticleEmittersBlueprint> CODEC = Emitter.CODEC.listOf().xmap(ParticleEmittersBlueprint::new, ParticleEmittersBlueprint::emitters);

    @Override
    public List<Component> createComponents()
    {
        List<ParticleEmitter> emitterList = new ArrayList<>(emitters.size());
        for (Emitter emitterBl : emitters)
        {
            ParticleEmitter emitter = new ParticleEmitter();
            emitter.offset = emitterBl.offset.copy();
            emitter.shape = emitterBl.shape;
            emitter.rate = emitterBl.rate;
            // Because this object is mutable...
            emitter.lifetime = switch (emitterBl.lifetime)
            {
                case OnceLifetime once -> new OnceLifetime(once.activeTime());
                case LoopingLifetime looping -> new LoopingLifetime(looping.activeTime, looping.sleepTime, looping.maxLoops);
                default -> throw new IllegalStateException("Unexpected value: " + emitterBl.lifetime);
            };
            emitterBl.envData.ifPresent(envData ->
            {
                emitter.environmentData = envData;
                envData.init().ifPresent(code -> {
                    Orlang.interpreter.interpret(code, emitter.environment);
                });
            });
            emitter.particleData = emitterBl.particle;
            emitterList.add(emitter);
        }
        return List.of(new ParticleEmitters(emitterList));
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleEmittersBlueprint> codec()
    {
        return CODEC;
    }
}
