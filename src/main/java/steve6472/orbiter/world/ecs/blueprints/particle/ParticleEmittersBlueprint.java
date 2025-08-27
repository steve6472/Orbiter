package steve6472.orbiter.world.ecs.blueprints.particle;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.core.util.ExtraCodecs;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.world.ecs.components.emitter.LocalSpaceEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitters;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.LoopingLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.OnceLifetime;
import steve6472.orbiter.world.ecs.components.emitter.rate.EmitterRate;
import steve6472.orbiter.world.ecs.components.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleEmittersBlueprint(List<Emitter> emitters) implements Blueprint<ParticleEmittersBlueprint>
{
    private record Emitter(OrVec3 offset, EmitterShape shape, EmitterLifetime lifetime, EmitterRate rate, LocalSpaceEmitter localSpace, Key entity)
    {
        public static final Codec<Emitter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OrVec3.CODEC.optionalFieldOf("offset", new OrVec3()).forGetter(Emitter::offset),
            EmitterShape.CODEC.fieldOf("shape").forGetter(Emitter::shape),
            EmitterLifetime.CODEC.fieldOf("lifetime").forGetter(Emitter::lifetime),
            EmitterRate.CODEC.fieldOf("rate").forGetter(Emitter::rate),
            LocalSpaceEmitter.CODEC.optionalFieldOf("local_space", LocalSpaceEmitter.DEFAULT).forGetter(Emitter::localSpace),
            Key.CODEC.fieldOf("entity").forGetter(Emitter::entity)
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
                case LoopingLifetime looping -> {
                    LoopingLifetime newLooping = new LoopingLifetime(looping.ticksActive, looping.ticksAsleep, looping.loopCount);
                    newLooping.state = looping.state;
                    newLooping.timer = looping.timer;
                    newLooping.timesLooped = looping.timesLooped;
                    yield newLooping;
                }
                default -> throw new IllegalStateException("Unexpected value: " + emitterBl.lifetime);
            };
            emitter.localSpace = emitterBl.localSpace;
            emitter.entity = emitterBl.entity;
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
