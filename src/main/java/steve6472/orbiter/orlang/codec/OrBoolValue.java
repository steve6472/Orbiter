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
public class OrBoolValue
{
    private static final OrBoolValue TRUE = new OrBoolValue(true);
    private static final OrBoolValue FALSE = new OrBoolValue(false);

    private final OrCode code;
    private OrlangValue.Bool value;

    private static final Codec<OrBoolValue> BOOL_CODEC = OrCode.CODEC.xmap(OrBoolValue::new, o -> o.code);
    public static final Codec<OrBoolValue> CODEC = Codec.withAlternative(Codec.BOOL.xmap(OrBoolValue::constant, OrBoolValue::get), BOOL_CODEC);

    public OrBoolValue(OrCode code)
    {
        this.code = code;
    }

    private OrBoolValue(boolean constant)
    {
        this.code = null;
        this.value = OrlangValue.bool(constant);
    }

    public static OrBoolValue constant(boolean constant)
    {
        return constant ? TRUE : FALSE;
    }

    public boolean hadFirstEval()
    {
        return value != null;
    }

    public boolean isConstant()
    {
        return code == null;
    }

    public boolean get()
    {
        return value.value();
    }

    public void evaluate(OrlangEnvironment environment)
    {
        if (isConstant())
            return;

        OrlangValue retValue = Orlang.interpreter.interpret(code, environment);
        if (!(retValue instanceof OrlangValue.Bool bool))
            throw new RuntimeException("Orlang did not return a bool");
        this.value = bool;
    }

    public boolean evaluateAndGet(OrlangEnvironment environment)
    {
        evaluate(environment);
        return get();
    }

    public OrBoolValue copy()
    {
        OrBoolValue orBoolValue = new OrBoolValue(code);
        orBoolValue.value = value;
        return orBoolValue;
    }
}
