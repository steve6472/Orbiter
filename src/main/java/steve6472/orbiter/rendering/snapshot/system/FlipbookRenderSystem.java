package steve6472.orbiter.rendering.snapshot.system;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.util.Profiler;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.assets.atlas.Atlas;
import steve6472.flare.assets.atlas.SpriteAtlas;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.OrbiterSBO;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.pairs.FlipbookParticlePair;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;
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
public class FlipbookRenderSystem extends CommonRenderSystem
{
    private static final int VERTEX_COUNT = 6;

    private static final Vector3f VERT_TR = new Vector3f(1, 1, 0);
    private static final Vector3f VERT_TL = new Vector3f(-1, 1, 0);
    private static final Vector3f VERT_BL = new Vector3f(-1, -1, 0);
    private static final Vector3f VERT_BR = new Vector3f(1, -1, 0);

    private final Client client;
    private final Profiler profiler = new Profiler(60);
    private final ParticleMaterial material;

    private static TextureSampler atlasSampler()
    {
        Atlas atlas = FlareRegistries.ATLAS.get(Constants.ATLAS_PARTICLE);
        if (atlas instanceof SpriteAtlas spriteAtlas)
        {
            return spriteAtlas.getAnimationAtlas().getSampler();
        }

        throw new RuntimeException();
    }

    public FlipbookRenderSystem(MasterRenderer masterRenderer, ParticleMaterial material, Client client)
    {
        super(masterRenderer, material.flipbookPipeline(),
            CommonBuilder
                .create()
                .entryImage(atlasSampler())
                .entrySBO(OrbiterSBO.PARTICLE_FLIPBOOK_ENTRIES.sizeof(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK_SHADER_STAGE_FRAGMENT_BIT)
                .vertexBuffer(Vertex.POS3F_UV.sizeof(), World.MAX_PARTICLES * VERTEX_COUNT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
        this.client = client;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        List<FlipbookParticlePair> list = currentRenderState.flipbookParticles.get(material);

        float partial = OrbiterApp.getInstance().partialTicks;

        profiler.start();

        // Let's make GC happy
        Matrix4f transform = new Matrix4f();
        Quaternionf rotation = new Quaternionf();
        Vector3f position = new Vector3f();

        VkBuffer buffer = flightFrame.getBuffer(2);
        int size = list.size() * vertex().sizeof() * VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        int renderedCount = 0;
        for (FlipbookParticlePair snapshotPair : list)
        {
            position.set(0, 0, 0);
            rotation.identity();

            FlipbookParticleSnapshot previousSnapshot = snapshotPair.previous();
            FlipbookParticleSnapshot currentSnapshot = snapshotPair.current();

            // Reset transform
            transform.identity();

            PlaneParticleRenderSystem.updateTransformation(previousSnapshot, currentSnapshot, transform, rotation, frameInfo.camera(), position, partial);

            if (Settings.INTERPOL_PARTICLES.get())
            {
                DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(previousSnapshot.x, previousSnapshot.y, previousSnapshot.z), 0.01f, DebugRender.RED));
                DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(currentSnapshot.x, currentSnapshot.y, currentSnapshot.z), 0.01f, DebugRender.GREEN));
            }

            addParticlePair(byteBuffer, transform);
            renderedCount++;
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), renderedCount * VERTEX_COUNT, 1, 0, 0);
        profiler.end();
    }

    private void addParticlePair(ByteBuffer buffer, Matrix4f transform)
    {
        Vector3f tr = new Vector3f(VERT_TR).mulPosition(transform);
        Vector3f tl = new Vector3f(VERT_TL).mulPosition(transform);
        Vector3f bl = new Vector3f(VERT_BL).mulPosition(transform);
        Vector3f br = new Vector3f(VERT_BR).mulPosition(transform);

        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
        buffer.putFloat(tl.x).putFloat(tl.y).putFloat(tl.z);
        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(br.x).putFloat(br.y).putFloat(br.z);
        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {
        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        List<FlipbookParticlePair> list = currentRenderState.flipbookParticles.get(material);

        VkBuffer buffer = flightFrame.getBuffer(1);
        int size = list.size() * OrbiterSBO.PARTICLE_FLIPBOOK_ENTRY.sizeof() * VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        long now = System.currentTimeMillis();
        float partial = OrbiterApp.getInstance().partialTicks;

        for (FlipbookParticlePair flipbookParticlePair : list)
        {
            FlipbookParticleSnapshot snapshot = flipbookParticlePair.current();
            int timeIndex = snapshot.framesTime.length - 1;
            for (int i = 0; i < snapshot.framesTime.length; i++)
            {
                long time = snapshot.framesTime[i];
                if (now < time)
                {
                    timeIndex = i;
                    break;
                }
            }
            int frameIndex, nextFrameIndex;
            frameIndex = snapshot.framesIndex[timeIndex];
            nextFrameIndex = snapshot.framesIndex[Math.min(timeIndex + 1, snapshot.framesIndex.length - 1)];

            byteBuffer.putFloat(snapshot.uv.x).putFloat(snapshot.uv.y).putFloat(snapshot.uv.z).putFloat(snapshot.uv.w);
            byteBuffer.putFloat(snapshot.singleSize.x).putFloat(snapshot.singleSize.y);
            byteBuffer.putInt(frameIndex);
            byteBuffer.putInt(nextFrameIndex);
            byteBuffer.putFloat(partial);
            byteBuffer.putInt(snapshot.flags);
            byteBuffer.putFloat(snapshot.pixelSize.x).putFloat(snapshot.pixelSize.y);
        }
    }

    @Override
    protected boolean shouldRender()
    {
        World world = client.getWorld();
        if (world == null)
            return false;

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null || currentRenderState.flipbookParticles.isEmpty())
            return false;

        List<FlipbookParticlePair> list = currentRenderState.flipbookParticles.get(material);
        //noinspection RedundantIfStatement
        if (list == null || list.isEmpty())
            return false;

        return true;
    }

    @Override
    public void cleanup()
    {
        super.cleanup();
    }
}
