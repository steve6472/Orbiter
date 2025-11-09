package steve6472.orbiter.rendering.snapshot.system.gizmo;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.MasterRenderer;
import steve6472.flare.VkBuffer;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.render.common.CommonBuilder;
import steve6472.flare.render.common.CommonRenderSystem;
import steve6472.flare.render.common.FlightFrame;
import steve6472.flare.struct.def.Vertex;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.world.World;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/22/2025
 * Project: Orbiter <br>
 */
public class TriGizmoRenderSystem extends CommonRenderSystem
{
    private final Client client;
    private final GizmoMaterial material;

    public TriGizmoRenderSystem(MasterRenderer masterRenderer, GizmoMaterial material, Client client)
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
        this.client = client;
    }

    @Override
    protected boolean shouldRender()
    {
        World world = client.getWorld();
        if (world == null)
            return false;

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null)
            return false;

        var tris = select(currentRenderState, material);
        //noinspection RedundantIfStatement
        if (tris == null || tris.isEmpty())
            return false;

        return true;
    }

    @Override
    protected void render(FlightFrame flightFrame, FrameInfo frameInfo, MemoryStack stack)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        WorldRenderState currentRenderState = OrbiterApp.getInstance().currentRenderState;
        if (currentRenderState == null)
            return;

        var tris = select(currentRenderState, material);
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

    private List<DrawableGizmoPrimitives.Tri> select(WorldRenderState renderState, GizmoMaterial material)
    {
        GizmoMaterial.Settings settings = material.settings();
        if (settings.alwaysOnTop())
        {
            DrawableGizmoPrimitives drawableGizmoPrimitivesAlwaysOnTop = renderState.drawableGizmoPrimitivesAlwaysOnTop;
            if (settings.hasAlpha())
                return drawableGizmoPrimitivesAlwaysOnTop.blendTris;
            else
                return drawableGizmoPrimitivesAlwaysOnTop.tris;
        } else
        {
            DrawableGizmoPrimitives drawableGizmoPrimitives = renderState.drawableGizmoPrimitives;
            if (settings.hasAlpha())
                return drawableGizmoPrimitives.blendTris;
            else
                return drawableGizmoPrimitives.tris;
        }
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
