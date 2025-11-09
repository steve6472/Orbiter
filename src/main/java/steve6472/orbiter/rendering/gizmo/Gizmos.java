package steve6472.orbiter.rendering.gizmo;

import org.joml.Vector3f;
import steve6472.orbiter.rendering.gizmo.shapes.FilledLineCuboid;
import steve6472.orbiter.rendering.gizmo.shapes.LineCuboid;
import steve6472.orbiter.rendering.gizmo.shapes.LineGizmo;
import steve6472.orbiter.rendering.gizmo.shapes.PointGizmo;

import java.util.List;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
@SuppressWarnings("unused")
public class Gizmos
{
    private static final ThreadLocal<GizmoList> GIZMOS = ThreadLocal.withInitial(GizmoList::new);

    private static final float DEFAULT_LINE_WIDTH = 2f;
    private static final float DEFAULT_POINT_SIZE = 5f;

    public static GizmoRenderSettings addGizmo(Gizmo gizmo)
    {
        GizmoList gizmoList = GIZMOS.get();
        if (gizmoList == null)
            throw new IllegalStateException("No gizmo list registered for thread '" + Thread.currentThread().getName() + "'");
        return gizmoList.add(gizmo);
    }

    public static List<GizmoInstance> getGizmosForRender()
    {
        return GIZMOS.get().getGizmosForRender();
    }

    public static GizmoRenderSettings line(Vector3f start, Vector3f end, int color, float width)
    {
        return addGizmo(new LineGizmo(start, end, color, width));
    }

    public static GizmoRenderSettings line(Vector3f start, Vector3f end, int color)
    {
        return addGizmo(new LineGizmo(start, end, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings point(Vector3f pos, int color, float size)
    {
        return addGizmo(new PointGizmo(pos, color, size));
    }

    public static GizmoRenderSettings point(Vector3f pos, int color)
    {
        return addGizmo(new PointGizmo(pos, color, DEFAULT_POINT_SIZE));
    }

    /*
     * Line cuboid
     */
    public static GizmoRenderSettings lineCuboid(Vector3f start, Vector3f end, int color, float lineWidth)
    {
        return addGizmo(new LineCuboid(start, end, color, lineWidth));
    }

    public static GizmoRenderSettings lineCuboid(Vector3f start, Vector3f end, int color)
    {
        return addGizmo(new LineCuboid(start, end, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings lineCuboid(Vector3f center, float halfSize, int color, float lineWidth)
    {
        return addGizmo(new LineCuboid(center, halfSize, color, lineWidth));
    }

    public static GizmoRenderSettings lineCuboid(Vector3f center, float halfSize, int color)
    {
        return addGizmo(new LineCuboid(center, halfSize, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings lineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int color, float lineWidth)
    {
        return addGizmo(new LineCuboid(center, halfWidth, halfHeight, halfDepth, color, lineWidth));
    }

    public static GizmoRenderSettings lineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int color)
    {
        return addGizmo(new LineCuboid(center, halfWidth, halfHeight, halfDepth, color, DEFAULT_LINE_WIDTH));
    }

    /*
     * Filled line cuboid
     */

    public static GizmoRenderSettings filledLineCuboid(Vector3f start, Vector3f end, int lineColor, int fillColor, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(start, end, lineColor, fillColor, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f start, Vector3f end, int lineColor, int fillColor)
    {
        return addGizmo(new FilledLineCuboid(start, end, lineColor, fillColor, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfSize, int lineColor, int fillColor, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, halfSize, lineColor, fillColor, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfSize, int lineColor, int fillColor)
    {
        return addGizmo(new FilledLineCuboid(center, halfSize, lineColor, fillColor, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int lineColor, int fillColor, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, halfWidth, halfHeight, halfDepth, lineColor, fillColor, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int lineColor, int fillColor)
    {
        return addGizmo(new FilledLineCuboid(center, halfWidth, halfHeight, halfDepth, lineColor, fillColor, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfSize, int color)
    {
        return addGizmo(new FilledLineCuboid(center, halfSize, color, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboidFromSize(Vector3f center, float width, float height, float depth, int lineColor, int fillColor, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, width / 2f, height / 2f, depth / 2f, lineColor, fillColor, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboidFromSize(Vector3f center, float width, float height, float depth, int lineColor, int fillColor)
    {
        return addGizmo(new FilledLineCuboid(center, width / 2f, height / 2f, depth / 2f, lineColor, fillColor, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f start, Vector3f end, int color, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(start, end, color, color, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f start, Vector3f end, int color)
    {
        return addGizmo(new FilledLineCuboid(start, end, color, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfSize, int color, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, halfSize, color, color, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int color, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, halfWidth, halfHeight, halfDepth, color, color, lineWidth));
    }

    public static GizmoRenderSettings filledLineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int color)
    {
        return addGizmo(new FilledLineCuboid(center, halfWidth, halfHeight, halfDepth, color, color, DEFAULT_LINE_WIDTH));
    }
    public static GizmoRenderSettings filledLineCuboidFromSize(Vector3f center, float width, float height, float depth, int color)
    {
        return addGizmo(new FilledLineCuboid(center, width / 2f, height / 2f, depth / 2f, color, color, DEFAULT_LINE_WIDTH));
    }

    public static GizmoRenderSettings filledLineCuboidFromSize(Vector3f center, float width, float height, float depth, int color, float lineWidth)
    {
        return addGizmo(new FilledLineCuboid(center, width / 2f, height / 2f, depth / 2f, color, color, lineWidth));
    }
}
