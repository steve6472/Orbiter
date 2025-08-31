package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class LinearAcceleration implements ParticleComponent
{
    public OrVec3 value;

    @Override
    public void reset()
    {
        value = null;
    }
}
