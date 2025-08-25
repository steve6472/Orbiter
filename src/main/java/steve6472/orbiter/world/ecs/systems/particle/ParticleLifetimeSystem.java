package steve6472.orbiter.world.ecs.systems.particle;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.particle.Lifetime;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleLifetimeSystem extends IteratingProfiledSystem
{
    private final PooledEngine particleEngine;

    public ParticleLifetimeSystem(World world)
    {
        super(Family.all(Lifetime.class).get());
        this.particleEngine = world.particleEngine();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Lifetime lifetime = Components.LIFETIME.get(entity);
        lifetime.ticksLeft--;
        if (lifetime.ticksLeft <= 0)
        {
            particleEngine.removeEntity(entity);
        }
    }
}
