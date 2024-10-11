package steve6472.orbiter.world.ecs.components.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;

import java.util.function.Consumer;
import java.util.function.Function;

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
    protected Function<Vector3f, Vector3f> get(PhysicsRigidBody body)
    {
        return body::getLinearVelocity;
    }

    @Override
    protected Consumer<Vector3f> set(PhysicsRigidBody body)
    {
        return body::setLinearVelocity;
    }
}