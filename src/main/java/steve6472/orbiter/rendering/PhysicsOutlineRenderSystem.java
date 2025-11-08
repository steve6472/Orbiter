package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.readonly.ConstSubShape;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.render.debug.objects.DebugCapsule;
import steve6472.flare.render.debug.objects.DebugCuboid;
import steve6472.flare.render.debug.objects.DebugSphere;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.Vertex;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.player.PCPlayer;
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
    public void render(FrameInfo frameInfo, MemoryStack stack)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("focus=" + isFocus);
        super.render(frameInfo, stack);
        profiler.pop();
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        if (client.getWorld() == null)
            return;

        RayCastResult lookAtObject = client.getRayTrace().getLookAtObject();
        if (lookAtObject == null)
            return;

        /*
         * Hack in the look at thing from PCPlayer
         * todo: use tick gizmo
         */
        if (isFocus)
        {
            Camera camera = frameInfo.camera();
            Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
            Vector3f hitPosition = new Vector3f(camera.viewPosition).add(new Vector3f(direction).mul(lookAtObject.getFraction() * PCPlayer.REACH));

            DebugRender.addDebugObjectForFrame(
                DebugRender.lineSphere(0.015f, 4, DebugRender.IVORY),
                new Matrix4f().translate(hitPosition));
        }

        UUID uuid = client.getWorld().bodyMap().getUUIDById(lookAtObject.getBodyId());

        // Render only in world objects with assigned entities
        if (uuid == null)
            return;

        Body body = client.getWorld().bodyMap().getBodyByUUID(uuid);
        if (body == null)
            return;

        Entity entity = ClickECS.findEntity(client, uuid);
        if (entity == null)
            return;

        List<Struct> verticies = new ArrayList<>();

        Collision collision = Components.COLLISION.get(entity);
        OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

//        System.out.println(client.getRayTrace().getSubShapeId());
        renderOutline(body, orbiterCollisionShape.ids(), (short) lookAtObject.getSubShapeId2(), verticies);

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

    private void renderOutline(Body lookAtBody, short[] ids, short lookatId, List<Struct> verticies)
    {
        Matrix4f transform = new Matrix4f();
        RVec3 pos = new RVec3();
        Quat rot = new Quat();
        lookAtBody.getPositionAndRotation(pos, rot);
        transform.translate(pos.x(), pos.y(), pos.z());
        transform.rotate(Convert.physToJoml(rot, new Quaternionf()));

        ConstShape shape = lookAtBody.getShape();
        if (shape instanceof CompoundShape compoundShape)
            lookatId = (short) ClickECS.fixSubShapeId(lookatId, compoundShape.getNumSubShapes());

        renderShape(shape, ids, lookatId, (short) 0, transform, verticies);
    }

    private void renderShape(ConstShape shape, short[] ids, short lookatId, short currentId, Matrix4f bodyTransform, List<Struct> verticies)
    {
        if (!(shape instanceof CompoundShape) && lookatId != -1)
        {
            if (isFocus && (ids[lookatId] == 0 || ids[lookatId] != ids[currentId]))
                return;

            if (!isFocus && ids[lookatId] != 0 && ids[lookatId] == ids[currentId])
                return;
        }

        if (isFocus && lookatId == -1)
            return;

        if (shape instanceof BoxShape box)
        {
            renderBox(box, bodyTransform, verticies);
        }
        else if (shape instanceof CapsuleShape shap)
        {
            renderCapsule(shap, bodyTransform, verticies);
        } else if (shape instanceof SphereShape shap)
        {
            renderSphere(shap, bodyTransform, verticies);
        } else if (shape instanceof CompoundShape shap)
        {
            ConstSubShape[] listChildren = shap.getSubShapes();
            for (int i = 0; i < listChildren.length; i++)
            {
                ConstSubShape childCollisionShape = listChildren[i];
                ConstShape shape1 = childCollisionShape.getShape();
                var offset = Convert.physToJoml(childCollisionShape.getPositionCom());
//                childCollisionShape.copyOffset(offset);
                renderShape(shape1, ids, lookatId, (short) i, new Matrix4f(bodyTransform).translate(offset.x, offset.y, offset.z), verticies);
            }
        }
    }

    private void renderBox(BoxShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        Vector3f halfSizes = Convert.physGetToJoml((_) -> shape.getHalfExtent());

        new DebugCuboid(new Vector3f(), halfSizes.x, halfSizes.y, halfSizes.z, color).addVerticies(verticies, bodyTransform);
    }

    private void renderCapsule(CapsuleShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        float radius = shape.getRadius();
        float height = shape.getHalfHeightOfCylinder();
        int quality = 13;

        new DebugCapsule(radius, height, quality, color).addVerticies(verticies, bodyTransform);
    }

    private void renderSphere(SphereShape shape, Matrix4f bodyTransform, List<Struct> verticies)
    {
        float radius = shape.getRadius();
        int quality = 13;

        new DebugSphere(radius, quality, color).addVerticies(verticies, bodyTransform);
    }
}
