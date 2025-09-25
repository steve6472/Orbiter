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
    private static final Vector3f[] VERTICIES = new Vector3f[]
        {
            new Vector3f(1, 1, 0),
            new Vector3f(-1, 1, 0),
            new Vector3f(-1, -1, 0),
            new Vector3f(-1, -1, 0),
            new Vector3f(1, -1, 0),
            new Vector3f(1, 1, 0)
        };

    // position & data
    private final Map<Entity, VkBuffer> buffers = new HashMap<>();
    private final List<VkBuffer> toDelete = new ArrayList<>();
    private final Client client;

    private static TextureSampler atlasSampler()
    {
        Atlas atlas = FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH);
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
        for (VkBuffer buffer : toDelete)
        {
            buffer.cleanup();
        }
        toDelete.clear();

        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> particle = world.particleEngine().getEntitiesFor(FAMILY);
        if (particle.size() == 0)
            return;

        long now = System.currentTimeMillis();

        for (Entity entity : particle)
        {
            List<Struct> structList = new ArrayList<>();

            VkBuffer buffer = buffers.computeIfAbsent(entity, _ ->
            {
                VkBuffer buf = new VkBuffer(frameInfo
                    .commandBuffer()
                    .getDevice(), vertex().sizeof(), VERTEX_COUNT, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
                buf.map();
                return buf;
            });

            FlipbookModel flipbook = ParticleComponents.FLIPBOOK_MODEL.get(entity);
            flipbook.startAnimAfterReset(now);
            flipbook.tick(now);
            OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

            Matrix4f transform = new Matrix4f();

            ParticleRenderCommon.doTransform(entity, env, transform, frameInfo.camera());

            for (Vector3f vertex : VERTICIES)
            {
                // TODO: no, 2 verticies are duplicated, there is no need to mult it twice, the result will be the same
                // TODO: this buffer should hold only positions, another buffer is needed for all the data 'cause there's a lot
                structList.add(OrbiterVertex.POS3F.create(new Vector3f(vertex).mulPosition(transform)));
            }

            buffer.writeToBuffer(vertex()::memcpy, structList);

            Struct animData = flipbook.toStruct(now);
            OrbiterPush.FLIPBOOK_ANIM_DATA.push(animData, frameInfo.commandBuffer(), pipeline().pipelineLayout(), VK_SHADER_STAGE_FRAGMENT_BIT, 0);

            LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
            LongBuffer offsets = stack.longs(0);
            vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
            vkCmdDraw(frameInfo.commandBuffer(), VERTEX_COUNT, 1, 0, 0);
        }

        // TODO: add buffers to toDelete here, they can not be deleted before they are rendered ofc
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}

    @Override
    public void cleanup()
    {
        super.cleanup();
        buffers.values().forEach(VkBuffer::cleanup);
    }
}
