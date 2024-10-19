package steve6472.orbiter.world.collision;

import steve6472.core.tokenizer.Token;

public enum ShapeToken implements Token
{
    COLLISION("collision"),

    SEPARATOR(","),
    PARAMETERS_START("("),
    PARAMETERS_END(")"),
    GROUP_START("{"),
    GROUP_END("}"),

    ;

    private final String symbol;

    ShapeToken(String symbol)
    {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol()
    {
        return symbol;
    }

    @Override
    public boolean isMerge()
    {
        return false;
    }
}