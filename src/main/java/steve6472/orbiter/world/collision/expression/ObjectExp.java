package steve6472.orbiter.world.collision.expression;

import steve6472.orbiter.world.collision.ShapeExp;

import java.util.Arrays;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public record ObjectExp(String type, float[] params) implements ShapeExp
{
    @Override
    public String toString()
    {
        return "ObjectExp{" + "type='" + type + '\'' + ", params=" + Arrays.toString(params) + '}';
    }
}
