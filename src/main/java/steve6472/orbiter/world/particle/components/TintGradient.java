package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.Gradient;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public class TintGradient implements ParticleComponent
{
    public Gradient gradient = new Gradient();

    @Override
    public void reset()
    {
        gradient.reset();
    }
}
