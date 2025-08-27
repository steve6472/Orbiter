package steve6472.orbiter.world.ecs.components.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class LocalSpace implements Component, Pool.Poolable
{
    public boolean position;
    public boolean rotation;
    public boolean velocity;

    @Override
    public void reset()
    {
        position = false;
        rotation = false;
        velocity = false;
    }

    @Override
    public String toString()
    {
        return "LocalSpace{" + "position=" + position + ", rotation=" + rotation + ", velocity=" + velocity + '}';
    }
}
