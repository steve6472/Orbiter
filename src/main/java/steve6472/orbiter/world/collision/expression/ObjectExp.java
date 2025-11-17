package steve6472.orbiter.world.collision.expression;

import com.mojang.datafixers.util.Either;
import steve6472.orbiter.world.collision.ShapeExp;

import java.util.Arrays;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public record ObjectExp(String type, Either<Float, String>[] params) implements ShapeExp
{
    @Override
    public String toString()
    {
        return "ObjectExp{" + "type='" + type + '\'' + ", params=" + Arrays.toString(params) + '}';
    }

    public float getF(int index)
    {
        return params[index].left().orElseThrow(() -> new IllegalArgumentException("Expected float, got string"));
    }

    public String getS(int index)
    {
        return params[index].right().orElseThrow(() -> new IllegalArgumentException("Expected string, got float"));
    }
}
