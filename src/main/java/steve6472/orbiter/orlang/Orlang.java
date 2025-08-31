package steve6472.orbiter.orlang;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class Orlang
{
    public static final OrlangParser parser = new OrlangParser();
    public static final OrlangInterpreter interpreter = new OrlangInterpreter();

    public static final Map<String, OrlangValue.Func> MATH_FUNCTIONS = new HashMap<>();

    static
    {
        Class<Double> D = double.class;
        Class<Boolean> B = boolean.class;

        MATH_FUNCTIONS.put("pi", OrlangValue.func(() -> Math.PI));

        // Basic arithmetic
        MATH_FUNCTIONS.put("abs", OrlangValue.func(D, Math::abs));

        MATH_FUNCTIONS.put("rad_sin", OrlangValue.func(D, Math::sin));
        MATH_FUNCTIONS.put("rad_cos", OrlangValue.func(D, Math::cos));
        MATH_FUNCTIONS.put("rad_asin", OrlangValue.func(D, Math::asin));
        MATH_FUNCTIONS.put("rad_acos", OrlangValue.func(D, Math::acos));
        MATH_FUNCTIONS.put("rad_atan", OrlangValue.func(D, Math::atan));
        MATH_FUNCTIONS.put("rad_atan2", OrlangValue.func(D, D, Math::atan2));

        // Trigonometric functions (input in degrees, output where applicable also in degrees)
        MATH_FUNCTIONS.put("sin", OrlangValue.func(D, v -> Math.sin(Math.toRadians(v))));
        MATH_FUNCTIONS.put("cos", OrlangValue.func(D, v -> Math.cos(Math.toRadians(v))));
        MATH_FUNCTIONS.put("asin", OrlangValue.func(D, v -> Math.toDegrees(Math.asin(v))));
        MATH_FUNCTIONS.put("acos", OrlangValue.func(D, v -> Math.toDegrees(Math.acos(v))));
        MATH_FUNCTIONS.put("atan", OrlangValue.func(D, v -> Math.toDegrees(Math.atan(v))));
        MATH_FUNCTIONS.put("atan2", OrlangValue.func(D, D, (a, b) -> Math.toDegrees(Math.atan2(a, b))));

        MATH_FUNCTIONS.put("ceil", OrlangValue.func(D, Math::ceil));
        MATH_FUNCTIONS.put("floor", OrlangValue.func(D, Math::floor));
        MATH_FUNCTIONS.put("trunc", OrlangValue.func(D, v -> (double) v.longValue()));
        MATH_FUNCTIONS.put("round", OrlangValue.func(D, v -> (double) Math.round(v)));

        MATH_FUNCTIONS.put("clamp", OrlangValue.func(D, D, D, Math::clamp));
        MATH_FUNCTIONS.put("max", OrlangValue.func(D, D, Math::max));
        MATH_FUNCTIONS.put("min", OrlangValue.func(D, D, Math::min));

        MATH_FUNCTIONS.put("mod", OrlangValue.func(D, D, (a, b) -> a % b));
        MATH_FUNCTIONS.put("pow", OrlangValue.func(D, D, Math::pow));
        MATH_FUNCTIONS.put("sqrt", OrlangValue.func(D, Math::sqrt));
        MATH_FUNCTIONS.put("exp", OrlangValue.func(D, Math::exp));
        MATH_FUNCTIONS.put("ln", OrlangValue.func(D, Math::log));

        // Random functions
        MATH_FUNCTIONS.put("random", OrlangValue.func(D, D, (low, high) -> low + Math.random() * (high - low)));
        MATH_FUNCTIONS.put("random_integer", OrlangValue.func(D, D, (low, high) -> (double) ((int) (low + Math.random() * ((high - low) + 1)))));

        // Die rolls (sum of N randoms)
        MATH_FUNCTIONS.put("die_roll", OrlangValue.func(D, D, D, (num, low, high) ->
        {
            double sum = 0;
            for (int i = 0; i < num.intValue(); i++)
            {
                sum += low + Math.random() * (high - low);
            }
            return sum;
        }));
        MATH_FUNCTIONS.put("die_roll_integer", OrlangValue.func(D, D, D, (num, low, high) ->
        {
            int sum = 0;
            for (int i = 0; i < num.intValue(); i++)
            {
                sum += (int) (low + Math.random() * ((high - low) + 1));
            }
            return (double) sum;
        }));

        // Interpolation
        MATH_FUNCTIONS.put("hermite_blend", OrlangValue.func(D, t -> t * t * (3 - 2 * t)));
        MATH_FUNCTIONS.put("lerp", OrlangValue.func(D, D, D, (a, b, t) -> a + (b - a) * t));
        // Lerp for rotation (keeps shortest angle)
        MATH_FUNCTIONS.put("deg_lerprotate", OrlangValue.func(D, D, D, (a, b, t) ->
        {
            double diff = ((b - a + Math.PI) % (2 * Math.PI)) - Math.PI;
            return a + diff * t;
        }));MATH_FUNCTIONS.put("lerprotate", OrlangValue.func(D, D, D, (a, b, t) ->
    {
        double diff = ((b - a + 180.0) % 360.0) - 180.0;
        return a + diff * t;
    }));
    }

    public static boolean checkBool(OrlangValue value, OrlangToken operation)
    {
        if (!(value instanceof OrlangValue.Bool bool))
            throw new IllegalStateException("Operation '" + operation.getSymbol() + "' expected a boolean result, got: " + value);
        return bool.value();
    }

    public static boolean checkBool(OrlangValue value)
    {
        if (!(value instanceof OrlangValue.Bool bool))
            throw new IllegalStateException("Expected a boolean result, got: " + value);
        return bool.value();
    }

    public static double checkNum(OrlangValue value, OrlangToken operation)
    {
        if (!(value instanceof OrlangValue.Number num))
            throw new IllegalStateException("Operation '" + operation.getSymbol() + "' expected a number result, got: " + value);
        return num.value();
    }

    public static double checkNum(OrlangValue value)
    {
        if (!(value instanceof OrlangValue.Number num))
            throw new IllegalStateException("Expected a number result, got: " + value);
        return num.value();
    }
}
