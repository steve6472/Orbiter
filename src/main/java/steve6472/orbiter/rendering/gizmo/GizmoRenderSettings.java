package steve6472.orbiter.rendering.gizmo;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public interface GizmoRenderSettings
{
    GizmoRenderSettings alwaysOnTop();

    GizmoRenderSettings fadeOut();

    // interpolated, use stack to generate id for pair maybe

    GizmoRenderSettings stayForMs(long ms);
}
