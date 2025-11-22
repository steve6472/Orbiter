package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import org.joml.Vector3f;
import steve6472.orbiter.Constants;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class VelocitySystem extends IteratingProfiledSystem
{
    public VelocitySystem()
    {
        super(Family.all(Position.class, LinearVelocity.class).exclude(Tag.Physics.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Position position = Components.POSITION.get(entity);
        LinearVelocity linearVelocity = Components.LINEAR_VELOCITY.get(entity);

        Vector3f gravity = new Vector3f(Constants.GRAVITY);
        Components.GRAVITY.ifPresent(entity, g -> gravity.set(g.value()));

        Vector3f pos = position.toVec3f();
        Vector3f vel = linearVelocity.toVec3f();

        // Update velocity: v = v + gravity * dt
        float newVelX = vel.x + gravity.x * deltaTime;
        float newVelY = vel.y + gravity.y * deltaTime;
        float newVelZ = vel.z + gravity.z * deltaTime;

        linearVelocity.set(newVelX, newVelY, newVelZ);

        // Update position: p = p + v * dt
        float newPosX = pos.x + newVelX * deltaTime;
        float newPosY = pos.y + newVelY * deltaTime;
        float newPosZ = pos.z + newVelZ * deltaTime;

        position.set(newPosX, newPosY, newPosZ);
    }

    public static Vector3f[] simulateTrajectory(Vector3f start, Vector3f startVelocity, int ticks)
    {
        Vector3f[] positions = new Vector3f[ticks];

        // Clone starting position and velocity to avoid mutating inputs
        Vector3f pos = new Vector3f(start);
        Vector3f vel = new Vector3f(startVelocity);

        float dt = 1f / Constants.TICKS_IN_SECOND;

        for (int i = 0; i < ticks; i++)
        {
            // Store current position
            positions[i] = new Vector3f(pos);

            // Update velocity: v = v + g * dt
            vel.fma(dt, Constants.GRAVITY);

            // Update position: p = p + v * dt
            pos.fma(dt, vel);
        }

        return positions;
    }
}
