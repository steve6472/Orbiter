package steve6472.orbiter.world.particle.components;

import org.joml.Vector3f;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 10/11/2025
 * Project: Orbiter <br>
 */
public class PlaneInterpolation implements ParticleComponent
{
    public final Vector3f position = new Vector3f();
    public final Vector3f previousPosition = new Vector3f();

    public float rotation;
    public float previousRotation;

    public float scaleX, scaleY;
    public float previousScaleX, previousScaleY;

    @Override
    public void reset()
    {
        position.zero();
        previousPosition.zero();

        rotation = 0;
        previousRotation = 0;

        scaleX = 1;
        scaleY = 1;
        previousScaleX = 1;
        previousScaleY = 1;
    }
}
