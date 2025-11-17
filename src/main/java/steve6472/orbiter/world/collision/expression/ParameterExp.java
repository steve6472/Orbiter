package steve6472.orbiter.world.collision.expression;

import com.mojang.datafixers.util.Either;
import steve6472.orbiter.world.collision.ShapeExp;

import java.util.Arrays;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public record ParameterExp(Either<Float, String>[] parameters) implements ShapeExp
{
    @Override
    public String toString()
    {
        return "ParameterExp{" + "parameters=" + Arrays.toString(parameters) + '}';
    }
}
