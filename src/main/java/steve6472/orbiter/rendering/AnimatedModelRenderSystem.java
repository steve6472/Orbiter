package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.FlareConstants;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Push;
import steve6472.flare.struct.def.SBO;
import steve6472.orbiter.Client;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.OrlangEnvironment;

import java.util.ArrayList;
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
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        VkBuffer buffer = flightFrame.getBuffer(0);

        List<Entity> entities = gatherEntities();
        int offset = 0;
        for (Entity entity : entities)
        {
            AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(entity);
            Position position = Components.POSITION.get(entity);
            Rotation rotation = Components.ROTATION.get(entity);
            OrlangEnv orlangEnv = Components.ENVIRONMENT.get(entity);
            OrlangEnvironment env = orlangEnv == null ? null : orlangEnv.env;

            Model model = animatedModel.model;
            AnimationController animationController = animatedModel.animationController;

            Matrix4f mat = new Matrix4f();
            if (position != null)
            {
                mat.translate(position.x(), position.y(), position.z());
            }
            if (rotation != null)
            {
                mat.rotate(rotation.toQuat());
            }

            animationController.tick(mat, env);

            Matrix4f[] array = animationController.getTransformations();
            var sbo = SBO.BONES.create((Object) array);

            buffer.writeToBuffer(SBO.BONES::memcpy, List.of(sbo), array.length * 64L, offset);
            buffer.flush(array.length * 64L, offset);

            Struct struct = Push.SKIN.create(array.length, offset / 64);
            Push.SKIN.push(struct, frameInfo.commandBuffer(), pipeline().pipelineLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0);

            model.bind(frameInfo.commandBuffer());
            model.draw(frameInfo.commandBuffer());

            offset += array.length * 64;
        }
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}

    private static final Family MODEL_FAMILY = Family.all(AnimatedModel.class, UUIDComp.class).get();
    private List<Entity> gatherEntities()
    {
        World world = client.getWorld();
        if (world == null)
            return List.of();

        ImmutableArray<Entity> physicsModels = world.ecsEngine().getEntitiesFor(MODEL_FAMILY);
        if (physicsModels.size() == 0)
            return List.of();

        List<Entity> list = new ArrayList<>(physicsModels.size());
        for (Entity entity : physicsModels)
        {
            // Disable rendering of client model
            UUIDComp uuidComp = Components.UUID.get(entity);
            if (uuidComp != null && uuidComp.uuid().equals(client.getClientUUID()))
                continue;

            list.add(entity);
        }

        if (list.isEmpty())
            return List.of();

        return list;
    }
}
