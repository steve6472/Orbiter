package steve6472.orbiter.world.ecs.systems.particle;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.particle.MaxAge;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleMaxAgeSystem extends IteratingProfiledSystem
{
    private final PooledEngine particleEngine;

    public ParticleMaxAgeSystem(World world)
    {
        super(Family.all(MaxAge.class).get());
        this.particleEngine = world.particleEngine();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        MaxAge maxAge = Components.MAX_AGE.get(entity);
        maxAge.age++;
        if (maxAge.age >= maxAge.maxAge)
        {
            particleEngine.removeEntity(entity);
        }
    }
}
