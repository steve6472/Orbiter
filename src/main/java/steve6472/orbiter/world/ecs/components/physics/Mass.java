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
public class Mass extends Valuef
{
    public static final Codec<Mass> CODEC = codec(Mass::new);
    public static final BufferCodec<ByteBuf, Mass> BUFFER_CODEC = bufferCodec(Mass::new);

    public Mass(float val)
    {
        super(val);
    }

    public Mass()
    {
        super(1f);
    }

    @Override
    protected float get(PhysicsRigidBody body)
    {
        return body.getMass();
    }

    @Override
    protected Consumer<Float> set(PhysicsRigidBody body)
    {
        return body::setMass;
    }
}