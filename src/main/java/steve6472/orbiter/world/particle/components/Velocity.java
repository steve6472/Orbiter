package steve6472.orbiter.world.particle.components;

import org.joml.Math;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class Velocity implements ParticleComponent
{
    // normalized vector
    public float x, y, z;

    /// Normalizes the vector
    public void set(double X, double Y, double Z, float initialSpeed)
    {
        double scalar = Math.invsqrt(Math.fma(X, X, Math.fma(Y, Y, Z * Z)));
        this.x = (float) (X * scalar) * initialSpeed;
        this.y = (float) (Y * scalar) * initialSpeed;
        this.z = (float) (Z * scalar) * initialSpeed;
    }

    @Override
    public void reset()
    {
        x = 0;
        y = 0;
        z = 0;
    }
}
