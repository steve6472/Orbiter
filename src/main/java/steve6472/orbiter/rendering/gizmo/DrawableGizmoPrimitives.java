package steve6472.orbiter.rendering.gizmo;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import org.joml.Vector3f;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public class DrawableGizmoPrimitives implements GizmoPrimitives
{
    public static final int LINE_COUNT = 4096;
    public static final int LINE_VERTEX_COUNT = 2;
    public static final int LINE_FLOAT_COUNT = 3 + 4;

    public static final int POINT_COUNT = 1024;
    public static final int POINT_VERTEX_COUNT = 1;
    public static final int POINT_FLOAT_COUNT = 3 + 4 + 1;

    public static final int TRI_COUNT = 8192;
    public static final int TRI_VERTEX_COUNT = 3;
    public static final int TRI_FLOAT_COUNT = 3 + 4;

    public final Float2ObjectMap<List<Line>> lines = new Float2ObjectArrayMap<>();
    public final Float2ObjectMap<List<Line>> blendLines = new Float2ObjectArrayMap<>();

    public final List<Point> points = new ArrayList<>();
    public final List<Point> blendPoints = new ArrayList<>();

    public final List<Tri> tris = new ArrayList<>();
    public final List<Tri> blendTris = new ArrayList<>();

    @Override
    public void point(Vector3f pos, int color, AlphaMultiplier alpha, float size)
    {
        float r = (float) ColorUtil.getRed(color) / 255f;
        float g = (float) ColorUtil.getGreen(color) / 255f;
        float b = (float) ColorUtil.getBlue(color) / 255f;
        float a = (float) ColorUtil.getAlpha(color) / 255f;
        Point line = new Point(pos, r, g, b, a, alpha, size);
        if (AlphaMultiplier.isBlend(a, alpha))
            blendPoints.add(line);
        else
            points.add(line);
    }

    @Override
    public void line(Vector3f start, Vector3f end, int color, AlphaMultiplier alpha, float width)
    {
        float r = (float) ColorUtil.getRed(color) / 255f;
        float g = (float) ColorUtil.getGreen(color) / 255f;
        float b = (float) ColorUtil.getBlue(color) / 255f;
        float a = (float) ColorUtil.getAlpha(color) / 255f;
        Line line = new Line(start, end, r, g, b, a, alpha, width);
        if (AlphaMultiplier.isBlend(a, alpha))
            blendLines.computeIfAbsent(width, _ -> new ArrayList<>()).add(line);
        else
            lines.computeIfAbsent(width, _ -> new ArrayList<>()).add(line);
    }

    @Override
    public void tri(Vector3f posA, Vector3f posB, Vector3f posC, int color, AlphaMultiplier alpha)
    {
        float r = (float) ColorUtil.getRed(color) / 255f;
        float g = (float) ColorUtil.getGreen(color) / 255f;
        float b = (float) ColorUtil.getBlue(color) / 255f;
        float a = (float) ColorUtil.getAlpha(color) / 255f;
        Tri tri = new Tri(posA, posB, posC, r, g, b, a, alpha);
        if (AlphaMultiplier.isBlend(a, alpha))
            blendTris.add(tri);
        else
            tris.add(tri);
    }

    public void createPrimitives(GizmoInstance instance)
    {
        instance.gizmo().create(this, instance.alphaMultiplier());
    }

    public void sortPrimitives(Vector3f viewPosition, float partialTicks)
    {
        blendLines.forEach((_, lines) -> sortMidpointSortable(lines, viewPosition));
        sortMidpointSortable(blendPoints, viewPosition);
        sortMidpointSortable(blendTris, viewPosition);
    }

    private void sortMidpointSortable(List<? extends MidpointSortable> list, Vector3f viewPosition)
    {
        final Vector3f lineMidpointE1 = new Vector3f();
        final Vector3f lineMidpointE2 = new Vector3f();

        list.sort((e1, e2) -> {
            float d1 = e1.midpoint(lineMidpointE1).distanceSquared(viewPosition);
            float d2 = e2.midpoint(lineMidpointE2).distanceSquared(viewPosition);

            return Float.compare(d2, d1);
        });
    }

    public void clearAll()
    {
        lines.clear();
        blendLines.clear();
        points.clear();
        blendPoints.clear();
        tris.clear();
        blendTris.clear();
    }

    public record Line(Vector3f start, Vector3f end, float r, float g, float b, float a, AlphaMultiplier alpha, float width) implements MidpointSortable
    {
        @Override
        public Vector3f midpoint(Vector3f store)
        {
            return store.set(start).add(end).mul(0.5f);
        }
    }

    public record Point(Vector3f pos, float r, float g, float b, float a, AlphaMultiplier alpha, float size) implements MidpointSortable
    {
        @Override
        public Vector3f midpoint(Vector3f store)
        {
            return store.set(pos);
        }
    }

    public record Tri(Vector3f posA, Vector3f posB, Vector3f posC, float r, float g, float b, float a, AlphaMultiplier alpha)  implements MidpointSortable
    {
        @Override
        public Vector3f midpoint(Vector3f store)
        {
            return store.set(posA).add(posB).add(posC).mul(0.33333334f);
        }
    }

    private interface MidpointSortable
    {
        Vector3f midpoint(Vector3f store);
    }
}
