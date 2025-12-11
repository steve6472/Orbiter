package steve6472.orbiter.hex;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 */
public record FractionalHex(double q, double r, double s)
{
    public Hex round()
    {
        int q_ = (int) Math.round(q);
        int r_ = (int) Math.round(r);
        int s_ = (int) Math.round(s);
        double qDiff = Math.abs(q_ - q);
        double rDiff = Math.abs(r_ - r);
        double sDiff = Math.abs(s_ - s);
        if (qDiff > rDiff && qDiff > sDiff)
            q_ = -r_ - s_;
        else if (rDiff > sDiff)
            r_ = -q_ - s_;
        else
            s_ = -q_ - r_;
        return new Hex(q_, r_, s_);
    }
}
