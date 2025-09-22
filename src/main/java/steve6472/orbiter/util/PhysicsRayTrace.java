package steve6472.orbiter.util;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;

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
    private PhysicsCollisionObject lookAtObject;
    private int lookAtTriangleIndex;

    public PhysicsRayTrace(Client client)
    {
        this.client = client;
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

        if (!excludeClientPlayer)
            return Optional.of(rayTraceList.getFirst());

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

    public Optional<PhysicsRayTestResult> rayTraceGetFirst(Camera camera, float distance, boolean excludeClientPlayer)
    {
        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        rayTraceList.clear();
        rayTrace(camera.viewPosition, direction, distance, rayTraceList);

        if (rayTraceList.isEmpty())
            return Optional.empty();

        if (!excludeClientPlayer)
            return Optional.of(rayTraceList.getFirst());

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

    public void updateLookAt(Camera camera, float reach)
    {
        rayTraceGetFirst(camera, reach, true)
            .ifPresentOrElse(
                t ->
                {
                    lookAtObject = t.getCollisionObject();
                    lookAtTriangleIndex = t.triangleIndex();
                },
                () -> lookAtObject = null
            );
    }

    public PhysicsCollisionObject getLookAtObject()
    {
        return lookAtObject;
    }

    public int getLookAtTriangleIndex()
    {
        return lookAtTriangleIndex;
    }
}
