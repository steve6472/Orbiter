package steve6472.orbiter.world.ecs.components.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class Lifetime implements Component, Pool.Poolable
{
    public int ticksLeft;

    public Lifetime(int ticksLeft)
    {
        this.ticksLeft = ticksLeft;
    }

    public Lifetime()
    {
        this(60);
    }

    public int ticksLeft()
    {
        return ticksLeft;
    }

    @Override
    public void reset()
    {
        ticksLeft = 60;
    }

    @Override
    public String toString()
    {
        return "Lifetime{" + "ticksLeft=" + ticksLeft + '}';
    }
}
