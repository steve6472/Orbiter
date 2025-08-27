package steve6472.orbiter.world.ecs.systems.particle;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.particle.ParticleFollowerId;
import steve6472.orbiter.world.ecs.components.particle.ParticleHolderId;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class RemoveInvalidFollowerSystem extends IteratingProfiledSystem
{
    private final PooledEngine particleEngine;

    public RemoveInvalidFollowerSystem(World world)
    {
        super(Family.all(ParticleFollowerId.class).get());
        this.particleEngine = world.particleEngine();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ParticleFollowerId follower = Components.PARTICLE_FOLLOWER.get(entity);
        ParticleHolderId holder = Components.PARTICLE_HOLDER.get(follower.entity);
        if (holder == null || holder.followerId != follower.followerId)
        {
            particleEngine.removeEntity(entity);
        }
    }
}
