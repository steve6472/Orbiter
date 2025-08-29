package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import steve6472.core.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class ParticleHolderId implements Component, Pool.Poolable
{
    public static final int UNASSIGNED = 0;

    public int followerId;

    public static int generateRandomId()
    {
        return RandomUtil.randomInt(1, Integer.MAX_VALUE);
    }

    @Override
    public void reset()
    {
        followerId = 0;
    }

    @Override
    public String toString()
    {
        return "ParticleHolderId{" + "followerId=" + followerId + '}';
    }
}
