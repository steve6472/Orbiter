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
public class Gravity extends Value3f
{
    public static final Codec<Gravity> CODEC = codec(Gravity::new);
    public static final BufferCodec<ByteBuf, Gravity> BUFFER_CODEC = bufferCodec(Gravity::new);

    public Gravity(float x, float y, float z)
    {
        super(x, y, z);
    }

    public Gravity()
    {
        super();
    }

    @Override
    protected Function<Vector3f, Vector3f> get(PhysicsRigidBody body)
    {
        return body::getGravity;
    }

    @Override
    protected Consumer<Vector3f> set(PhysicsRigidBody body)
    {
        return body::setGravity;
    }
}