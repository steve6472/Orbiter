package steve6472.orbiter.orlang;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public enum VarContext
{
    VARIABLE("variable", "v"),
    TEMP("temp", "t"),
    MATH("math"),
    QUERY("query", "q"),
    ;

    private final String contextName;
    private final String alias;

    VarContext(String contextName, String alias)
    {
        this.contextName = contextName;
        this.alias = alias;
    }

    VarContext(String contextName)
    {
        this(contextName, null);
    }

    public String contextName()
    {
        return contextName;
    }

    public String alias()
    {
        return alias;
    }
}
