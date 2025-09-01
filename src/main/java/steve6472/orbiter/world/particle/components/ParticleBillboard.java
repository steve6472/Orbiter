package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.rendering.Billboard;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class ParticleBillboard implements ParticleComponent
{
    public Billboard billboard;

    @Override
    public void reset()
    {
        billboard = Billboard.FIXED;
    }
}
