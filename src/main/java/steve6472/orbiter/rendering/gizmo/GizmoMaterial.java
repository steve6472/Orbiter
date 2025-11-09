package steve6472.orbiter.rendering.gizmo;

import org.jetbrains.annotations.Nullable;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.orbiter.rendering.OrbiterPipelines;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record GizmoMaterial(Settings settings, PipelineConstructor pipeline)
{
    private static final List<GizmoMaterial> MATERIALS = new ArrayList<>(6);

    public static final GizmoMaterial OPAQUE = new GizmoMaterial(new Settings(false, false), OrbiterPipelines.GIZMO);
    public static final GizmoMaterial OPAQUE_ON_TOP = new GizmoMaterial(new Settings(false, true), OrbiterPipelines.GIZMO);
    public static final GizmoMaterial BLEND = new GizmoMaterial(new Settings(true, false), OrbiterPipelines.GIZMO);
    public static final GizmoMaterial BLEND_ON_TOP = new GizmoMaterial(new Settings(true, true), OrbiterPipelines.GIZMO);

    public GizmoMaterial
    {
        MATERIALS.add(this);
    }

    public GizmoMaterial(Settings settings, Function<Settings, PipelineConstructor> pipeline)
    {
        this(settings, pipeline.apply(settings));
    }

    public static @Nullable GizmoMaterial fromGizmoInstace(Settings settings)
    {
        for (GizmoMaterial material : MATERIALS)
        {
            if (material.settings.equals(settings))
                return material;
        }

        return null;
    }

    public record Settings(boolean hasAlpha, boolean alwaysOnTop) {}
}
