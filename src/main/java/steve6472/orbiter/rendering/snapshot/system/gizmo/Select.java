package steve6472.orbiter.rendering.snapshot.system.gizmo;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
class Select
{
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T select(GizmoMaterial material, Function<DrawableGizmoPrimitives, T> normal, Function<DrawableGizmoPrimitives, T> blend)
    {
        WorldRenderState renderState = OrbiterApp.getInstance().currentRenderState;
        if (renderState == null)
            return selectPerFrameGizmo(material, normal, blend);

        T perTick = selectPerTickGizmos(renderState, material, normal, blend);
        T perFrame = selectPerFrameGizmo(material, normal, blend);

        if (perTick instanceof List<?> pt && perFrame instanceof List<?> pl)
        {
            List unionList = new ArrayList<>(pt.size() + pl.size());
            unionList.addAll(pt);
            unionList.addAll(pl);
            return (T) unionList;
        }
        else if (perTick instanceof Float2ObjectMap<?> pt && perFrame instanceof Float2ObjectMap<?> pf)
        {
            Float2ObjectMap<List> unionMap = new Float2ObjectArrayMap(pt.size() + pf.size());
            pt.forEach((k, v) -> unionMap.put((float) k, (List) v));
            pf.forEach((k, v) ->
            {
                ArrayList o = (ArrayList) unionMap.computeIfAbsent(k, _ -> new ArrayList<>());
                o.addAll((List) v);
            });

            return (T) unionMap;
        }
        else
        {
            throw new IllegalStateException(
                "Can not create union with per tick type '" + perTick.getClass().getCanonicalName() + "' and per frame type '" + perFrame.getClass().getCanonicalName() + "'");
        }
    }

    private static <T> T selectPerTickGizmos(WorldRenderState renderState, GizmoMaterial material, Function<DrawableGizmoPrimitives, T> normal, Function<DrawableGizmoPrimitives, T> blend)
    {
        GizmoMaterial.Settings settings = material.settings();
        if (settings.alwaysOnTop())
        {
            DrawableGizmoPrimitives drawableGizmoPrimitivesAlwaysOnTop = renderState.drawableGizmoPrimitivesAlwaysOnTop;
            if (settings.hasAlpha())
                return blend.apply(drawableGizmoPrimitivesAlwaysOnTop);
            else
                return normal.apply(drawableGizmoPrimitivesAlwaysOnTop);
        } else
        {
            DrawableGizmoPrimitives drawableGizmoPrimitives = renderState.drawableGizmoPrimitives;
            if (settings.hasAlpha())
                return blend.apply(drawableGizmoPrimitives);
            else
                return normal.apply(drawableGizmoPrimitives);
        }
    }

    private static <T> T selectPerFrameGizmo(GizmoMaterial material, Function<DrawableGizmoPrimitives, T> normal, Function<DrawableGizmoPrimitives, T> blend)
    {
        GizmoMaterial.Settings settings = material.settings();
        if (settings.alwaysOnTop())
        {
            DrawableGizmoPrimitives drawableGizmoPrimitivesAlwaysOnTop = OrbiterApp.getInstance().drawableGizmoPrimitivesAlwaysOnTop;
            if (settings.hasAlpha())
                return blend.apply(drawableGizmoPrimitivesAlwaysOnTop);
            else
                return normal.apply(drawableGizmoPrimitivesAlwaysOnTop);
        } else
        {
            DrawableGizmoPrimitives drawableGizmoPrimitives = OrbiterApp.getInstance().drawableGizmoPrimitives;
            if (settings.hasAlpha())
                return blend.apply(drawableGizmoPrimitives);
            else
                return normal.apply(drawableGizmoPrimitives);
        }
    }
}
