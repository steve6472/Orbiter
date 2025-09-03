package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.*;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.ErrorModel;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.descriptors.DescriptorPool;
import steve6472.flare.descriptors.DescriptorSetLayout;
import steve6472.flare.descriptors.DescriptorWriter;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.RenderSystem;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Push;
import steve6472.flare.struct.def.UBO;
import steve6472.orbiter.Client;
import steve6472.orbiter.orlang.Orlang;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrCode;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static steve6472.flare.SwapChain.MAX_FRAMES_IN_FLIGHT;

/**
 * Created by steve6472
 * Date: 8/31/2024
 * Project: Flare <br>
 */
public final class ModelUnshadedTintedRenderSystem extends RenderSystem
{
    private final DescriptorPool globalPool;
    private final DescriptorSetLayout globalSetLayout;
    private final List<FlightFrame> frames = new ArrayList<>(MAX_FRAMES_IN_FLIGHT);
    private final SBOTintedTransfromArray<Model> transfromArray = new SBOTintedTransfromArray<>(ErrorModel.VK_STATIC_INSTANCE);
    private final Client client;
    private final RenderPipeline.Enum renderPipeline;

    public ModelUnshadedTintedRenderSystem(MasterRenderer masterRenderer, Client client, boolean additive)
    {
        super(masterRenderer, additive ? OrbiterPipelines.MODEL_UNSHADED_TINTED_ADDITIVE : OrbiterPipelines.MODEL_UNSHADED_TINTED);
        this.client = client;
        renderPipeline = additive ? RenderPipeline.Enum.MODEL_UNSHADED_TINTED_ADDITIVE : RenderPipeline.Enum.MODEL_UNSHADED_TINTED;

        globalSetLayout = DescriptorSetLayout
            .builder(device)
            .addBinding(0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC, VK_SHADER_STAGE_VERTEX_BIT)
            .addBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK_SHADER_STAGE_FRAGMENT_BIT)
            .addBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, VK_SHADER_STAGE_VERTEX_BIT)
            .build();
        globalPool = DescriptorPool
            .builder(device)
            .setMaxSets(MAX_FRAMES_IN_FLIGHT)
            .addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER_DYNAMIC, MAX_FRAMES_IN_FLIGHT)
            .addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, MAX_FRAMES_IN_FLIGHT)
            .addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, MAX_FRAMES_IN_FLIGHT)
            .build();

        for (int i = 0; i < MAX_FRAMES_IN_FLIGHT; i++)
        {
            FlightFrame frame = new FlightFrame();
            frames.add(frame);

            VkBuffer global = new VkBuffer(device, UBO.GLOBAL_CAMERA_UBO.sizeof(), 1, VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT);
            global.map();
            frame.uboBuffer = global;

            VkBuffer sbo = new VkBuffer(device, OrbiterSBO.MODEL_TINT_ENTRIES.sizeof(), 1, VK_BUFFER_USAGE_STORAGE_BUFFER_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT);
            sbo.map();
            frame.sboBuffer = sbo;

            try (MemoryStack stack = MemoryStack.stackPush())
            {
                DescriptorWriter descriptorWriter = new DescriptorWriter(globalSetLayout, globalPool);
                frame.descriptorSet = descriptorWriter
                    .writeBuffer(0, stack, frame.uboBuffer, UBO.GLOBAL_CAMERA_UBO.sizeof() / UBO.GLOBAL_CAMERA_MAX_COUNT)
                    .writeImage(1, stack, FlareRegistries.ATLAS.get(FlareConstants.ATLAS_BLOCKBENCH).getSampler())
                    .writeBuffer(2, stack, frame.sboBuffer)
                    .build();
            }
        }
    }

    @Override
    public long[] setLayouts()
    {
        return new long[]{globalSetLayout.descriptorSetLayout};
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack stack)
    {
        FlightFrame flightFrame = frames.get(frameInfo.frameIndex());

        Struct globalUBO = UBO.GLOBAL_CAMERA_UBO.create(frameInfo.camera().getProjectionMatrix(), frameInfo
            .camera()
            .getViewMatrix());
        int singleInstanceSize = UBO.GLOBAL_CAMERA_UBO.sizeof() / UBO.GLOBAL_CAMERA_MAX_COUNT;

        flightFrame.uboBuffer.writeToBuffer(UBO.GLOBAL_CAMERA_UBO::memcpy, List.of(globalUBO), singleInstanceSize, singleInstanceSize * frameInfo.camera().cameraIndex);
        flightFrame.uboBuffer.flush(singleInstanceSize, (long) singleInstanceSize * frameInfo.camera().cameraIndex);

        pipeline().bind(frameInfo.commandBuffer());

        updateSbo(flightFrame.sboBuffer, frameInfo);

        vkCmdBindDescriptorSets(frameInfo.commandBuffer(), VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline().pipelineLayout(), 0, stack.longs(flightFrame.descriptorSet), stack.ints(singleInstanceSize * frameInfo.camera().cameraIndex));

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

    private void updateSbo(VkBuffer sboBuffer, FrameInfo frameInfo)
    {
        transfromArray.start();
        updateTransformArray(transfromArray, frameInfo);

        var sbo = OrbiterSBO.MODEL_TINT_ENTRIES.create(transfromArray.getTransformsArray());
        sboBuffer.writeToBuffer(OrbiterSBO.MODEL_TINT_ENTRIES::memcpy, sbo);
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
        OrlangEnvironment env = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);

        // Update curves each frame
        env.curves.forEach((name, curve) -> curve.calculate(name, env));
        OrCode frame = env.expressions.get("frame");
        if (frame != null)
        {
            Orlang.interpreter.interpret(frame, env);
        }

        if (lastArea == null || lastModel != model.model)
        {
            lastArea = sboTransfromArray.getAreaByType(model.model);
            lastModel = model.model;
        }

        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        Matrix4f primitiveTransform = lastArea.getTransform();
        Vector3f position = new Vector3f();

        if (ParticleComponents.POSITION.has(entity))
        {
            Position particlePos = ParticleComponents.POSITION.get(entity);
            if (localSpace != null && localSpace.position)
            {
                ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
                var holderPosition = Components.POSITION.get(follower.entity);
                if (holderPosition != null)
                {
                    position.add(holderPosition.x(), holderPosition.y(), holderPosition.z());
                }
            }
            position.add(particlePos.x, particlePos.y, particlePos.z);
        }

        ParticleBillboard particleBillboard = ParticleComponents.BILLBOARD.get(entity);
        if (particleBillboard != null)
        {
            Matrix4f matrix4f = BillboardUtil.makeBillboard(position, entity, camera, particleBillboard);
            primitiveTransform.mul(matrix4f);
        } else
        {
            primitiveTransform.translate(position.x, position.y, position.z);
        }

        if (localSpace != null && localSpace.rotation)
        {
            ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
            steve6472.orbiter.world.ecs.components.physics.Rotation holderRotation = Components.ROTATION.get(follower.entity);
            if (holderRotation != null)
            {
                primitiveTransform.rotate(new Quaternionf(holderRotation.x(), holderRotation.y(), holderRotation.z(), holderRotation.w()));
            }
        }

        var rotation = ParticleComponents.ROTATION.get(entity);
        if (rotation != null)
        {
            BillboardUtil.applySpin(primitiveTransform, (float) Math.toRadians(rotation.rotation));
        }

        if (ParticleComponents.SCALE.has(entity))
        {
            Scale scale = ParticleComponents.SCALE.get(entity);
            scale.scale.evaluate(env);
            primitiveTransform.scale(scale.scale.fx(), scale.scale.fy(), scale.scale.fz());
        }

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

    @Override
    public void cleanup()
    {
        globalSetLayout.cleanup();
        globalPool.cleanup();

        for (FlightFrame flightFrame : frames)
        {
            flightFrame.uboBuffer.cleanup();
            flightFrame.sboBuffer.cleanup();
        }
    }

    final static class FlightFrame
    {
        VkBuffer uboBuffer;
        VkBuffer sboBuffer;
        long descriptorSet;
    }
}
