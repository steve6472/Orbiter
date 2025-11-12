package steve6472.orbiter.rendering.snapshot.system.gizmo;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class TriGizmoRenderSystem extends CommonRenderSystem
{
    private final GizmoMaterial material;

    public TriGizmoRenderSystem(MasterRenderer masterRenderer, GizmoMaterial material)
    {
        super(masterRenderer, material.triPipeline(),
            CommonBuilder
                .create()
                .vertexBuffer(
                    Vertex.POS3F_COL4F.sizeof(),
                    DrawableGizmoPrimitives.TRI_COUNT * DrawableGizmoPrimitives.TRI_VERTEX_COUNT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
    }

    @Override
    protected boolean shouldRender()
    {
        var tris = Select.select(material, p -> p.tris, p -> p.blendTris);
        //noinspection RedundantIfStatement
        if (tris == null || tris.isEmpty())
            return false;

        return true;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        var tris = Select.select(material, p -> p.tris, p -> p.blendTris);
        if (tris == null || tris.isEmpty())
            return;

        if (tris.size() > DrawableGizmoPrimitives.TRI_COUNT)
            throw new RuntimeException("Too many tris");

        VkBuffer buffer = flightFrame.getBuffer(0);
        int size = tris.size() * vertex().sizeof() * DrawableGizmoPrimitives.TRI_VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        long now = System.currentTimeMillis();

        for (DrawableGizmoPrimitives.Tri point : tris)
        {
            addTri(byteBuffer, point, now);
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);
        vkCmdDraw(frameInfo.commandBuffer(), tris.size() * DrawableGizmoPrimitives.TRI_VERTEX_COUNT, 1, 0, 0);
    }

    private boolean addTri(ByteBuffer buffer, DrawableGizmoPrimitives.Tri tri, long now)
    {
        float alphaMultiplier = tri.alpha().get(now);
        //TODO: finish this
//        if (alphaMultiplier == 0)
//            return false;

        buffer.putFloat(tri.posA().x);
        buffer.putFloat(tri.posA().y);
        buffer.putFloat(tri.posA().z);
        buffer.putFloat(tri.r());
        buffer.putFloat(tri.g());
        buffer.putFloat(tri.b());
        buffer.putFloat(tri.a() * alphaMultiplier);

        buffer.putFloat(tri.posB().x);
        buffer.putFloat(tri.posB().y);
        buffer.putFloat(tri.posB().z);
        buffer.putFloat(tri.r());
        buffer.putFloat(tri.g());
        buffer.putFloat(tri.b());
        buffer.putFloat(tri.a() * alphaMultiplier);

        buffer.putFloat(tri.posC().x);
        buffer.putFloat(tri.posC().y);
        buffer.putFloat(tri.posC().z);
        buffer.putFloat(tri.r());
        buffer.putFloat(tri.g());
        buffer.putFloat(tri.b());
        buffer.putFloat(tri.a() * alphaMultiplier);

        return true;
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
