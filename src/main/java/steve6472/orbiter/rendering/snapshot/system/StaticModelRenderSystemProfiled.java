package steve6472.orbiter.rendering.snapshot.system;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.render.StaticModelRenderImpl;
import steve6472.flare.render.StaticModelRenderSystem;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;

/**
 * Created by steve6472
 * Date: 11/5/2025
 * Project: Orbiter <br>
 */
public class StaticModelRenderSystemProfiled extends StaticModelRenderSystem
{
    public StaticModelRenderSystemProfiled(MasterRenderer masterRenderer, StaticModelRenderImpl renderImpl, PipelineConstructor pipeline)
    {
        super(masterRenderer, renderImpl, pipeline);
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack stack)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("StaticModelRenderSystemProfiled");
        super.render(frameInfo, stack);
        profiler.pop();
    }
}
