package steve6472.orbiter.world.ecs.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public class OrlangEnv implements ParticleComponent
{
    public OrlangEnvironment env = new OrlangEnvironment();

    @Override
    public void reset()
    {
        env.reset();
    }
}
