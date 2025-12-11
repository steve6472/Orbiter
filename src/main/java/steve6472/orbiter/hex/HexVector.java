package steve6472.orbiter.hex;

public enum HexVector
{
    TOP_RIGHT(Hex.DIRECTIONS[1]),
    RIGHT(Hex.DIRECTIONS[0]),
    BOTTOM_RIGHT(Hex.DIRECTIONS[5]),
    BOTTOM_LEFT(Hex.DIRECTIONS[4]),
    LEFT(Hex.DIRECTIONS[3]),
    TOP_LEFT(Hex.DIRECTIONS[2]),

    INVALID(new Hex(0, 0, 0));

    private static final HexVector[] VALUES = HexVector.values();
    public final Hex vector;

    HexVector(Hex vector)
    {
        this.vector = vector;
    }

    public static HexVector fromDirection(Hex direction)
    {
        for (HexVector value : VALUES)
        {
            if (value.vector.equals(direction))
                return value;
        }
        return INVALID;
    }

    public HexDirection getDirection(HexVector vector)
    {
        return switch (this)
        {
            case TOP_RIGHT -> switch (vector)
            {
                case RIGHT -> HexDirection.RIGHT;
                case TOP_RIGHT -> HexDirection.FORWARD;
                case TOP_LEFT -> HexDirection.LEFT;
                case BOTTOM_RIGHT -> HexDirection.SHARP_RIGHT;
                case LEFT -> HexDirection.SHARP_LEFT;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case RIGHT -> switch (vector)
            {
                case RIGHT -> HexDirection.FORWARD;
                case TOP_RIGHT -> HexDirection.LEFT;
                case TOP_LEFT -> HexDirection.SHARP_LEFT;
                case BOTTOM_RIGHT -> HexDirection.RIGHT;
                case BOTTOM_LEFT -> HexDirection.SHARP_RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case BOTTOM_RIGHT -> switch (vector)
            {
                case RIGHT -> HexDirection.LEFT;
                case TOP_RIGHT -> HexDirection.SHARP_LEFT;
                case BOTTOM_RIGHT -> HexDirection.FORWARD;
                case BOTTOM_LEFT -> HexDirection.RIGHT;
                case LEFT -> HexDirection.SHARP_RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case BOTTOM_LEFT -> switch (vector)
            {
                case RIGHT -> HexDirection.SHARP_LEFT;
                case BOTTOM_RIGHT -> HexDirection.LEFT;
                case BOTTOM_LEFT -> HexDirection.FORWARD;
                case TOP_LEFT -> HexDirection.SHARP_RIGHT;
                case LEFT -> HexDirection.RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case LEFT -> switch (vector)
            {
                case TOP_RIGHT -> HexDirection.SHARP_RIGHT;
                case BOTTOM_RIGHT -> HexDirection.SHARP_LEFT;
                case BOTTOM_LEFT -> HexDirection.LEFT;
                case TOP_LEFT -> HexDirection.RIGHT;
                case LEFT -> HexDirection.FORWARD;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case TOP_LEFT -> switch (vector)
            {
                case RIGHT -> HexDirection.SHARP_RIGHT;
                case TOP_RIGHT -> HexDirection.RIGHT;
                case BOTTOM_LEFT -> HexDirection.SHARP_LEFT;
                case TOP_LEFT -> HexDirection.FORWARD;
                case LEFT -> HexDirection.LEFT;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }
}