package steve6472.orbiter.rendering.gizmo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
class GizmoList
{
    private final List<GizmoInstance> gizmos = new ArrayList<>();

    GizmoRenderSettings add(Gizmo gizmo)
    {
        GizmoInstance gizmoInstance = new GizmoInstance(gizmo);
        gizmos.add(gizmoInstance);
        return gizmoInstance;
    }

    List<GizmoInstance> getGizmosForRender()
    {
        List<GizmoInstance> result = new ArrayList<>(gizmos);
        long now = System.currentTimeMillis();
        gizmos.removeIf(instance -> instance.hasExpired(now));
        return result;
    }
}
