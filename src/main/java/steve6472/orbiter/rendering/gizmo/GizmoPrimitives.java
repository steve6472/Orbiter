package steve6472.orbiter.rendering.gizmo;

import org.joml.Vector3f;
import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public interface GizmoPrimitives
{
    void point(Vector3f pos, int color, AlphaMultiplier alpha, float size);

    void line(Vector3f start, Vector3f end, int color, AlphaMultiplier alpha, float width);

    void tri(Vector3f a, Vector3f b, Vector3f c, int color, AlphaMultiplier alpha);

    default void quad(Vector3f a, Vector3f b, Vector3f c, Vector3f d, int color, AlphaMultiplier alpha)
    {
        tri(a, b, c, color, alpha);
        tri(c, d, a, color, alpha);
    }

//    void text(Vector3f pos, String text);
}
