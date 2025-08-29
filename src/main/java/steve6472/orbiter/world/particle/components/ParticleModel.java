package steve6472.orbiter.world.particle.components;

import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.ErrorModel;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class ParticleModel implements ParticleComponent
{
    public Model model;

    @Override
    public void reset()
    {
        model = ErrorModel.VK_STATIC_INSTANCE;
    }
}
