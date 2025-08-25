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
public class Scale implements Component, Pool.Poolable
{
    public static final Codec<Scale> CODEC = Codec.FLOAT.xmap(Scale::new, Scale::scale);
    public static final BufferCodec<ByteBuf, Scale> BUFFER_CODEC = BufferCodec.of(BufferCodecs.FLOAT, Scale::scale, Scale::new);

    public float scale;

    public Scale(float scale)
    {
        this.scale = scale;
    }

    public Scale()
    {
        this(1f);
    }

    public float scale()
    {
        return scale;
    }

    @Override
    public void reset()
    {
        scale = 1;
    }
}
