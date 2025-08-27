package steve6472.orbiter.world.ecs.systems.particle;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.OrlangValue;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.particle.MaxAge;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleUpdateEnvSystem extends IteratingProfiledSystem
{
    public ParticleUpdateEnvSystem()
    {
        super(Family.all(OrlangEnvironment.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        OrlangEnvironment env = Components.PARTICLE_ENVIRONMENT.get(entity);

        MaxAge maxAge = Components.MAX_AGE.get(entity);
        if (maxAge != null)
        {
            env.setValue(MaxAge.AGE, OrlangValue.num(maxAge.age));
            env.setValue(MaxAge.MAX_AGE, OrlangValue.num(maxAge.maxAge));
        }
    }
}
