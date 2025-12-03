package steve6472.orbiter.world.ecs.components.physics;

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
public class LinearVelocity extends Value3f
{
    public static final Codec<LinearVelocity> CODEC = codec(LinearVelocity::new);
    public static final BufferCodec<ByteBuf, LinearVelocity> BUFFER_CODEC = bufferCodec(LinearVelocity::new);

    public LinearVelocity(float x, float y, float z)
    {
        super(x, y, z);
    }

    public LinearVelocity()
    {
        super();
    }

    @Override
    protected Vec3Arg get(BodyInterface bi, int body)
    {
        return bi.getLinearVelocity(body);
    }

    @Override
    protected void set(BodyInterface bi, int body, Vec3Arg vec)
    {
        bi.setLinearVelocity(body, vec);
    }
}