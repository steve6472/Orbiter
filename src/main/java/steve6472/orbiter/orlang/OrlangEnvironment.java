package steve6472.orbiter.orlang;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangEnvironment implements Component, Pool.Poolable
{
    private final Map<String, OrlangValue> variableMap = new HashMap<>();
    private final Map<String, OrlangValue> tempMap = new HashMap<>();

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
                return variableMap.get(identifier.name());
            else if (identifier.context() == VarContext.TEMP)
                return tempMap.get(identifier.name());
            else
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
        return "OrlangEnvironment{" + "valueMap=" + variableMap + '}';
    }

    @Override
    public void reset()
    {
        variableMap.clear();
        tempMap.clear();
    }
}
