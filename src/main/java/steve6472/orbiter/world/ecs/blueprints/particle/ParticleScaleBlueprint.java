package steve6472.orbiter.world.ecs.blueprints.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
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
import steve6472.orbiter.world.ecs.components.particle.Scale;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleScaleBlueprint(OrVec3 scale) implements Blueprint<ParticleScaleBlueprint>
{
    public static final Key KEY = Constants.key("particle_scale");
    public static final Codec<ParticleScaleBlueprint> CODEC = OrVec3.CODEC.xmap(ParticleScaleBlueprint::new, ParticleScaleBlueprint::scale);

    @Override
    public List<Component> createComponents()
    {
        Scale component = new Scale();
        component.scale = scale.copy();
        return List.of(component);
    }

    @Override
    public List<Component> createParticleComponents(PooledEngine particleEngine)
    {
        Scale component = particleEngine.createComponent(Scale.class);
        component.scale = scale.copy();
        return List.of(component);
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleScaleBlueprint> codec()
    {
        return CODEC;
    }
}
