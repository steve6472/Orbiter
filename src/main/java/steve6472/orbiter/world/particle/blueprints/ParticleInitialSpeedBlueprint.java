package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleInitialSpeedBlueprint(OrNumValue initialSpeed) implements PCBlueprint<ParticleInitialSpeedBlueprint>
{
    public static final Key KEY = Constants.key("initial_speed");
    public static final Codec<ParticleInitialSpeedBlueprint> CODEC = OrNumValue.CODEC.xmap(ParticleInitialSpeedBlueprint::new, ParticleInitialSpeedBlueprint::initialSpeed);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        throw new UnsupportedOperationException("Velocity is special case");
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleInitialSpeedBlueprint> codec()
    {
        return CODEC;
    }
}
