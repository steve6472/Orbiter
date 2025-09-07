package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.particle.components.LinearAcceleration;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.codec.OrVec3;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleLinearAccelerationBlueprint(OrVec3 value) implements PCBlueprint<ParticleLinearAccelerationBlueprint>
{
    public static final Key KEY = Constants.key("linear_acceleration");
    public static final Codec<ParticleLinearAccelerationBlueprint> CODEC = OrVec3.CODEC.xmap(ParticleLinearAccelerationBlueprint::new, ParticleLinearAccelerationBlueprint::value);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        LinearAcceleration component = particleEngine.createComponent(LinearAcceleration.class);
        component.value = value.copy();
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleLinearAccelerationBlueprint> codec()
    {
        return CODEC;
    }
}
