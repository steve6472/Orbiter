package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.orlang.codec.OrVec3;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class Scale implements ParticleComponent
{
    public OrVec3 scale;

    @Override
    public void reset()
    {
        scale = null;
    }
}
