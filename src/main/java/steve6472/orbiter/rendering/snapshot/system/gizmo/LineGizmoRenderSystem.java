package steve6472.orbiter.rendering.snapshot.system.gizmo;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class LineGizmoRenderSystem extends CommonRenderSystem
{
    private final GizmoMaterial material;

    public LineGizmoRenderSystem(MasterRenderer masterRenderer, GizmoMaterial material)
    {
        super(masterRenderer, material.linePipeline(),
            CommonBuilder
                .create()
                .vertexBuffer(
                    Vertex.POS3F_COL4F.sizeof(),
                    DrawableGizmoPrimitives.LINE_COUNT * DrawableGizmoPrimitives.LINE_VERTEX_COUNT,
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT)
        );
        this.material = material;
    }

    @Override
    protected boolean shouldRender()
    {
        var linesByWidth = Select.select(material, p -> p.lines, p -> p.blendLines);
        //noinspection RedundantIfStatement
        if (linesByWidth == null || linesByWidth.isEmpty())
            return false;

        return true;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        var linesByWidth = Select.select(material, p -> p.lines, p -> p.blendLines);
        if (linesByWidth == null || linesByWidth.isEmpty())
            return;

        int totalMaxLinesCount = 0;

        for (float width : linesByWidth.keySet())
        {
            List<DrawableGizmoPrimitives.Line> lines = linesByWidth.get(width);
            totalMaxLinesCount += lines.size();
        }

        if (totalMaxLinesCount > DrawableGizmoPrimitives.LINE_COUNT)
            throw new RuntimeException("Too many lines");

        VkBuffer buffer = flightFrame.getBuffer(0);
        int size = totalMaxLinesCount * vertex().sizeof() * DrawableGizmoPrimitives.LINE_VERTEX_COUNT;
        ByteBuffer byteBuffer = buffer
            .getMappedMemory()
            .getByteBuffer(0, size);

        long now = System.currentTimeMillis();
        for (float width : linesByWidth.keySet())
        {
            List<DrawableGizmoPrimitives.Line> lines = linesByWidth.get(width);
            for (DrawableGizmoPrimitives.Line line : lines)
            {
                addLine(byteBuffer, line, now);
            }
        }

        LongBuffer vertexBuffers = stack.longs(buffer.getBuffer());
        LongBuffer offsets = stack.longs(0);

        vkCmdBindVertexBuffers(frameInfo.commandBuffer(), 0, vertexBuffers, offsets);

        int vertexOffset = 0;
        for (var entry : linesByWidth.float2ObjectEntrySet())
        {
            float width = entry.getFloatKey();
            List<DrawableGizmoPrimitives.Line> group = entry.getValue();
            int vertexCount = group.size() * DrawableGizmoPrimitives.LINE_VERTEX_COUNT;

            vkCmdSetLineWidth(frameInfo.commandBuffer(), width);
            vkCmdDraw(frameInfo.commandBuffer(), vertexCount, 1, vertexOffset, 0);

            vertexOffset += vertexCount;
        }
    }

    private boolean addLine(ByteBuffer buffer, DrawableGizmoPrimitives.Line line, long now)
    {
        float alphaMultiplier = line.alpha().get(now);
        //TODO: finish this
//        if (alphaMultiplier == 0)
//            return false;

        buffer.putFloat(line.start().x);
        buffer.putFloat(line.start().y);
        buffer.putFloat(line.start().z);
        buffer.putFloat(line.r());
        buffer.putFloat(line.g());
        buffer.putFloat(line.b());
        buffer.putFloat(line.a() * alphaMultiplier);

        buffer.putFloat(line.end().x);
        buffer.putFloat(line.end().y);
        buffer.putFloat(line.end().z);
        buffer.putFloat(line.r());
        buffer.putFloat(line.g());
        buffer.putFloat(line.b());
        buffer.putFloat(line.a() * alphaMultiplier);

        return true;
    }

    @Override
    protected void updateData(FlightFrame flightFrame, FrameInfo frameInfo)
    {}
}
