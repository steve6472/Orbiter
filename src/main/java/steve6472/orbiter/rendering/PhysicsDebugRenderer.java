package steve6472.orbiter.rendering;

import com.github.stephengold.joltjni.CustomDebugRendererSimple;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import steve6472.core.util.ColorUtil;
import steve6472.flare.MasterRenderer;
import steve6472.flare.ui.font.render.TextLine;

import static steve6472.flare.render.debug.DebugRender.addDebugObjectForFrame;
import static steve6472.flare.render.debug.DebugRender.line;

/**
 * Created by steve6472
 * Date: 10/7/2025
 * Project: Orbiter <br>
 * // TODO: make this into a proper renrerer (2, one lines one triangle)
 */
public class PhysicsDebugRenderer extends CustomDebugRendererSimple
{
    private final MasterRenderer renderer;

    public PhysicsDebugRenderer(MasterRenderer masterRenderer)
    {
        this.renderer = masterRenderer;
    }

    /**
     * Draw the specified 3-D line. Meant to be overridden.
     *
     * @param x1       the X coordinate of the first endpoint
     * @param y1       the Y coordinate of the first endpoint
     * @param z1       the Z coordinate of the first endpoint
     * @param x2       the X coordinate of the 2nd endpoint
     * @param y2       the Y coordinate of the 2nd endpoint
     * @param z2       the Z coordinate of the 2nd endpoint
     * @param colorInt the color of the line
     */
    public void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, int colorInt)
    {
        float[] colors = ColorUtil.getColors(colorInt);
        addDebugObjectForFrame(line(new Vector3f((float) x1, (float) y1, (float) z1), new Vector3f((float) x2, (float) y2, (float) z2), new Vector4f(colors[0], colors[1], colors[2], colors[3])));
    }

    /**
     * Draw the specified 3-D text. Meant to be overridden.
     *
     * @param xx       the X coordinate of the text
     * @param yy       the Y coordinate of the text
     * @param zz       the Z coordinate of the text
     * @param text     the text to display (not null)
     * @param colorInt the color of the text
     * @param height   the height of the text
     */
    public void drawText3d(double xx, double yy, double zz, String text, int colorInt, float height)
    {
//        System.out.println("Rendering " + text);
//        var textLine = TextLine.fromText(text + " (" + colorInt + ")", 0.1f);
//        renderer.textRender().line(textLine, new Matrix4f().translate((float) xx, (float) (yy) + height, (float) zz));
    }

    /**
     * Draw the specified 3-D triangle. Meant to be overridden.
     *
     * @param x1       the X coordinate of the first vertex
     * @param y1       the Y coordinate of the first vertex
     * @param z1       the Z coordinate of the first vertex
     * @param x2       the X coordinate of the 2nd vertex
     * @param y2       the Y coordinate of the 2nd vertex
     * @param z2       the Z coordinate of the 2nd vertex
     * @param x3       the X coordinate of the 3rd vertex
     * @param y3       the Y coordinate of the 3rd vertex
     * @param z3       the Z coordinate of the 3rd vertex
     * @param colorInt the color of the triangle
     * @param ordinal  the ECastShadow ordinal
     */
    public void drawTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, int colorInt, int ordinal)
    {
        float[] colors = ColorUtil.getColors(colorInt);
        Vector4f color = new Vector4f(colors[0], colors[1], colors[2], colors[3]);
        addDebugObjectForFrame(line(new Vector3f((float) x1, (float) y1, (float) z1), new Vector3f((float) x2, (float) y2, (float) z2), color));
        addDebugObjectForFrame(line(new Vector3f((float) x2, (float) y2, (float) z2), new Vector3f((float) x3, (float) y3, (float) z3), color));
        addDebugObjectForFrame(line(new Vector3f((float) x3, (float) y3, (float) z3), new Vector3f((float) x1, (float) y1, (float) z1), color));
    }
}
