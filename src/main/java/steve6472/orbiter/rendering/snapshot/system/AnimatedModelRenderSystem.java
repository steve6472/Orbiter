package steve6472.orbiter.rendering.snapshot.system;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Push;
import steve6472.flare.struct.def.SBO;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.pairs.AnimatedModelPair;
import steve6472.orbiter.rendering.snapshot.snapshots.AnimatedModelSnapshot;
import steve6472.orbiter.world.World;

import java.util.List;

import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public class AnimatedModelRenderSystem extends CommonRenderSystem
{
    private final Client client;

    public AnimatedModelRenderSystem(MasterRenderer masterRenderer, PipelineConstructor pipeline, Client client)
    {
        super(masterRenderer, pipeline, CommonBuilder.create()
            .entrySBO(SBO.BONES.sizeof(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK_SHADER_STAGE_VERTEX_BIT)
            .entryImage(FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH).getSampler()));
        this.client = client;
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack stack)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("AnimatedModelRenderSystem");
        super.render(frameInfo, stack);
        profiler.pop();
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null || currentRenderState.animatedModels.isEmpty())
            return;

        float partial = OrbiterApp.getInstance().partialTicks;

        VkBuffer buffer = flightFrame.getBuffer(0);

        int offset = 0;
        for (AnimatedModelPair snapshotPair : currentRenderState.animatedModels)
        {
            AnimatedModelSnapshot previousSnapshot = snapshotPair.previous();
            AnimatedModelSnapshot currentSnapshot = snapshotPair.current();

            Matrix4f[] interpolated = new Matrix4f[currentSnapshot.transformations.length];

            for (int i = 0; i < currentSnapshot.transformations.length; i++)
            {
                interpolated[i] = previousSnapshot.transformations[i].lerp(currentSnapshot.transformations[i], partial, new Matrix4f());
            }

            var sbo = SBO.BONES.create((Object) interpolated);

            buffer.writeToBuffer(SBO.BONES::memcpy, List.of(sbo), interpolated.length * 64L, offset);
            buffer.flush(interpolated.length * 64L, offset);

            Struct struct = Push.SKIN.create(interpolated.length, offset / 64);
            Push.SKIN.push(struct, frameInfo.commandBuffer(), pipeline().pipelineLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0);

            currentSnapshot.model.bind(frameInfo.commandBuffer());
            currentSnapshot.model.draw(frameInfo.commandBuffer());

            offset += interpolated.length * 64;
        }
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
