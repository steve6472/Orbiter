package steve6472.orbiter.orlang;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrlangEnvironment
{
    private Map<String, OrlangValue> valueMap = new HashMap<>();
    private Map<String, OrlangValue> tempValueMap = new HashMap<>();

    public void setValue(AST.Node.Identifier identifier, OrlangValue value)
    {
        if (identifier.path().length == 0)
            valueMap.put(identifier.name(), value);
        else
            throw new UnsupportedOperationException("Path is not implemented yet");
    }

    public OrlangValue getValue(AST.Node.Identifier identifier)
    {
        if (identifier.path().length == 0)
            return valueMap.get(identifier.name());
        else
            throw new UnsupportedOperationException("Path is not implemented yet");
    }

    // Currently nesting is disabled
    public OrlangEnvironment nest()
    {
        return this;
    }

    @Override
    public String toString()
    {
        return "OrlangEnvironment{" + "valueMap=" + valueMap + '}';
    }
}
