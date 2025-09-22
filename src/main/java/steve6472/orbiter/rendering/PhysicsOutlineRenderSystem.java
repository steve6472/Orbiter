package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.render.debug.objects.DebugCapsule;
import steve6472.flare.render.debug.objects.DebugCuboid;
import steve6472.flare.render.debug.objects.DebugSphere;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.systems.ClickECS;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkCmdDraw;
import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 9/17/2025
 * Project: Orbiter <br>
 */
public class PhysicsOutlineRenderSystem extends CommonRenderSystem
{
    private final Client client;
    private final Vector4f color;
    private final boolean isFocus;

    public PhysicsOutlineRenderSystem(MasterRenderer masterRenderer, boolean isFocus, Client client)
    {
        super(masterRenderer, isFocus ? OrbiterPipelines.PHYSICS_OUTLINE_FOCUS : OrbiterPipelines.PHYSICS_OUTLINE, CommonBuilder.create()
            .vertexBuffer(Vertex.POS3F_COL4F.sizeof(), 262144, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
        this.isFocus = isFocus;
        this.color = isFocus ? WHITE : BLACK;
        this.client = client;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        if (client.getWorld() == null)
            return;

        PhysicsCollisionObject lookAtObject = client.getRayTrace().getLookAtObject();
        if (lookAtObject == null)
            return;

        List<Struct> verticies = new ArrayList<>();

        Object userObject = lookAtObject.getUserObject();

        // Render only in world objects with assigned entities
        if (!(userObject instanceof UUID uuid))
            return;

        Entity entity = ClickECS.findEntity(client, uuid);
        if (entity == null)
            return;

        Collision collision = Components.COLLISION.get(entity);
        OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

        renderOutline(lookAtObject, orbiterCollisionShape.ids(), (short) client.getRayTrace().getLookAtTriangleIndex(), verticies);

        VkBuffer buffer = flightFrame.getBuffer(0);

        buffer.writeToBuffer(vertex()::memcpy, verticies);

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);
        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), verticies.size(), 1, 0, 0);
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}

    private void renderOutline(PhysicsCollisionObject lookAtObject, short[] ids, short lookatId, List<Struct> verticies)
    {
        CollisionShape shape = lookAtObject.getCollisionShape();
        Matrix4f transform = Convert.physGetTransformToJoml(lookAtObject, new Matrix4f());
        transform.scale(Convert.physGetToJoml(shape::getScale));

        renderShape(shape, ids, lookatId, (short) 0, transform, verticies);
    }

    private void renderShape(CollisionShape shape, short[] ids, short lookatId, short currentId, Matrix4f bodyTransform, List<Struct> verticies)
    {
        if (!(shape instanceof CompoundCollisionShape) && lookatId != -1)
        {
            if (isFocus && (ids[lookatId] == 0 || ids[lookatId] != ids[currentId]))
                return;

            if (!isFocus && ids[lookatId] != 0 && ids[lookatId] == ids[currentId])
                return;
        }

        if (isFocus && lookatId == -1)
            return;

        if (shape instanceof BoxCollisionShape box)
        {
            renderBox(box, bodyTransform, verticies);
        }
        else if (shape instanceof CapsuleCollisionShape shap)
        {
            renderCapsule(shap, bodyTransform, verticies);
        } else if (shape instanceof SphereCollisionShape shap)
        {
            renderSphere(shap, bodyTransform, verticies);
        } else if (shape instanceof CompoundCollisionShape shap)
        {
            ChildCollisionShape[] listChildren = shap.listChildren();
            for (int i = 0; i < listChildren.length; i++)
            {
                ChildCollisionShape childCollisionShape = listChildren[i];
                CollisionShape shape1 = childCollisionShape.getShape();
                var offset = Convert.jomlToPhys(new Vector3f());
                childCollisionShape.copyOffset(offset);
                renderShape(shape1, ids, lookatId, (short) i, new Matrix4f(bodyTransform).translate(offset.x, offset.y, offset.z), verticies);
            }
        }
    }

    private void renderBox(BoxCollisionShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        Vector3f halfSizes = Convert.physGetToJoml(shape::getHalfExtents);

        new DebugCuboid(new Vector3f(), halfSizes.x, halfSizes.y, halfSizes.z, color).addVerticies(verticies, bodyTransform);
    }

    private void renderCapsule(CapsuleCollisionShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        float radius = shape.getRadius();
        float height = shape.getHeight();
        int quality = 13;

        new DebugCapsule(radius, height, quality, color).addVerticies(verticies, bodyTransform);
    }

    private void renderSphere(SphereCollisionShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        float radius = shape.getRadius();
        int quality = 13;

        new DebugSphere(radius, quality, color).addVerticies(verticies, bodyTransform);
    }
}
