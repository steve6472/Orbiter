package steve6472.orbiter.hex;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 * <a href="https://www.redblobgames.com/grids/hexagons/#pixel-to-hex">Inspiration</a>
 * Implements: Odd-r pointy top, axial coordinates
 */
public class HexGrid
{
    private final Layout layout;

    public HexGrid(Layout layout)
    {
        this.layout = layout;
    }

    public Hex pixelToHex(Vector2f point)
    {
        Orientation M = layout.orientation();
        Vector2f pt = new Vector2f((point.x - layout.origin().x) / layout.size().x, (point.y - layout.origin().y) / layout.size().y);
        double q = M.b0() * pt.x + M.b1() * pt.y;
        double r = M.b2() * pt.x + M.b3() * pt.y;
        return new FractionalHex(q, r, -q - r).round();
    }

    public Vector2f hexToPixel(Hex h)
    {
        Orientation M = layout.orientation();
        double x = (M.f0() * h.q() + M.f1() * h.r()) * layout.size().x;
        double y = (M.f2() * h.q() + M.f3() * h.r()) * layout.size().y;
        return new Vector2f((float) (x + layout.origin().x), (float) (y + layout.origin().y));
    }

    public Vector2f hexCornerOffset(int corner)
    {
        Vector2f size = layout.size();
        double angle = 2.0 * Math.PI * (layout.orientation().startAngle() + corner) / 6.0;
        return new Vector2f((float) (size.x * Math.cos(angle)), (float) (size.y * Math.sin((angle))));
    }

    public List<Vector2f> corners(Hex hex)
    {
        List<Vector2f> corners = new ArrayList<>(6);
        Vector2f center = hexToPixel(hex);
        for (int i = 0; i < 6; i++)
        {
            corners.add(new Vector2f(center).add(hexCornerOffset(i)));
        }
        return corners;
    }

    // pointy top
    public void iterateRectangle(int top, int bottom, int left, int right, Consumer<Hex> func)
    {
        for (int r = top; r <= bottom; r++)
        {
            int r_offset = r >> 1;
            for (int q = left - r_offset; q <= right - r_offset; q++)
            {
                Hex coords = new Hex(q, r, -q-r);
                func.accept(coords);
            }
        }
    }

    public Layout layout()
    {
        return layout;
    }
}
