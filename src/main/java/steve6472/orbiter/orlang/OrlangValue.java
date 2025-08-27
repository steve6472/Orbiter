package steve6472.orbiter.orlang;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public interface OrlangValue
{
    Bool TRUE = new Bool(true);
    Bool FALSE = new Bool(false);

    static Number num(double val)
    {
        return new Number(val);
    }

    static Bool bool(boolean val)
    {
        return val ? TRUE : FALSE;
    }

    static Func func(_Func0 function)
    {
        return new Func0(function);
    }

    static <A1> Func func(Class<A1> clazz, _Func1<A1> function)
    {
        verifyClasses(clazz);
        return new Func1<>(clazz, function);
    }

    static <A1, A2> Func func(Class<A1> clazz1, Class<A2> clazz2, _Func2<A1, A2> function)
    {
        verifyClasses(clazz1, clazz2);
        return new Func2<>(clazz1, clazz2, function);
    }

    static <A1, A2, A3> Func func(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, _Func3<A1, A2, A3> function)
    {
        verifyClasses(clazz1, clazz2, clazz3);
        return new Func3<>(clazz1, clazz2, clazz3, function);
    }

    /*
     * Big boi classes
     */
    final class Number implements OrlangValue
    {
        private final double value;

        private Number(double value)
        {
            this.value = value;
        }

        public double value()
        {
            return value;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            var that = (Number) obj;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value);
        }

        @Override
        public String toString()
        {
            return "Number[" + "value=" + value + ']';
        }
    }

    final class Bool implements OrlangValue
    {
        private final boolean value;

        private Bool(boolean value)
        {
            this.value = value;
        }

        public boolean value()
        {
            return value;
        }

        @Override
        public String toString()
        {
            return "Bool{" + "value=" + value + '}';
        }
    }

    /*
     * Functions
     */

    static OrlangValue smartCast(Object val)
    {
        Objects.requireNonNull(val);

        if (val instanceof java.lang.Number number)
            return num(number.doubleValue());
        else if (val instanceof Boolean b)
            return bool(b);
        else
            throw new IllegalArgumentException("Unknown type " + val.getClass().getSimpleName());
    }

    static Object dumbCast(OrlangValue val)
    {
        Objects.requireNonNull(val);

        if (val instanceof Number number)
            return number.value;
        else if (val instanceof Bool b)
            return b.value;
        else
            throw new IllegalArgumentException("Unknown type " + val.getClass().getSimpleName());
    }

    private static void verifyClasses(Class<?>... clazz)
    {
        Class<Double> D = double.class;
        Class<Boolean> B = boolean.class;

        for (Class<?> aClass : clazz)
        {
            if (aClass != D && aClass != B)
            {
                throw new RuntimeException("Unexpected class type " + aClass);
            }
        }
    }

    private static void verifyInputs(OrlangValue[] values, Class<?>... types)
    {
        if (types.length != values.length)
            throw new RuntimeException("Types and values count mismatch");

        for (int i = 0; i < types.length; i++)
        {
            verifyInput(values[i], types[i], i);
        }
    }

    private static void verifyInput(OrlangValue value, Class<?> type, int index)
    {
        if (type == double.class && !(value instanceof Number))
            throw new IllegalArgumentException("Expected number, instead got " + value.getClass().getSimpleName() + " at " + index);

        if (type == boolean.class && !(value instanceof Bool))
            throw new IllegalArgumentException("Expected boolean, instead got " + value.getClass().getSimpleName() + " at " + index);
    }

    private static void verifyInputCount(OrlangValue[] arr, int expected)
    {
        if (arr.length != expected) throw new RuntimeException("Incorrect count of arguments, expected: " + expected + ", got: " + arr.length);
    }

    interface Func extends OrlangValue
    {
        OrlangValue eval(OrlangValue... values);
    }

    interface _Func0
    {
        Object eval();
    }

    interface _Func1<A1>
    {
        Object eval(A1 a1);
    }

    interface _Func2<A1, A2>
    {
        Object eval(A1 a1, A2 a2);
    }

    interface _Func3<A1, A2, A3>
    {
        Object eval(A1 a1, A2 a2, A3 a3);
    }

    record Func0(_Func0 value) implements Func
    {
        @Override
        public OrlangValue eval(OrlangValue... values)
        {
            return smartCast(value.eval());
        }
    }

    record Func1<A1>(Class<A1> type1, _Func1<A1> value) implements Func
    {
        @Override
        public OrlangValue eval(OrlangValue... values)
        {
            OrlangValue.verifyInputCount(values, 1);
            OrlangValue.verifyInput(values[0], type1, 1);
            //noinspection rawtypes,unchecked
            Object eval = ((_Func1) value).eval(dumbCast(values[0]));
            return smartCast(eval);
        }
    }

    record Func2<A1, A2>(Class<A1> type1, Class<A2> type2, _Func2<A1, A2> value) implements Func
    {
        @Override
        public OrlangValue eval(OrlangValue... values)
        {
            OrlangValue.verifyInputCount(values, 2);
            OrlangValue.verifyInputs(values, type1, type2);
            //noinspection rawtypes,unchecked
            return smartCast(((_Func2) value).eval(dumbCast(values[0]), dumbCast(values[1])));
        }
    }

    record Func3<A1, A2, A3>(Class<A1> type1, Class<A2> type2, Class<A3> type3, _Func3<A1, A2, A3> value) implements Func
    {
        @Override
        public OrlangValue eval(OrlangValue... values)
        {
            OrlangValue.verifyInputCount(values, 3);
            OrlangValue.verifyInputs(values, type1, type2, type3);
            //noinspection rawtypes,unchecked
            return smartCast(((_Func3) value).eval(dumbCast(values[0]), dumbCast(values[1]), dumbCast(values[2])));
        }
    }
}
