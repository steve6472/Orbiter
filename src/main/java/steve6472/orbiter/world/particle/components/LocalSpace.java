package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class LocalSpace implements ParticleComponent
{
    public boolean position;
    public boolean rotation;
    public boolean velocity;

    @Override
    public void reset()
    {
        position = false;
        rotation = false;
        velocity = false;
    }

    @Override
    public String toString()
    {
        return "LocalSpace{" + "position=" + position + ", rotation=" + rotation + ", velocity=" + velocity + '}';
    }
}
