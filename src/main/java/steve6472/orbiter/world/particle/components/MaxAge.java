package steve6472.orbiter.world.particle.components;

import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.AST;
import steve6472.orlang.VarContext;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class MaxAge implements ParticleComponent
{
    public static final AST.Node.Identifier MAX_AGE = new AST.Node.Identifier(VarContext.VARIABLE, "max_age");
    public static final AST.Node.Identifier AGE = new AST.Node.Identifier(VarContext.VARIABLE, "age");

    // in seconds
    public double maxAge;
    public long spawnTimeMilli;

    @Override
    public void reset()
    {
        maxAge = 1;
        spawnTimeMilli = 0;
    }

    public double calculateAge(long now)
    {
        return (now - spawnTimeMilli) / 1e3d;
    }

    @Override
    public String toString()
    {
        return "MaxAge{" + "maxAge=" + maxAge + ", spawnTimeMilli=" + spawnTimeMilli + '}';
    }
}
