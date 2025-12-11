package steve6472.orbiter.hex;

public enum HexDirection
{
    FORWARD('f'),
    LEFT('l'),
    RIGHT('r'),
    SHARP_LEFT('e'),
    SHARP_RIGHT('i');

    public final char code;

    HexDirection(char code)
    {
        this.code = code;
    }
}