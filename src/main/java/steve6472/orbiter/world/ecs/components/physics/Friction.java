package steve6472.orbiter.world.ecs.components.physics;

import com.github.stephengold.joltjni.BodyInterface;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Friction extends Valuef
{
    public static final Codec<Friction> CODEC = codec(Friction::new);
    public static final BufferCodec<ByteBuf, Friction> BUFFER_CODEC = bufferCodec(Friction::new);

    public Friction(float val)
    {
        super(val);
    }

    public Friction()
    {
        super(0.5f);
    }

    @Override
    protected float get(BodyInterface bi, int body)
    {
        return bi.getFriction(body);
    }

    @Override
    protected void set(BodyInterface bi, int body, float value)
    {
        bi.setFriction(body, value);
    }
}