package steve6472.orbiter.util;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.render.debug.DebugRender;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 9/3/2025
 * Project: Orbiter <br>
 */
public class PhysicsRayTrace
{
    private final List<PhysicsRayTestResult> rayTraceList = new ArrayList<>();
    private final Client client;

    public PhysicsRayTrace(Client client)
    {
        this.client = client;
    }

    public void onRender(float frameTime)
    {
        Player player = client.player();
        Camera camera = OrbiterApp.getInstance().camera();
        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());

        float distance = 16;

        Optional<PhysicsRayTestResult> physicsRayTestResult = rayTraceGetFirst(new Vector3f(player.getEyePos()), direction, distance, true);
        physicsRayTestResult.ifPresent(res ->
        {
            PhysicsCollisionObject hitObj = res.getCollisionObject();
            Vector3f hitObjPos = Convert.physGetToJoml(hitObj::getPhysicsLocation);
            Quaternionf hitObjRot = Convert.physGetToJomlQuat(hitObj::getPhysicsRotation);

            DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(), 0.2f, DebugRender.DARK_ORANGE), new Matrix4f().translate(hitObjPos).rotate(hitObjRot));

            Vector3f hitPosition = new Vector3f(player.getEyePos()).add(new Vector3f(direction).mul(res.getHitFraction() * distance));

            DebugRender.addDebugObjectForFrame(
                DebugRender.lineSphere(0.02f, 3, DebugRender.IVORY),
                new Matrix4f().translate(hitPosition));
        });
    }

    /// @param position Starting position
    /// @param direction Normalized vector
    /// @param distance Max distance
    /// @param rayTestResults Where to store the results
    public void rayTrace(Vector3f position, Vector3f direction, float distance, List<PhysicsRayTestResult> rayTestResults)
    {
        client.getWorld().physics().rayTest(
            Convert.jomlToPhys(position),
            Convert.jomlToPhys(position).addLocal(direction.x * distance, direction.y * distance, direction.z * distance),
            rayTestResults);
    }

    public Optional<PhysicsRayTestResult> rayTraceGetFirst(Vector3f position, Vector3f direction, float distance, boolean excludeClientPlayer)
    {
        rayTraceList.clear();
        rayTrace(position, direction, distance, rayTraceList);
        if (rayTraceList.isEmpty())
            return Optional.empty();
        if (excludeClientPlayer)
        {
            for (PhysicsRayTestResult physicsRayTestResult : rayTraceList)
            {
                PhysicsCollisionObject collisionObject = physicsRayTestResult.getCollisionObject();
                if (collisionObject.userIndex() == Constants.CLIENT_PLAYER_MAGIC_CONSTANT)
                {
                    continue;
                }
                return Optional.of(physicsRayTestResult);
            }
            return Optional.empty();
        }
        return Optional.of(rayTraceList.getFirst());
    }
}
