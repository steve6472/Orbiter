package steve6472.orbiter.world.ecs.components.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class LinearDamping extends Valuef
{
    public static final Codec<LinearDamping> CODEC = codec(LinearDamping::new);
    public static final BufferCodec<ByteBuf, LinearDamping> BUFFER_CODEC = bufferCodec(LinearDamping::new);

    public LinearDamping(float val)
    {
        super(val);
    }

    public LinearDamping()
    {
        super(0);
    }

    @Override
    protected float get(PhysicsRigidBody body)
    {
        return body.getLinearDamping();
    }

    @Override
    protected Consumer<Float> set(PhysicsRigidBody body)
    {
        return body::setLinearDamping;
    }
}