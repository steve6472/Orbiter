package steve6472.orbiter.rendering.gizmo.shapes;

import org.joml.Vector3f;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.rendering.gizmo.Gizmo;
import steve6472.orbiter.rendering.gizmo.GizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record FilledLineCuboid(Vector3f start, Vector3f end, int lineColor, int fillColor, float lineWidth) implements Gizmo
{
    public FilledLineCuboid(Vector3f center, float halfSize, int lineColor, int fillColor, float lineWidth)
    {
        this(new Vector3f(center).sub(halfSize, halfSize, halfSize), new Vector3f(center).add(halfSize, halfSize, halfSize), lineColor, fillColor, lineWidth);
    }

    public FilledLineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int lineColor, int fillColor, float lineWidth)
    {
        this(new Vector3f(center).sub(halfWidth, halfHeight, halfDepth), new Vector3f(center).add(halfWidth, halfHeight, halfDepth), lineColor, fillColor, lineWidth);
    }

    @Override
    public void create(GizmoPrimitives primitives, AlphaMultiplier alpha)
    {
        Vector3f p0 = new Vector3f(start.x, start.y, start.z);
        Vector3f p1 = new Vector3f(end.x, start.y, start.z);
        Vector3f p2 = new Vector3f(end.x, end.y, start.z);
        Vector3f p3 = new Vector3f(start.x, end.y, start.z);

        Vector3f p4 = new Vector3f(start.x, start.y, end.z);
        Vector3f p5 = new Vector3f(end.x, start.y, end.z);
        Vector3f p6 = new Vector3f(end.x, end.y, end.z);
        Vector3f p7 = new Vector3f(start.x, end.y, end.z);

        // Front face
        if (lineColor != fillColor || ColorUtil.getAlpha(lineColor) != 255)
        {
            primitives.line(p0, p1, lineColor, alpha, lineWidth);
            primitives.line(p1, p2, lineColor, alpha, lineWidth);
            primitives.line(p2, p3, lineColor, alpha, lineWidth);
            primitives.line(p3, p0, lineColor, alpha, lineWidth);

            // Back face
            primitives.line(p4, p5, lineColor, alpha, lineWidth);
            primitives.line(p5, p6, lineColor, alpha, lineWidth);
            primitives.line(p6, p7, lineColor, alpha, lineWidth);
            primitives.line(p7, p4, lineColor, alpha, lineWidth);

            // Connecting edges
            primitives.line(p0, p4, lineColor, alpha, lineWidth);
            primitives.line(p1, p5, lineColor, alpha, lineWidth);
            primitives.line(p2, p6, lineColor, alpha, lineWidth);
            primitives.line(p3, p7, lineColor, alpha, lineWidth);
        }

        // --- Filled faces using triangles ---

        // Front face (Z = start.z)
        primitives.tri(p2, p1, p0, fillColor, alpha);
        primitives.tri(p0, p3, p2, fillColor, alpha);

        // Back face (Z = end.z)
        primitives.tri(p7, p4, p5, fillColor, alpha);
        primitives.tri(p5, p6, p7, fillColor, alpha);

        // Left face (X = start.x)
        primitives.tri(p3, p0, p4, fillColor, alpha);
        primitives.tri(p4, p7, p3, fillColor, alpha);

        // Right face (X = end.x)
        primitives.tri(p6, p5, p1, fillColor, alpha);
        primitives.tri(p1, p2, p6, fillColor, alpha);

        // Top face (Y = end.y)
        primitives.tri(p6, p2, p3, fillColor, alpha);
        primitives.tri(p3, p7, p6, fillColor, alpha);

        // Bottom face (Y = start.y)
        primitives.tri(p1, p5, p4, fillColor, alpha);
        primitives.tri(p4, p0, p1, fillColor, alpha);
    }
}
