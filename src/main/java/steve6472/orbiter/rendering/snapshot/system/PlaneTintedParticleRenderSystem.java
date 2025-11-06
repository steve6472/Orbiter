package steve6472.orbiter.rendering.snapshot.system;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.assets.atlas.Atlas;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.struct.def.Vertex;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.pairs.PlaneTintedParticlePair;
import steve6472.orbiter.rendering.snapshot.snapshots.PlaneTintedParticleSnapshot;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.World;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class PlaneTintedParticleRenderSystem extends CommonRenderSystem
{
    private static final int VERTEX_COUNT = 6;

    private static final Vector3f VERT_TR = new Vector3f(1, 1, 0);
    private static final Vector3f VERT_TL = new Vector3f(-1, 1, 0);
    private static final Vector3f VERT_BL = new Vector3f(-1, -1, 0);
    private static final Vector3f VERT_BR = new Vector3f(1, -1, 0);

    private final Client client;
    private final ParticleMaterial material;

    private static TextureSampler atlasSampler()
    {
        Atlas atlas = FlareRegistries.ATLAS.get(Constants.ATLAS_PARTICLE);
        return atlas.getSampler();
    }

    public PlaneTintedParticleRenderSystem(MasterRenderer masterRenderer, ParticleMaterial material, Client client)
    {
        super(masterRenderer, material.planePipeline(),
            CommonBuilder
                .create()
                .entryImage(atlasSampler())
                .vertexBuffer(Vertex.POS3F_COL4F_UV.sizeof(), World.MAX_PARTICLES * VERTEX_COUNT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
        this.client = client;
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack stack)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("PlaneTintedParticleRenderSystem");
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
        if (currentRenderState == null || currentRenderState.tintedParticles.isEmpty())
            return;

        List<PlaneTintedParticlePair> list = currentRenderState.tintedParticles.get(material);
        if (list == null || list.isEmpty())
            return;

        float partial = OrbiterApp.getInstance().partialTicks;

        // Let's make GC happy
        Vector4f color = new Vector4f(1, 1, 1, 1);
        Matrix4f transform = new Matrix4f();
        Quaternionf rotation = new Quaternionf();
        Vector3f position = new Vector3f();

        VkBuffer buffer = flightFrame.getBuffer(1);
        int size = list.size() * vertex().sizeof() * VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        int renderedCount = 0;
        for (PlaneTintedParticlePair snapshotPair : list)
        {
            position.set(0, 0, 0);
            rotation.identity();

            PlaneTintedParticleSnapshot previousSnapshot = snapshotPair.previous();
            PlaneTintedParticleSnapshot currentSnapshot = snapshotPair.current();

            // Reset transform and color
            transform.identity();
            color.set(1, 1, 1, 1);

            PlaneParticleRenderSystem.updateTransformation(previousSnapshot, currentSnapshot, transform, rotation, frameInfo.camera(), position, partial);

            if (Settings.INTERPOL_PARTICLES.get())
            {
                DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(previousSnapshot.x, previousSnapshot.y, previousSnapshot.z), 0.01f, DebugRender.RED));
                DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(currentSnapshot.x, currentSnapshot.y, currentSnapshot.z), 0.01f, DebugRender.GREEN));
            }

            // TODO: interpolate properly
            color.set(currentSnapshot.r, currentSnapshot.g, currentSnapshot.b, currentSnapshot.a);

            Vector4f uv = currentSnapshot.uv;
            addParticlePair(byteBuffer, transform, color, uv);
            renderedCount++;
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), renderedCount * VERTEX_COUNT, 1, 0, 0);
    }

    private void addParticlePair(ByteBuffer buffer, Matrix4f transform, Vector4f color, Vector4f uv)
    {
        Vector3f tr = new Vector3f(VERT_TR).mulPosition(transform);
        Vector3f tl = new Vector3f(VERT_TL).mulPosition(transform);
        Vector3f bl = new Vector3f(VERT_BL).mulPosition(transform);
        Vector3f br = new Vector3f(VERT_BR).mulPosition(transform);

        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.x).putFloat(uv.w);

        buffer.putFloat(tl.x).putFloat(tl.y).putFloat(tl.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.x).putFloat(uv.y);

        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.z).putFloat(uv.y);

        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.z).putFloat(uv.y);

        buffer.putFloat(br.x).putFloat(br.y).putFloat(br.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.z).putFloat(uv.w);

        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
        buffer.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(color.w);
        buffer.putFloat(uv.x).putFloat(uv.w);
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
