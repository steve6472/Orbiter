package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.assets.atlas.Atlas;
import steve6472.flare.assets.atlas.SpriteAtlas;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.FlipbookModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orlang.OrlangEnvironment;

import java.nio.LongBuffer;
import java.util.*;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class FlipbookRenderSystem extends CommonRenderSystem
{
    private static final Family FAMILY = Family.all(Position.class, FlipbookModel.class).get();
    private static final int VERTEX_COUNT = 6;

    private static final Vector3f VERT_TR = new Vector3f(1, 1, 0);
    private static final Vector3f VERT_TL = new Vector3f(-1, 1, 0);
    private static final Vector3f VERT_BL = new Vector3f(-1, -1, 0);
    private static final Vector3f VERT_BR = new Vector3f(1, -1, 0);

    private final Map<Entity, VkBuffer> buffers = new HashMap<>();
    private final Client client;

    private static TextureSampler atlasSampler()
    {
        Atlas atlas = FlareRegistries.ATLAS.get(Constants.ATLAS_PARTICLE);
        if (atlas instanceof SpriteAtlas spriteAtlas)
        {
            return spriteAtlas.getAnimationAtlas().getSampler();
        }

        throw new RuntimeException();
    }

    public FlipbookRenderSystem(MasterRenderer masterRenderer, PipelineConstructor pipeline, Client client)
    {
        super(masterRenderer, pipeline,
            CommonBuilder
                .create()
                .entryImage(atlasSampler())
        );
        this.client = client;
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

        long now = System.currentTimeMillis();

        Struct[] structList = new Struct[VERTEX_COUNT];
        Matrix4f transform = new Matrix4f();

        for (Entity entity : particle)
        {
            transform.identity();

            VkBuffer buffer = buffers.computeIfAbsent(entity, _ ->
            {
                VkBuffer buf = new VkBuffer(
                    frameInfo.commandBuffer().getDevice(),
                    vertex().sizeof(),
                    VERTEX_COUNT,
                    VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
                buf.map();
                return buf;
            });

            FlipbookModel flipbook = ParticleComponents.FLIPBOOK_MODEL.get(entity);
            flipbook.startAnimAfterReset(now);
            flipbook.tick(now);
            OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

            ParticleRenderCommon.doTransform(entity, env, transform, frameInfo.camera(), false);

            Struct tr = OrbiterVertex.POS3F.create(new Vector3f(VERT_TR).mulPosition(transform));
            Struct tl = OrbiterVertex.POS3F.create(new Vector3f(VERT_TL).mulPosition(transform));
            Struct bl = OrbiterVertex.POS3F.create(new Vector3f(VERT_BL).mulPosition(transform));
            Struct br = OrbiterVertex.POS3F.create(new Vector3f(VERT_BR).mulPosition(transform));

            structList[0] = tr;
            structList[1] = tl;
            structList[2] = bl;
            structList[3] = bl;
            structList[4] = br;
            structList[5] = tr;

            buffer.writeToBuffer(vertex()::memcpy, structList);

            Struct animData = flipbook.toStruct(now);
            OrbiterPush.FLIPBOOK_ANIM_DATA.push(animData, frameInfo.commandBuffer(), pipeline().pipelineLayout(), VK_SHADER_STAGE_FRAGMENT_BIT, 0);

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
