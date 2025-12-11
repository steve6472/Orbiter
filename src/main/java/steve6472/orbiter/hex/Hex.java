package steve6472.orbiter.hex;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 */
public record Hex(int q, int r, int s)
{
    /*
     * Arithmetic
     */

    public Hex add(Hex right)
    {
        return new Hex(q + right.q, r + right.r, s + right.s);
    }

    public Hex sub(Hex right)
    {
        return new Hex(q - right.q, r - right.r, s - right.s);
    }

    public Hex mul(Hex right)
    {
        return new Hex(q * right.q, r * right.r, s * right.s);
    }

    /*
     * Distance
     */

    public int len()
    {
        return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
    }

    public int distance(Hex other)
    {
        return sub(other).len();
    }

    /*
     * Neighbor
     */

    public static final Hex[] DIRECTIONS = new Hex[] {
        new Hex(1, 0, -1),
        new Hex(1, -1, 0),
        new Hex(0, -1, 1),
        new Hex(-1, 0, 1),
        new Hex(-1, 1, 0),
        new Hex(0, 1, -1)
    };

    public Hex neighbor(int direction)
    {
        return add(DIRECTIONS[direction]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Hex hex = (Hex) o;
        return q == hex.q && r == hex.r;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(q, r);
    }
}
