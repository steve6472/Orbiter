package steve6472.orbiter.hex;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 */
public record Orientation(double f0, double f1, double f2, double f3,
                          double b0, double b1, double b2, double b3,
                          double startAngle)
{
    private static final double SQR3 = Math.sqrt(3.0);

    public static final Orientation POINTY = new Orientation(
        SQR3, SQR3 / 2.0, 0.0, 3.0 / 2.0,
        SQR3 / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0,
        0.5);

    public static final Orientation FLAT = new Orientation(
        3.0 / 2.0, 0.0, SQR3 / 2.0, SQR3,
        2.0 / 3.0, 0.0, -1.0 / 3.0, SQR3 / 3.0,
        0.0);
}
