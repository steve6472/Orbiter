package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class Rotation implements ParticleComponent
{
    public float rotationAxisX, rotationAxisY, rotationAxisZ;
    public float rotation;
    public float rate;
    public OrNumValue acceleration;
    public OrNumValue dragCoefficient;

    @Override
    public void reset()
    {
        rotationAxisX = 0;
        rotationAxisY = 0;
        rotationAxisZ = -1;
        rotation = 0;
        rate = 0;
        acceleration = null;
        dragCoefficient = null;
    }
}
