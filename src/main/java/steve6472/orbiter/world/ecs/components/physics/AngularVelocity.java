package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class AngularVelocity extends Value3f implements Component, Pool.Poolable
{
    public static final Codec<AngularVelocity> CODEC = codec(AngularVelocity::new);
    public static final BufferCodec<ByteBuf, AngularVelocity> BUFFER_CODEC = bufferCodec(AngularVelocity::new);

    public AngularVelocity(float x, float y, float z)
    {
        super(x, y, z);
    }

    public AngularVelocity()
    {
        super();
    }

    @Override
    protected Vec3Arg get(BodyInterface bi, int body)
    {
        return bi.getAngularVelocity(body);
    }

    @Override
    protected void set(BodyInterface bi, int body, Vec3Arg vec)
    {
        bi.setAngularVelocity(body, vec);
    }

    @Override
    public void reset()
    {
        set(0, 0, 0);
    }
}