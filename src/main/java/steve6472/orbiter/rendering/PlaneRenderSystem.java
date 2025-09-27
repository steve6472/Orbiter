package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.assets.atlas.Atlas;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.orbiter.Client;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.PlaneModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.RenderPipeline;
import steve6472.orlang.OrlangEnvironment;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class PlaneRenderSystem extends CommonRenderSystem
{
    public static final Family FAMILY = Family.all(Position.class, PlaneModel.class).get();
    private static final int VERTEX_COUNT = 6;

    private static final Vector3f VERT_TR = new Vector3f(1, 1, 0);
    private static final Vector3f VERT_TL = new Vector3f(-1, 1, 0);
    private static final Vector3f VERT_BL = new Vector3f(-1, -1, 0);
    private static final Vector3f VERT_BR = new Vector3f(1, -1, 0);

    private final Map<Entity, VkBuffer> buffers = new HashMap<>();
    private final Client client;
    private final RenderPipeline.Enum particlePipeline;

    private static TextureSampler atlasSampler()
    {
        Atlas atlas = FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH);
        return atlas.getSampler();
    }

    public PlaneRenderSystem(MasterRenderer masterRenderer, PipelineConstructor pipeline, Client client, RenderPipeline.Enum particlePipeline)
    {
        super(masterRenderer, pipeline,
            CommonBuilder
                .create()
                .entryImage(atlasSampler())
        );
        this.client = client;
        this.particlePipeline = particlePipeline;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> particle = world.particleEngine().getEntitiesFor(FAMILY);
        if (particle.size() == 0)
            return;

        Vector3f cameraPos = frameInfo.camera().viewPosition;

        List<Entity> sorted = new ArrayList<>(particle.size());
        for (Entity ent : particle)
        {
            RenderPipeline renderPipeline = ParticleComponents.RENDER_PIPELINE.get(ent);
            if ((renderPipeline != null && renderPipeline.value == this.particlePipeline) || (renderPipeline == null && this.particlePipeline == RenderPipeline.Enum.MODEL))
                sorted.add(ent);
        }
        sorted.sort((e1, e2) -> {
            var p1 = ParticleComponents.POSITION.get(e1);
            var p2 = ParticleComponents.POSITION.get(e2);

            float d1 = Vector3f.distanceSquared(p1.x, p1.y, p1.z, cameraPos.x, cameraPos.y, cameraPos.z);
            float d2 = Vector3f.distanceSquared(p2.x, p2.y, p2.z, cameraPos.x, cameraPos.y, cameraPos.z);

            return Float.compare(d2, d1);
        });

        // Let's make GC happy
        Struct[] structList = new Struct[VERTEX_COUNT];
        Vector4f color = new Vector4f(1, 1, 1, 1);
        Matrix4f transform = new Matrix4f();

        for (Entity entity : sorted)
        {
            VkBuffer buffer = buffers.computeIfAbsent(entity, _ ->
            {
                VkBuffer buf = new VkBuffer(
                    frameInfo.commandBuffer().getDevice(),
                    vertex().sizeof(),
                    VERTEX_COUNT,
                    VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
                );
                buf.map();
                return buf;
            });

            // Reset transform and color
            transform.identity();
            color.set(1, 1, 1, 1);

            PlaneModel planeModel = ParticleComponents.PLANE_MODEL.get(entity);
            OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

            ParticleRenderCommon.doTransform(entity, env, transform, frameInfo.camera(), false);
            ParticleRenderCommon.doTint(entity, env, color);

            Vector4f uv = planeModel.uv;
            Struct tr = vertex().create(new Vector3f(VERT_TR).mulPosition(transform), color, new Vector2f(uv.x, uv.w));
            Struct tl = vertex().create(new Vector3f(VERT_TL).mulPosition(transform), color, new Vector2f(uv.x, uv.y));
            Struct bl = vertex().create(new Vector3f(VERT_BL).mulPosition(transform), color, new Vector2f(uv.z, uv.y));
            Struct br = vertex().create(new Vector3f(VERT_BR).mulPosition(transform), color, new Vector2f(uv.z, uv.w));

            structList[0] = tr;
            structList[1] = tl;
            structList[2] = bl;
            structList[3] = bl;
            structList[4] = br;
            structList[5] = tr;

            buffer.writeToBuffer(vertex()::memcpy, structList);

            LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
            LongBuffer offsets = stack.longs(0);
            vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
            vkCmdDraw(frameInfo.commandBuffer(), VERTEX_COUNT, 1, 0, 0);
        }
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}

    @Override
    public void cleanup()
    {
        super.cleanup();
        buffers.values().forEach(VkBuffer::cleanup);
        buffers.clear();
    }
}
