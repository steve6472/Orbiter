package steve6472.orbiter.orlang;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public interface OrlangValue
{
    record Number(double value) implements OrlangValue {}

    Bool TRUE = new Bool(true);
    Bool FALSE = new Bool(false);
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

    static Bool bool(boolean val)
    {
        return val ? TRUE : FALSE;
    }
}
