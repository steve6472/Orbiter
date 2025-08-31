package steve6472.orbiter.orlang.codec;

import com.mojang.serialization.Codec;
import steve6472.orbiter.orlang.Orlang;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.OrlangValue;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrNumValue
{
    private final OrCode code;
    private OrlangValue.Number value;

    private static final Codec<OrNumValue> NUM_CODEC = OrCode.CODEC.xmap(OrNumValue::new, o -> o.code);
    public static final Codec<OrNumValue> CODEC = Codec.withAlternative(Codec.DOUBLE.xmap(OrNumValue::new, OrNumValue::get), NUM_CODEC);

    public OrNumValue(OrCode code)
    {
        this.code = code;
    }

    public OrNumValue(double constant)
    {
        this.code = null;
        this.value = OrlangValue.num(constant);
    }

    public boolean hadFirstEval()
    {
        return !isConstant() && value != null;
    }

    public boolean isConstant()
    {
        return code == null;
    }

    public double get()
    {
        return value.value();
    }

    public float fget()
    {
        return (float) value.value();
    }

    public void evaluate(OrlangEnvironment environment)
    {
        if (isConstant())
            return;

        OrlangValue retValue = Orlang.interpreter.interpret(code, environment);
        if (!(retValue instanceof OrlangValue.Number number))
            throw new RuntimeException("Orlang did not return a number, got: '" + retValue + "' for code: '" + code.codeStr() + "'");
        this.value = number;
    }

    public double evaluateAndGet(OrlangEnvironment environment)
    {
        evaluate(environment);
        return get();
    }

    public OrNumValue copy()
    {
        OrNumValue orNumValue = new OrNumValue(code);
        orNumValue.value = value;
        return orNumValue;
    }

    @Override
    public String toString()
    {
        return "OrNumValue{" + "code=" + code + ", value=" + value + '}';
    }
}
