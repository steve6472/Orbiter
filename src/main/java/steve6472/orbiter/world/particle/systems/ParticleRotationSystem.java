package steve6472.orbiter.world.particle.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.Rotation;
import steve6472.orbiter.world.particle.components.Velocity;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleRotationSystem extends IteratingProfiledSystem
{
    public ParticleRotationSystem()
    {
        super(Family.all(Rotation.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        var rotation = ParticleComponents.ROTATION.get(entity);

        var environment = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);
        if (environment != null)
        {
            var linearAcceleration = rotation.acceleration;
            if (linearAcceleration != null)
            {
                linearAcceleration.evaluate(environment);

                var linearDragCoefficient = rotation.dragCoefficient;
                float drag = 0;
                if (linearDragCoefficient != null)
                {
                    drag = (float) linearDragCoefficient.evaluateAndGet(environment);
                }

                float dragX = -drag * rotation.rate;

                float netAccX = linearAcceleration.fget() + dragX;

                rotation.rate += netAccX * deltaTime;
            }
        }

        rotation.rotation += rotation.rate * deltaTime;
    }
}
