package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class RenderMaterial implements ParticleComponent
{
    public ParticleMaterial value;

    @Override
    public void reset()
    {
        value = ParticleMaterial.BLEND;
    }
}
