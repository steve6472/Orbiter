package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.Camera;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.ErrorModel;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Push;
import steve6472.orbiter.Client;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.RenderPipeline;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 8/31/2024
 * Project: Flare <br>
 */
public final class ModelUnshadedTintedRenderSystem extends CommonRenderSystem
{
    private final SBOTintedTransfromArray<Model> transfromArray = new SBOTintedTransfromArray<>(ErrorModel.VK_STATIC_INSTANCE);
    private final Client client;
    private final RenderPipeline.Enum renderPipeline;

    public ModelUnshadedTintedRenderSystem(MasterRenderer masterRenderer, Client client, boolean additive)
    {
        super(masterRenderer,
            additive ? OrbiterPipelines.MODEL_UNSHADED_TINTED_ADDITIVE : OrbiterPipelines.MODEL_UNSHADED_TINTED,
            CommonBuilder
                .create()
                .entryImage(FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH).getSampler())
                .entrySBO(OrbiterSBO.MODEL_TINT_ENTRIES.sizeof(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT, VK_SHADER_STAGE_VERTEX_BIT)
        );

        this.client = client;
        renderPipeline = additive ? RenderPipeline.Enum.MODEL_UNSHADED_TINTED_ADDITIVE : RenderPipeline.Enum.MODEL_UNSHADED_TINTED;
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

        var sbo = OrbiterSBO.MODEL_TINT_ENTRIES.create(transfromArray.getTransformsArray());
        buffer.writeToBuffer(OrbiterSBO.MODEL_TINT_ENTRIES::memcpy, sbo);
    }

    private static final Family PARTICLE_FAMILY = Family.all(ParticleModel.class, Position.class, OrlangEnvironment.class).get();

    public void updateTransformArray(SBOTintedTransfromArray<Model> sboTransfromArray, FrameInfo frameInfo)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        ImmutableArray<Entity> particle = world.particleEngine().getEntitiesFor(PARTICLE_FAMILY);
        if (particle.size() == 0)
            return;

        List<Entity> list = new ArrayList<>(particle.size());
        for (Entity entity : particle)
        {
            RenderPipeline renderPipeline = ParticleComponents.RENDER_PIPELINE.get(entity);
            if ((renderPipeline != null && renderPipeline.value == this.renderPipeline))
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
            Pair<Model, SBOTintedTransfromArray<Model>.Area> modelAreaPair = processEntity(entity, lastModel, lastArea, sboTransfromArray, frameInfo.camera());
            lastModel = modelAreaPair.getFirst();
            lastArea = modelAreaPair.getSecond();
        }
    }

    private Pair<Model, SBOTintedTransfromArray<Model>.Area> processEntity(Entity entity, Model lastModel, SBOTintedTransfromArray<Model>.Area lastArea, SBOTintedTransfromArray<Model> sboTransfromArray, Camera camera)
    {
        ParticleModel model = ParticleComponents.MODEL.get(entity);
        OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

        if (lastArea == null || lastModel != model.model)
        {
            lastArea = sboTransfromArray.getAreaByType(model.model);
            lastModel = model.model;
        }

        Matrix4f primitiveTransform = lastArea.getTransform();
        ParticleRenderCommon.updateTransformMat(primitiveTransform, entity, camera, env);

        var tintrgba = ParticleComponents.TINT_RGBA.get(entity);
        if (tintrgba != null)
        {
            lastArea.getTint().set(
                tintrgba.r.evaluateAndGet(env),
                tintrgba.g.evaluateAndGet(env),
                tintrgba.b.evaluateAndGet(env),
                tintrgba.a.evaluateAndGet(env)
            );
        } else
        {
            var tintGradient = ParticleComponents.TINT_GRADIENT.get(entity);
            if (tintGradient != null)
            {
                tintGradient.apply(env, lastArea.getTint());
            }
        }

        lastArea.update();

        return Pair.of(lastModel, lastArea);
    }
}
