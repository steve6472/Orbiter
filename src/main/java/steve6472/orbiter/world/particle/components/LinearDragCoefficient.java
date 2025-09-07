package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.codec.OrNumValue;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class LinearDragCoefficient implements ParticleComponent
{
    public OrNumValue value;

    @Override
    public void reset()
    {
        value = null;
    }
}
