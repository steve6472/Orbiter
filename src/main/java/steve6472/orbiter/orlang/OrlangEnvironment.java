package steve6472.orbiter.orlang;

import steve6472.orbiter.orlang.codec.OrCode;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangEnvironment implements ParticleComponent
{
    private final Map<String, OrlangValue> variableMap = new HashMap<>();
    private final Map<String, OrlangValue> tempMap = new HashMap<>();
    public Map<String, OrCode> expressions = new HashMap<>();
    public Map<AST.Node.Identifier, Curve> curves = new HashMap<>();

    public void setValue(AST.Node.Identifier identifier, OrlangValue value)
    {
        if (identifier.path().length == 0)
        {
            if (identifier.context() == VarContext.VARIABLE)
                variableMap.put(identifier.name(), value);
            else if (identifier.context() == VarContext.TEMP)
                tempMap.put(identifier.name(), value);
            else
                throw new IllegalArgumentException("Context " + identifier.context() + " can not be written to");
        } else
        {
            throw new UnsupportedOperationException("Path is not implemented yet");
        }
    }

    public OrlangValue getValue(AST.Node.Identifier identifier)
    {
        if (identifier.path().length == 0)
        {
            if (identifier.context() == VarContext.VARIABLE)
            {
                OrlangValue orlangValue = variableMap.get(identifier.name());
                if (orlangValue == null)
                    throw new NullPointerException("No value for " + identifier + " was found");
                return orlangValue;
            } else if (identifier.context() == VarContext.TEMP)
            {
                OrlangValue orlangValue = tempMap.get(identifier.name());
                if (orlangValue == null)
                    throw new NullPointerException("No value for " + identifier + " was found");
                return orlangValue;
            } else
                throw new IllegalArgumentException("Context " + identifier.context() + " is not implemented yet");
        } else
        {
            throw new UnsupportedOperationException("Path is not implemented yet");
        }
    }

    public void clearTemp()
    {
        tempMap.clear();
    }

    // Currently nesting is disabled
    public OrlangEnvironment nest()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return "OrlangEnvironment{" + "variableMap=" + variableMap + ", tempMap=" + tempMap + ", expressions=" + expressions + ", curves=" + curves + '}';
    }

    @Override
    public void reset()
    {
        variableMap.clear();
        tempMap.clear();
        expressions.clear();
        curves.clear();
    }
}
