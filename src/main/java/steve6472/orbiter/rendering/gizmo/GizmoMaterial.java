package steve6472.orbiter.rendering.gizmo;

import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.orbiter.rendering.OrbiterPipelines;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public record GizmoMaterial(Settings settings,
                            PipelineConstructor pointPipeline,
                            PipelineConstructor linePipeline,
                            PipelineConstructor triPipeline)
{
    private record Pipelines(
        Function<Settings, PipelineConstructor> point,
        Function<Settings, PipelineConstructor> line,
        Function<Settings, PipelineConstructor> tri) {}

    private static final Pipelines PIPELINES = new Pipelines(OrbiterPipelines.GIZMO_POINT, OrbiterPipelines.GIZMO_LINE, OrbiterPipelines.GIZMO_TRI);

    public static final GizmoMaterial OPAQUE = new GizmoMaterial(new Settings(false, false));
    public static final GizmoMaterial OPAQUE_ON_TOP = new GizmoMaterial(new Settings(false, true));
    public static final GizmoMaterial BLEND = new GizmoMaterial(new Settings(true, false));
    public static final GizmoMaterial BLEND_ON_TOP = new GizmoMaterial(new Settings(true, true));

    public GizmoMaterial(Settings settings)
    {
        this(settings, PIPELINES.point.apply(settings), PIPELINES.line.apply(settings), PIPELINES.tri.apply(settings));
    }

    public record Settings(boolean hasAlpha, boolean alwaysOnTop) {}
}
