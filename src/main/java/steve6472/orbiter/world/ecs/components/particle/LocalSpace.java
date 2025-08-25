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

    public LocalSpace()
    {

    }

    @Override
    public void reset()
    {
        position = false;
        rotation = false;
        velocity = false;
    }
}
