package steve6472.orbiter.ui;

import org.joml.RoundingMode;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.render.impl.UIRenderImpl;
import steve6472.flare.ui.textures.SpriteEntry;
import steve6472.moondust.MoonDust;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.hex.*;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.scheduler.Scheduler;
import steve6472.orbiter.settings.Keybinds;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/22/2025
 * Project: Orbiter <br>
 */
public class HexCast extends UIRenderImpl
{
    private static final Logger LOGGER = Log.getLogger(HexCast.class);
    private final OrbiterApp orbiter;

    private static final Key LINE = Constants.key("hex/line");
    private static final Key LINE_LAST = Constants.key("hex/line_last");
    private static final Key LINE_OLD = Constants.key("hex/line_old");
    private static final Key DOT = Constants.key("hex/dot");
    private static final Key DOT_NEAR = Constants.key("hex/dot_near");
    private static final Key WHITE = Constants.key("hex/white");
    private static final int POINT_SIZE = 3;
    private static final int SPREAD = 32;

    public static boolean isHexOpen = false;

    private static final HexPatternDrawer drawer = new HexPatternDrawer();

    public HexCast(OrbiterApp orbiter)
    {
        this.orbiter = orbiter;
    }

    public static void openHex()
    {
        isHexOpen = true;
        drawer.currentPattern.clear();
        drawer.oldPatterns.clear();
    }

    @Override
    public void render()
    {
        if (!isHexOpen)
            return;

        float pixelScale = MoonDust.getInstance().getPixelScale();

        HexGrid grid = new HexGrid(new Layout(Orientation.POINTY, new Vector2f(32, 32), new Vector2f(46, 48)));
        sprite((int) (grid.layout().origin().x / pixelScale) - 2, (int) (grid.layout().origin().y / pixelScale) - 2, 0, 5, 5, DOT_NEAR);

        int top = 0;
        int bottom = 11;
        int left = 0;
        int right = 18;

        grid.iterateRectangle(top, bottom, left, right, coords -> drawHex(grid.corners(coords)));

        Vector2i cursor = orbiter.input().getMousePositionRelativeToTopLeftOfTheWindow();
        Hex hexUnderCursor = grid.pixelToHex(new Vector2f(cursor));

        if (Keybinds.HOLD_OBJECT.isActive())
        {
            drawer.addHexCoords(hexUnderCursor);
        } else
        {
            String codeFromPoints = drawer.finishPattern();
            if (!codeFromPoints.isBlank())
            {
                Scheduler.runTaskLater(() -> ((PCPlayer) OrbiterApp.getInstance().getClient().player()).castHex(codeFromPoints));
            }
        }

        Vector2i underMouse = grid.hexToPixel(hexUnderCursor).get(RoundingMode.FLOOR, new Vector2i());
        if (!drawer.currentPattern.isEmpty())
        {
            drawPattern(drawer.currentPattern, grid, LINE);
            drawLastLine(grid);
        }

        for (List<Hex> oldPattern : drawer.oldPatterns)
        {
            drawPattern(oldPattern, grid, LINE_OLD);
        }
    }

    private void drawLastLine(HexGrid grid)
    {
        if (drawer.currentPattern.size() > 1)
        {
            Hex secondToLastHex = drawer.currentPattern.get(drawer.currentPattern.size() - 2);
            Hex lastHex = drawer.currentPattern.getLast();
            Vector2i secondToLastHexPox = grid.hexToPixel(secondToLastHex).get(RoundingMode.FLOOR, new Vector2i());
            Vector2i lastHexPos = grid.hexToPixel(lastHex).get(RoundingMode.FLOOR, new Vector2i());
            createLine(secondToLastHexPox.x, secondToLastHexPox.y, lastHexPos.x, lastHexPos.y, 0, 8, 5, getTextureEntry(LINE_LAST));
        }

        if (!drawer.currentPattern.isEmpty())
        {
            Vector2i cursor = orbiter.input().getMousePositionRelativeToTopLeftOfTheWindow();

            Hex lastHex = drawer.currentPattern.getLast();
            Vector2i lastHexPos = grid.hexToPixel(lastHex).get(RoundingMode.FLOOR, new Vector2i());
            createLine(lastHexPos.x, lastHexPos.y, cursor.x, cursor.y, 0, 8, 5, getTextureEntry(LINE_LAST));
        }
    }

    private void drawPattern(List<Hex> pattern, HexGrid grid, Key textureKey)
    {
        for (int i = 0; i < pattern.size() - 1; i++)
        {
            Hex last = pattern.get(i);
            Hex next = pattern.get(i + 1);
            Vector2i lastPos = grid.hexToPixel(last).get(RoundingMode.FLOOR, new Vector2i());
            Vector2i nextPos = grid.hexToPixel(next).get(RoundingMode.FLOOR, new Vector2i());
            createLine(lastPos.x, lastPos.y, nextPos.x, nextPos.y, 0, 8, 5, getTextureEntry(textureKey));
        }
    }

    private void drawHex(List<Vector2f> corners)
    {
        for (int j = 0; j < corners.size(); j++)
        {
            Vector2f first = corners.get(j);
            Vector2f second = corners.get(j == corners.size() - 1 ? 0 : j + 1);
            createLine((int) first.x, (int) first.y, (int) second.x, (int) second.y, 0, 3, 3, getTextureEntry(LINE));
        }
    }

    protected final void sprite(int x, int y, float zIndex, int width, int height, Key textureKey)
    {
        float pixelScale = MoonDust.getInstance().getPixelScale();
        createSprite((int) (x * pixelScale), (int) (y * pixelScale), zIndex, (int) (width * pixelScale), (int) (height * pixelScale), width, height, NO_TINT, getTextureEntry(textureKey));
    }

    protected final void createLine(
        int x1, int y1,     // center of start point
        int x2, int y2,     // center of end point
        float zIndex,
        float lineWidth,
        int pixelScale,
        SpriteEntry texture)
    {
        int index = (texture == null) ? 0 : texture.index();

        // Fit zIndex to 0 - 0.1 range (same logic as sprite)
        zIndex /= 256f;
        zIndex /= 10f;

        // Direction vector
        float dx = x2 - x1;
        float dy = y2 - y1;

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0f) return;

        // Normalize direction
        float nx = dx / len;
        float ny = dy / len;

        // Perpendicular vector (for width)
        float px = -ny;
        float py = nx;

        float hw = lineWidth * 0.5f;

        // Offset for thickness
        // Expand start and end along direction for proper caps
        float sx = x1 - nx * hw;
        float sy = y1 - ny * hw;
        float ex = x2 + nx * hw;
        float ey = y2 + ny * hw;

        // Side offsets
        float ox = px * hw;
        float oy = py * hw;

        // Final vertices
        Vector3f v1 = new Vector3f(sx - ox, sy - oy, zIndex); // start left
        Vector3f v2 = new Vector3f(sx + ox, sy + oy, zIndex); // start right
        Vector3f v3 = new Vector3f(ex + ox, ey + oy, zIndex); // end right
        Vector3f v4 = new Vector3f(ex - ox, ey - oy, zIndex); // end left

        // Texture / aux data
        Vector3f vertexData = new Vector3f(index, len / (float) pixelScale, pixelScale);

        // Two triangles
        vertex(v1, NO_TINT, vertexData);
        vertex(v2, NO_TINT, vertexData);
        vertex(v3, NO_TINT, vertexData);

        vertex(v3, NO_TINT, vertexData);
        vertex(v4, NO_TINT, vertexData);
        vertex(v1, NO_TINT, vertexData);
    }
}
