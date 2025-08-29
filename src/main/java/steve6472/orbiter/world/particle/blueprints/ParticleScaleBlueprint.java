package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.world.particle.components.Scale;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orbiter.world.particle.core.PCBlueprint;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleScaleBlueprint(OrVec3 scale) implements PCBlueprint<ParticleScaleBlueprint>
{
    public static final Key KEY = Constants.key("scale");
    public static final Codec<ParticleScaleBlueprint> CODEC = OrVec3.CODEC.xmap(ParticleScaleBlueprint::new, ParticleScaleBlueprint::scale);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        Scale component = particleEngine.createComponent(Scale.class);
        component.scale = scale.copy();
        return component;
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
