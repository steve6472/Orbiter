package steve6472.orbiter.world.particle.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class ParticleFollowerId implements Component, Pool.Poolable
{
    public int followerId;
    public Entity entity;

    @Override
    public void reset()
    {
        followerId = 0;
        entity = null;
    }

    @Override
    public String toString()
    {
        return "ParticleFollowerId{" + "followerId=" + followerId + ", entity=" + entity + '}';
    }
}
