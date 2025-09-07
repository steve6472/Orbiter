package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.ErrorModel;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.StructDef;
import steve6472.flare.struct.def.Push;
import steve6472.orbiter.Client;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.rendering.particle.Tint;
import steve6472.orbiter.rendering.particle.Transform;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.components.RenderPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;

/**
 * Created by steve6472
 * Date: 8/31/2024
 * Project: Flare <br>
 */
public class CommonParticleRenderSystem<E extends SBOModelArray.Entry> extends CommonRenderSystem
{
    private final SBOModelArray<Model, E> transfromArray;
    private final Client client;
    private final Family family;
    private final RenderPipeline.Enum particlePipeline;
    private final StructDef struct;

    public CommonParticleRenderSystem(
        MasterRenderer masterRenderer,
        PipelineConstructor pipeline,
        Client client,
        Family family,
        StructDef struct,
        RenderPipeline.Enum particlePipeline,
        Supplier<E> constructor,
        IntFunction<Object[]> entryArray)
    {
        super(masterRenderer,
            pipeline,
            CommonBuilder
                .create()
                .entryImage(FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH).getSampler())
                .entrySBO(struct.sizeof(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK_SHADER_STAGE_VERTEX_BIT)
        );
        this.client = client;
        this.family = family;
        this.particlePipeline = particlePipeline;
        this.struct = struct;

        transfromArray = new SBOModelArray<>(ErrorModel.VK_STATIC_INSTANCE, constructor, entryArray);
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        int totalIndex = 0;
        for (var area : transfromArray.getAreas())
        {
            if (area.toRender() == 0)
                continue;

            Struct offset = Push.STATIC_TRANSFORM_OFFSET.create(totalIndex);
            Push.STATIC_TRANSFORM_OFFSET.push(offset, frameInfo.commandBuffer(), pipeline().pipelineLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0);
            area.modelType().bind(frameInfo.commandBuffer());
            area.modelType().draw(frameInfo.commandBuffer(), area.toRender());
            totalIndex += area.toRender();
        }
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {
        VkBuffer buffer = flightFrame.getBuffer(1);

        transfromArray.start();
        updateTransformArray(transfromArray, frameInfo);

        var sbo = struct.create(transfromArray.getEntriesArray());
        buffer.writeToBuffer(struct::memcpy, sbo);
    }

    public void updateTransformArray(SBOModelArray<Model, E> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> particle = world.particleEngine().getEntitiesFor(family);
        if (particle.size() == 0)
            return;

        List<Entity> list = new ArrayList<>(particle.size());
        for (Entity entity : particle)
        {
            RenderPipeline renderPipeline = ParticleComponents.RENDER_PIPELINE.get(entity);
            if ((renderPipeline != null && renderPipeline.value == this.particlePipeline) || (renderPipeline == null && this.particlePipeline == RenderPipeline.Enum.MODEL))
                list.add(entity);
        }

        if (list.isEmpty())
            return;

        sboTransfromArray.sort(list, entity -> sboTransfromArray.addArea(ParticleComponents.MODEL.get(entity).model).index());

        if (sboTransfromArray.getAreas().isEmpty())
            return;

        var lastArea = sboTransfromArray.getAreaByIndex(0);
        Model lastModel = ParticleComponents.MODEL.get(list.getFirst()).model;
        for (Entity entity : list)
        {
            ParticleModel model = ParticleComponents.MODEL.get(entity);
            OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

            if (lastArea == null || lastModel != model.model)
            {
                lastArea = sboTransfromArray.getAreaByType(model.model);
                lastModel = model.model;
            }

            E entry = lastArea.getEntry();

            if (entry instanceof Transform transform) ParticleRenderCommon.doTransform(entity, env, transform.transform(), frameInfo.camera());
            if (entry instanceof Tint tint) ParticleRenderCommon.doTint(entity, env, tint.tint());

            lastArea.moveIndex();
        }
    }
}
