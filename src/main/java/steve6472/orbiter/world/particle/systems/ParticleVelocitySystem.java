package steve6472.orbiter.world.particle.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.Velocity;
import steve6472.orbiter.world.particle.components.Position;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleVelocitySystem extends IteratingProfiledSystem
{
    public ParticleVelocitySystem()
    {
        super(Family.all(Position.class, Velocity.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        var position = ParticleComponents.POSITION.get(entity);
        var velocity = ParticleComponents.VELOCITY.get(entity);

        var environment = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);
        if (environment != null)
        {
            var linearAcceleration = ParticleComponents.LINEAR_ACCELERATION.get(entity);
            if (linearAcceleration != null)
            {
                linearAcceleration.value.evaluate(environment.env);

                var linearDragCoefficient = ParticleComponents.LINEAR_DRAG_COEFFICIENT.get(entity);
                float drag = 0;
                if (linearDragCoefficient != null)
                {
                    drag = (float) linearDragCoefficient.value.evaluateAndGet(environment.env);
                }

                float dragX = -drag * velocity.x;
                float dragY = -drag * velocity.y;
                float dragZ = -drag * velocity.z;

                float netAccX = linearAcceleration.value.fx() + dragX;
                float netAccY = linearAcceleration.value.fy() + dragY;
                float netAccZ = linearAcceleration.value.fz() + dragZ;

                velocity.x += netAccX * deltaTime;
                velocity.y += netAccY * deltaTime;
                velocity.z += netAccZ * deltaTime;
            }
        }

        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        position.z += velocity.z * deltaTime;
    }
}
