package steve6472.orbiter.world.emitter;

import com.badlogic.ashley.core.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.QueryFunctionSet;

/**
 * Created by steve6472
 * Date: 9/8/2025
 * Project: Orbiter <br>
 */
public class EmitterQueryFunctions extends QueryFunctionSet
{
    public EmitterQueryFunctions(Entity entity)
    {
        Class<Double> D = Double.TYPE;
        Class<Boolean> B = Boolean.TYPE;

        functions.put("get_rot_y", OrlangValue.func(() ->
        {
            Rotation rotation = Components.ROTATION.get(entity);
            if (rotation == null)
                return 0;
            Quaternionf quat = rotation.toQuat();
            double yawRad = Math.atan2(
                2.0f * (quat.y * quat.w + quat.x * quat.z),
                1.0f - 2.0f * (quat.y * quat.y + quat.x * quat.x)
            );

            // Convert to degrees
            double yawDeg = Math.toDegrees(yawRad);

            // Normalize to [0, 360)
            yawDeg = (yawDeg % 360d + 360d) % 360d;
            return yawDeg;
        }));

        functions.put("total_linear_velocity", OrlangValue.func(() ->
        {
            LinearVelocity linearVelocity = Components.LINEAR_VELOCITY.get(entity);
            if (linearVelocity == null)
                return 0;
            return (double) Vector3f.length(linearVelocity.x(), linearVelocity.y(), linearVelocity.z());
        }));
    }
}
