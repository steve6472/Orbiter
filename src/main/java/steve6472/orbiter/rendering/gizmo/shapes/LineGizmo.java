package steve6472.orbiter.rendering.gizmo.shapes;

import org.joml.Vector3f;
import steve6472.orbiter.rendering.gizmo.Gizmo;
import steve6472.orbiter.rendering.gizmo.GizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record LineGizmo(Vector3f start, Vector3f end, int color, float width) implements Gizmo
{
    @Override
    public void create(GizmoPrimitives primitives, AlphaMultiplier alpha)
    {
        primitives.line(start, end, color, alpha, width);
    }
}
