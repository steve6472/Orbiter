package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.components.LinearDragCoefficient;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleLinearDragCoefficientBlueprint(OrNumValue value) implements PCBlueprint<ParticleLinearDragCoefficientBlueprint>
{
    public static final Key KEY = Constants.key("linear_drag_coefficient");
    public static final Codec<ParticleLinearDragCoefficientBlueprint> CODEC = OrNumValue.CODEC.xmap(ParticleLinearDragCoefficientBlueprint::new, ParticleLinearDragCoefficientBlueprint::value);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        LinearDragCoefficient component = particleEngine.createComponent(LinearDragCoefficient.class);
        component.value = value.copy();
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleLinearDragCoefficientBlueprint> codec()
    {
        return CODEC;
    }
}
