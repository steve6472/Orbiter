package steve6472.orbiter.rendering.gizmo.shapes;

import org.joml.Vector3f;
import org.joml.Vector4f;
import steve6472.orbiter.rendering.gizmo.Gizmo;
import steve6472.orbiter.rendering.gizmo.GizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record LineCuboid(Vector3f start, Vector3f end, int color, float lineWidth) implements Gizmo
{
    public LineCuboid(Vector3f center, float halfSize, int color, float lineWidth)
    {
        this(new Vector3f(center).sub(halfSize, halfSize, halfSize), new Vector3f(center).add(halfSize, halfSize, halfSize), color, lineWidth);
    }

    public LineCuboid(Vector3f center, float halfWidth, float halfHeight, float halfDepth, int color, float lineWidth)
    {
        this(new Vector3f(center).sub(halfWidth, halfHeight, halfDepth), new Vector3f(center).add(halfWidth, halfHeight, halfDepth), color, lineWidth);
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
        primitives.line(p0, p1, color, alpha, lineWidth);
        primitives.line(p1, p2, color, alpha, lineWidth);
        primitives.line(p2, p3, color, alpha, lineWidth);
        primitives.line(p3, p0, color, alpha, lineWidth);

        // Back face
        primitives.line(p4, p5, color, alpha, lineWidth);
        primitives.line(p5, p6, color, alpha, lineWidth);
        primitives.line(p6, p7, color, alpha, lineWidth);
        primitives.line(p7, p4, color, alpha, lineWidth);

        // Connecting edges
        primitives.line(p0, p4, color, alpha, lineWidth);
        primitives.line(p1, p5, color, alpha, lineWidth);
        primitives.line(p2, p6, color, alpha, lineWidth);
        primitives.line(p3, p7, color, alpha, lineWidth);
    }
}
