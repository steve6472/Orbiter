package steve6472.orbiter.world.ecs.components.particle;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.orlang.codec.OrVec3;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class Scale implements Component, Pool.Poolable
{
//    public static final Codec<Scale> CODEC = Codec.FLOAT.xmap(Scale::new, Scale::scale);
//    public static final BufferCodec<ByteBuf, Scale> BUFFER_CODEC = BufferCodec.of(BufferCodecs.FLOAT, Scale::scale, Scale::new);

    public OrVec3 scale;

    @Override
    public void reset()
    {
        scale = null;
    }
}
