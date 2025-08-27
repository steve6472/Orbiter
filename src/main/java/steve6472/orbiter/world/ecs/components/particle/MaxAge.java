package steve6472.orbiter.world.ecs.components.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.orlang.AST;
import steve6472.orbiter.orlang.VarContext;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class MaxAge implements Component, Pool.Poolable
{
    public static final AST.Node.Identifier MAX_AGE = new AST.Node.Identifier(VarContext.VARIABLE, "particle_max_age");
    public static final AST.Node.Identifier AGE = new AST.Node.Identifier(VarContext.VARIABLE, "particle_age");

    public int maxAge;
    public int age;

    @Override
    public void reset()
    {
        maxAge = 60;
        age = 0;
    }

    @Override
    public String toString()
    {
        return "MaxAge{" + "maxAge=" + maxAge + ", age=" + age + '}';
    }
}
