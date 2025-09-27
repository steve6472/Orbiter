package steve6472.orbiter.world.particle.components;

import com.badlogic.ashley.core.Entity;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class ParticleFollowerId implements ParticleComponent
{
    public int followerId;
    public Entity entity;
    public String locator;

    @Override
    public void reset()
    {
        followerId = 0;
        entity = null;
        locator = null;
    }

    @Override
    public String toString()
    {
        return "ParticleFollowerId{" + "followerId=" + followerId + ", entity=" + entity + '}';
    }
}
