package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.particle.components.RenderPipeline;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticlePipelineBlueprint(RenderPipeline.Enum value) implements PCBlueprint<ParticlePipelineBlueprint>
{
    public static final Key KEY = Constants.key("render_pipeline");
    public static final Codec<ParticlePipelineBlueprint> CODEC = RenderPipeline.Enum.CODEC.xmap(ParticlePipelineBlueprint::new, ParticlePipelineBlueprint::value);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        RenderPipeline component = particleEngine.createComponent(RenderPipeline.class);
        component.value = value;
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticlePipelineBlueprint> codec()
    {
        return CODEC;
    }
}
