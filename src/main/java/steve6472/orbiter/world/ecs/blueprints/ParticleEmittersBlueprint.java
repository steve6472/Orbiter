package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.*;
import steve6472.flare.assets.model.Model;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.particle.blueprints.ParticleLocalSpaceBlueprint;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitters;
import steve6472.orbiter.world.particle.blueprints.ParticleMaxAgeBlueprint;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.EmitterLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.LoopingLifetime;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.OnceLifetime;
import steve6472.orbiter.world.ecs.components.emitter.rate.EmitterRate;
import steve6472.orbiter.world.ecs.components.emitter.shapes.EmitterShape;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.components.MaxAge;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.components.Scale;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleScaleBlueprint;

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
    public record ParticleData(Holder<Model> model, Optional<ParticleMaxAgeBlueprint> maxAge, Optional<ParticleScaleBlueprint> scale, Optional<ParticleLocalSpaceBlueprint> localSpace)
    {
        public static final Codec<ParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Holder.create(FlareRegistries.STATIC_MODEL).fieldOf("model").forGetter(ParticleData::model),
            ParticleMaxAgeBlueprint.CODEC.optionalFieldOf("max_age").forGetter(ParticleData::maxAge),
            ParticleScaleBlueprint.CODEC.optionalFieldOf("scale").forGetter(ParticleData::scale),
            ParticleLocalSpaceBlueprint.CODEC.optionalFieldOf("local_space").forGetter(ParticleData::localSpace)
        ).apply(instance, ParticleData::new));

        public List<Component> createComponents(PooledEngine particleEngine, OrlangEnvironment environment)
        {
            List<Component> components = new ArrayList<>();

            ParticleModel model = particleEngine.createComponent(ParticleModel.class);
            model.model = this.model.get();
            components.add(model);

            maxAge.ifPresentOrElse(maxAge -> {
                MaxAge component = particleEngine.createComponent(MaxAge.class);
                component.maxAge = (int) maxAge.maxAge().evaluateAndGet(environment);
                components.add(component);
            }, () -> components.add(particleEngine.createComponent(MaxAge.class)));

            scale.ifPresent(scale -> {
                Scale component = particleEngine.createComponent(Scale.class);
                component.scale = scale.scale().copy();
                components.add(component);
            });

            localSpace.ifPresent(localSpace -> {
                LocalSpace component = particleEngine.createComponent(LocalSpace.class);
                component.position = localSpace.position();
                component.rotation = localSpace.rotation();
                component.velocity = localSpace.velocity();
                components.add(component);
            });

            return components;
        }
    }

    private record Emitter(OrVec3 offset, EmitterShape shape, EmitterLifetime lifetime, EmitterRate rate, ParticleData particle)
    {
        public static final Codec<Emitter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OrVec3.CODEC.optionalFieldOf("offset", new OrVec3()).forGetter(Emitter::offset),
            EmitterShape.CODEC.fieldOf("shape").forGetter(Emitter::shape),
            EmitterLifetime.CODEC.fieldOf("lifetime").forGetter(Emitter::lifetime),
            EmitterRate.CODEC.fieldOf("rate").forGetter(Emitter::rate),
            ParticleData.CODEC.fieldOf("particle").forGetter(Emitter::particle)
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
                    LoopingLifetime newLooping = new LoopingLifetime(looping.ticksActive, looping.ticksAsleep, looping.maxLoopCount);
                    newLooping.state = looping.state;
                    newLooping.timer = looping.timer;
                    newLooping.timesLooped = looping.timesLooped;
                    yield newLooping;
                }
                default -> throw new IllegalStateException("Unexpected value: " + emitterBl.lifetime);
            };
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
