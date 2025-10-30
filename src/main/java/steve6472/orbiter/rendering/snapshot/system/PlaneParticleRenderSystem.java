package steve6472.orbiter.rendering.snapshot.system;

import com.badlogic.ashley.core.Family;
import org.joml.*;
import org.lwjgl.system.MemoryStack;
import steve6472.core.util.Profiler;
import steve6472.flare.Camera;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.assets.atlas.Atlas;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.BillboardUtil;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.snapshot.pairs.ParticlePair;
import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.components.PlaneModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.TintGradient;
import steve6472.orbiter.world.particle.components.TintRGBA;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class PlaneParticleRenderSystem extends CommonRenderSystem
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
        return atlas.getSampler();
    }

    public PlaneParticleRenderSystem(MasterRenderer masterRenderer, ParticleMaterial material, Client client)
    {
        super(masterRenderer, material.pipeline(),
            CommonBuilder
                .create()
                .entryImage(atlasSampler())
                .vertexBuffer(Vertex.POS3F_UV.sizeof(), World.MAX_PARTICLES * VERTEX_COUNT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
        this.client = client;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null || currentRenderState.particles.isEmpty())
            return;

        List<ParticlePair> list = currentRenderState.particles.get(material);
        if (list == null || list.isEmpty())
            return;

        float partial = OrbiterApp.getInstance().partialTicks;

        profiler.start();

        // Let's make GC happy
        Matrix4f transform = new Matrix4f();
        Quaternionf rotation = new Quaternionf();
        Vector3f position = new Vector3f();

        VkBuffer buffer = flightFrame.getBuffer(1);
        int size = list.size() * vertex().sizeof() * VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        int renderedCount = 0;
        for (ParticlePair snapshotPair : list)
        {
            position.set(0, 0, 0);
            rotation.identity();

            ParticleSnapshot previousSnapshot = snapshotPair.previous();
            ParticleSnapshot currentSnapshot = snapshotPair.current();

            // Reset transform and color
            transform.identity();

            updateTransformation(previousSnapshot, currentSnapshot, transform, rotation, frameInfo.camera(), position, partial);

            Vector4f uv = currentSnapshot.uv;
            addParticlePair(byteBuffer, transform, uv);
            renderedCount++;
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), renderedCount * VERTEX_COUNT, 1, 0, 0);

        profiler.end();
//        ProfilerPrint.sout(profiler, "Count", renderedCount);
    }

    private void addParticlePair(ByteBuffer buffer, Matrix4f transform, Vector4f uv)
    {
        Vector3f tr = new Vector3f(VERT_TR).mulPosition(transform);
        Vector3f tl = new Vector3f(VERT_TL).mulPosition(transform);
        Vector3f bl = new Vector3f(VERT_BL).mulPosition(transform);
        Vector3f br = new Vector3f(VERT_BR).mulPosition(transform);

        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
        buffer.putFloat(uv.x).putFloat(uv.w);

        buffer.putFloat(tl.x).putFloat(tl.y).putFloat(tl.z);
        buffer.putFloat(uv.x).putFloat(uv.y);

        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(uv.z).putFloat(uv.y);

        buffer.putFloat(bl.x).putFloat(bl.y).putFloat(bl.z);
        buffer.putFloat(uv.z).putFloat(uv.y);

        buffer.putFloat(br.x).putFloat(br.y).putFloat(br.z);
        buffer.putFloat(uv.z).putFloat(uv.w);

        buffer.putFloat(tr.x).putFloat(tr.y).putFloat(tr.z);
        buffer.putFloat(uv.x).putFloat(uv.w);
    }

    private void updateTransformation(ParticleSnapshot previousSnapshot, ParticleSnapshot currentSnapshot, Matrix4f transform, Quaternionf rotation, Camera camera, Vector3f position, float partialTicks)
    {
        position.set(currentSnapshot.rx, currentSnapshot.ry, currentSnapshot.rz);

        BillboardUtil.makeBillboard(
            transform,
            position,
            previousSnapshot.x - currentSnapshot.x,
            previousSnapshot.y - currentSnapshot.y,
            previousSnapshot.z - currentSnapshot.z,
            camera,
            currentSnapshot.billboard);

        transform.setTranslation(position);
        rotation.set(previousSnapshot.parentRotation);
        rotation.slerp(currentSnapshot.parentRotation, partialTicks);
        transform.rotate(rotation);
        transform.rotateZ(lerp(previousSnapshot.rotation, currentSnapshot.rotation, partialTicks));
        transform.scale(lerp(previousSnapshot.scaleX, currentSnapshot.scaleX, partialTicks), lerp(previousSnapshot.scaleY, currentSnapshot.scaleY, partialTicks), 1);
    }

    public static float lerp(float start, float end, float value)
    {
        return start + value * (end - start);
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
