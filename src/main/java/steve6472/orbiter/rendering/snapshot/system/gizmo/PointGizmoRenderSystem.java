package steve6472.orbiter.rendering.snapshot.system.gizmo;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.orbiter.Client;
import steve6472.orbiter.rendering.OrbiterVertex;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;
import steve6472.orbiter.world.World;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class PointGizmoRenderSystem extends CommonRenderSystem
{
    private final Client client;
    private final GizmoMaterial material;

    public PointGizmoRenderSystem(MasterRenderer masterRenderer, GizmoMaterial material, Client client)
    {
        super(masterRenderer, material.pointPipeline(),
            CommonBuilder
                .create()
                .vertexBuffer(
                    OrbiterVertex.POS3F_COL4F_FLOAT.sizeof(),
                    DrawableGizmoPrimitives.POINT_COUNT * DrawableGizmoPrimitives.POINT_VERTEX_COUNT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
        this.client = client;
    }

    @Override
    protected boolean shouldRender()
    {
        var points = Select.select(material, p -> p.points, p -> p.blendPoints);
        //noinspection RedundantIfStatement
        if (points == null || points.isEmpty())
            return false;

        return true;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        var points = Select.select(material, p -> p.points, p -> p.blendPoints);
        if (points == null || points.isEmpty())
            return;

        if (points.size() > DrawableGizmoPrimitives.POINT_COUNT)
            throw new RuntimeException("Too many points (" + points.size() + " > " + DrawableGizmoPrimitives.POINT_COUNT + ")");

        VkBuffer buffer = flightFrame.getBuffer(0);
        int size = points.size() * vertex().sizeof() * DrawableGizmoPrimitives.POINT_VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        long now = System.currentTimeMillis();

        for (DrawableGizmoPrimitives.Point point : points)
        {
            addPoint(byteBuffer, point, now);
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), points.size(), 1, 0, 0);
    }

    private boolean addPoint(ByteBuffer buffer, DrawableGizmoPrimitives.Point point, long now)
    {
        float alphaMultiplier = point.alpha().get(now);
        //TODO: finish this
//        if (alphaMultiplier == 0)
//            return false;

        buffer.putFloat(point.pos().x);
        buffer.putFloat(point.pos().y);
        buffer.putFloat(point.pos().z);
        buffer.putFloat(point.r());
        buffer.putFloat(point.g());
        buffer.putFloat(point.b());
        buffer.putFloat(point.a() * alphaMultiplier);
        buffer.putFloat(point.size());

        return true;
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
