package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class Position implements ParticleComponent
{
    public float x, y, z;

    @Override
    public void reset()
    {
        x = 0;
        y = 0;
        z = 0;
    }
}
