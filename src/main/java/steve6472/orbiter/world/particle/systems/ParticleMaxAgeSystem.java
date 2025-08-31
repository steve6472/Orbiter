package steve6472.orbiter.world.particle.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.MaxAge;
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

    private long now;

    @Override
    public void update(float deltaTime)
    {
        now = System.currentTimeMillis();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        MaxAge maxAge = ParticleComponents.MAX_AGE.get(entity);
        if (maxAge.calculateAge(now) >= maxAge.maxAge)
        {
            particleEngine.removeEntity(entity);
        }
    }
}
