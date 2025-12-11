package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.stephengold.joltjni.Body;
import com.github.stephengold.joltjni.CompoundShape;
import com.github.stephengold.joltjni.RayCastResult;
import com.github.stephengold.joltjni.readonly.ConstShape;
import org.joml.*;
import steve6472.core.util.MathUtil;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.hex.*;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.BlueprintReference;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;

import java.lang.Math;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 12/11/2025
 * Project: Orbiter <br>
 */
public class HoldInteract extends EntitySystem
{
    private UUID lastEntity;
    private HexGrid grid;
    private HexPatternDrawer drawer;

    @Override
    public void update(float deltaTime)
    {
        final float size = 9;

        if (!Keybinds.INTERACT_OBJECT_REPEAT.isActive())
        {
            if (drawer != null)
            {
                String s = drawer.finishPattern();
                if (!s.isBlank())
                {
                    ((PCPlayer) OrbiterApp.getInstance().getClient().player()).castHex(s);
                    System.out.println(s);
                }
            }
            return;
        }

        Client client = OrbiterApp.getInstance().getClient();

        RayCastResult lookAtObject = client.getRayTrace().getLookAtObject();
        if (lookAtObject == null)
            return;

        UUID uuid = client.getWorld().bodyMap().getUUIDById(lookAtObject.getBodyId());
        if (uuid == null)
            return;

        Body body = client.getWorld().bodyMap().getBodyById(lookAtObject.getBodyId());
        if (body == null)
            return;

        Entity entity = ClickECS.findEntity(client, uuid);
        if (entity == null)
            return;

        BlueprintReference blueprintReference = Components.BLUEPRINT_REFERENCE.get(entity);
        if (blueprintReference == null || !blueprintReference.key().equals(Constants.key("hex/slate"))) return;

        Position position = Components.POSITION.get(entity);
        if (position == null) return;

        Rotation rotation = Components.ROTATION.get(entity);
        if (rotation == null) return;
        Quaternionf quat = rotation.toQuat();

        if (!uuid.equals(lastEntity))
        {
            grid = new HexGrid(new Layout(Orientation.POINTY, new Vector2f(size / 16f / 16f, size / 16f / 16f), new Vector2f(-4f / 16f, -4f / 16f)));
            drawer = new HexPatternDrawer();
            lastEntity = uuid;
        }

        if (grid == null)
            return;

        String id = "";
        ConstShape shape = body.getShape();
        if (shape instanceof CompoundShape)
        {
            Collision collision = Components.COLLISION.get(entity);
            OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

            id = orbiterCollisionShape.ids()[client.getRayTrace().getLookAtSubshapeOrdinal()];

            shape = orbiterCollisionShape.collisionShape().getLeafShape(client.getRayTrace().getLookAtObject().getSubShapeId2(), new int[1]);
        }

        Vector3f point = new Vector3f(0, 0.52f / 16f, 0);
        point.add(position.toVec3f());
        Vector3f normal = new Vector3f(0, 1, 0);
        quat.transform(normal);

        Vector3f direction = MathUtil.yawPitchToVector(client.getCamera().yaw() + (float) (Math.PI * 0.5f), client.getCamera().pitch());
        float v = Intersectionf.intersectRayPlane(client.getCamera().viewPosition, direction, point, normal, 0.0001f);

        Vector3f hitPos = new Vector3f(direction).mul(v).add(client.getCamera().viewPosition);
//        Gizmos.filledLineCuboid(hitPos, 0.05f, 0x4000ff00, 2f);

        Vector3f localHitPos = new Vector3f(hitPos);
        localHitPos.sub(position.toVec3f());
        new Quaternionf(quat).invert().transform(localHitPos);

        localHitPos.add(new Vector3f(4f / 16f, 0, 4f / 16f));
        localHitPos.max(new Vector3f(0, 0, 0));
        localHitPos.min(new Vector3f(8f / 16f, 0, 8f / 16f));
        Gizmos.filledLineCuboid(localHitPos, 0.02f, 0x40ff0000, 1f);

        Vector3f offset = quat.transform(new Vector3f(0, 0.52f / 16f, 0)).add(position.toVec3f());
        grid.iterateRectangle(1, 8, 1, 7, hex -> {
            drawHex(grid.corners(hex), quat, offset, 0x90257470);
        });

        Hex hex = grid.pixelToHex(new Vector2f(localHitPos.x - 4f / 16f, localHitPos.z - 4f / 16f));
        drawHex(grid.corners(hex), quat, offset, 0xc0747025);
        drawer.addHexCoords(hex);

        Vector2f underMouse = grid.hexToPixel(hex);

        if (!drawer.currentPattern.isEmpty())
        {
            if (drawer.currentPattern.size() == 1)
            {
                Hex lastHex = drawer.currentPattern.getLast();
                Vector2f lastHexScreen = grid.hexToPixel(lastHex);
                lineOnGrid(lastHexScreen, underMouse, quat, offset, 0xffffffff);
            } else
            {
                for (int i = 0; i < drawer.currentPattern.size() - 2; i++)
                {
                    Hex last = drawer.currentPattern.get(i);
                    Hex next = drawer.currentPattern.get(i + 1);
                    Vector2f lastPos = grid.hexToPixel(last);
                    Vector2f nextPos = grid.hexToPixel(next);
                    lineOnGrid(lastPos, nextPos, quat, offset, 0xffffffff);
                }

                Hex lastHex = drawer.currentPattern.get(drawer.currentPattern.size() - 2);
                Vector2f lastHexPos = grid.hexToPixel(lastHex);
                lineOnGrid(lastHexPos, underMouse, quat, offset, 0xff00ffff);
            }
        }
    }

    private void drawHex(List<Vector2f> corners, Quaternionf rotation, Vector3f offset, int color)
    {
        for (int j = 0; j < corners.size(); j++)
        {
            Vector2f first = corners.get(j);
            Vector2f second = corners.get(j == corners.size() - 1 ? 0 : j + 1);
            lineOnGrid(first, second, rotation, offset, color);
        }
    }

    private void lineOnGrid(Vector2f from, Vector2f to, Quaternionf quat, Vector3f offset, int color)
    {
        Gizmos.line(quat.transform(new Vector3f(from.x, 0, from.y)).add(offset), quat.transform(new Vector3f(to.x, 0, to.y)).add(offset), color);
    }
}
