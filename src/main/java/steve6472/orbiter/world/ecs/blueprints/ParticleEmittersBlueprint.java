package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.emitter.ParticleEmitter;
import steve6472.orbiter.world.emitter.ParticleEmitters;
import steve6472.orbiter.world.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.emitter.lifetime.LoopingLifetime;
import steve6472.orbiter.world.emitter.lifetime.OnceLifetime;
import steve6472.orbiter.world.emitter.rate.EmitterRate;
import steve6472.orbiter.world.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;
import steve6472.orlang.Orlang;
import steve6472.orlang.codec.OrVec3;

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
    public record Emitter(OrVec3 offset, EmitterShape shape, EmitterLifetime lifetime, EmitterRate rate, Holder<ParticleBlueprint> particle, Optional<ParticleEmitter.EnvData> envData)
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

    public static class EmitterEntry implements Keyable
    {
        public Emitter emitterBlueprint;
        public Key key;

        @Override
        public Key key()
        {
            return key;
        }

        public ParticleEmitter toEmitter()
        {
            ParticleEmitter emitter = new ParticleEmitter();
            emitter.offset = emitterBlueprint.offset.copy();
            emitter.shape = emitterBlueprint.shape;
            emitter.rate = emitterBlueprint.rate;
            // Because this object is mutable...
            emitter.lifetime = switch (emitterBlueprint.lifetime)
            {
                case OnceLifetime once -> new OnceLifetime(once.activeTime().copy());
                case LoopingLifetime looping -> new LoopingLifetime(looping.activeTime, looping.sleepTime, looping.maxLoops);
                default -> throw new IllegalStateException("Unexpected value: " + emitterBlueprint.lifetime);
            };
            emitterBlueprint.envData.ifPresent(envData ->
            {
                emitter.environmentData = new ParticleEmitter.EnvData(envData.init(), envData.emitterTick(), envData.particleTick(), envData.curves());
                emitter.environmentData.init().ifPresent(code -> Orlang.interpreter.interpret(code, emitter.environment));
            });
            emitter.particleData = emitterBlueprint.particle;
            return emitter;
        }
    }

    public static final Key KEY = Constants.key("particle_emitters");
    private static final Codec<ParticleEmittersBlueprint> CODEC_INLINE = Emitter.CODEC.listOf().xmap(ParticleEmittersBlueprint::new, ParticleEmittersBlueprint::emitters);
    private static final Codec<ParticleEmittersBlueprint> CODEC_BLUEPRINTS =
        Holder.create(Registries.EMITTER_BLUEPRINT).listOf().xmap(l -> new ParticleEmittersBlueprint(l.stream().map(Holder::get).map(e -> e.emitterBlueprint).toList()), b -> b.emitters.stream().map(e -> {
            EmitterEntry entry = new EmitterEntry();
            entry.emitterBlueprint = e;
            entry.key = Key.withNamespace("none", "none");
            return entry;
        }).map(Holder::fromValue).toList());

    public static final Codec<ParticleEmittersBlueprint> CODEC = Codec.withAlternative(CODEC_BLUEPRINTS, CODEC_INLINE);

    @Override
    public List<Component> createComponents()
    {
        EmitterEntry e = new EmitterEntry();
        List<ParticleEmitter> emitterList = new ArrayList<>(emitters.size());
        for (Emitter emitterBl : emitters)
        {
            e.emitterBlueprint = emitterBl;
            emitterList.add(e.toEmitter());
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
