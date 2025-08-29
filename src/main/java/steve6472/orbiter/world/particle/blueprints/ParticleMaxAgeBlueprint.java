package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.components.MaxAge;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleMaxAgeBlueprint(OrNumValue maxAge) implements PCBlueprint<ParticleMaxAgeBlueprint>
{
    public static final Key KEY = Constants.key("max_age");
    public static final Codec<ParticleMaxAgeBlueprint> CODEC = OrNumValue.CODEC.xmap(ParticleMaxAgeBlueprint::new, ParticleMaxAgeBlueprint::maxAge);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        MaxAge component = particleEngine.createComponent(MaxAge.class);
        component.maxAge = (int) maxAge.evaluateAndGet(environment);
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleMaxAgeBlueprint> codec()
    {
        return CODEC;
    }
}
