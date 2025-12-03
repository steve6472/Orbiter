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
public record SphereGizmo(Vector3f position, float radius, int quality, int color, float lineWidth) implements Gizmo
{
    @Override
    public void create(GizmoPrimitives primitives, AlphaMultiplier alpha)
    {
        new HemisphereGizmo(position, radius, quality, true, color, lineWidth).create(primitives, alpha);
        new HemisphereGizmo(position, radius, quality, false, color, lineWidth).create(primitives, alpha);
    }
}
