package steve6472.orbiter.rendering.gizmo;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import org.joml.Vector3f;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final int POINT_FLOAT_COUNT = 3 + 4;

    public final Float2ObjectMap<List<Line>> lines = new Float2ObjectArrayMap<>();
    public final Float2ObjectMap<List<Line>> blendLines = new Float2ObjectArrayMap<>();

    @Override
    public void point(Vector3f pos, int color, AlphaMultiplier alpha, float size)
    {
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
    public void tri(Vector3f a, Vector3f b, Vector3f c, int color, AlphaMultiplier alpha)
    {

    }

    public void createPrimitives(GizmoInstance instance)
    {
        instance.gizmo().create(this, instance.alphaMultiplier());
    }

    public void sortPrimitives(Vector3f viewPosition, float partialTicks)
    {
        blendLines.forEach((_, lines) -> sortMidpointSortable(lines, viewPosition));
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

    public record Line(Vector3f start, Vector3f end, float r, float g, float b, float a, AlphaMultiplier alpha, float width) implements MidpointSortable
    {
        public Vector3f midpoint(Vector3f store)
        {
            return store.set(start).add(end).mul(0.5f);
        }
    }

    public record Point(Vector3f pos, float r, float g, float b, float a, AlphaMultiplier alpha, float size) { }
    public record Tri(Vector3f posA, Vector3f posB, Vector3f posC, float r, float g, float b, float a, AlphaMultiplier alpha) { }

    private interface MidpointSortable
    {
        Vector3f midpoint(Vector3f store);
    }
}
