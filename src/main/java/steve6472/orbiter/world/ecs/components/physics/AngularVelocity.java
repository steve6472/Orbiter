package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
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
    protected Function<Vector3f, Vector3f> get(PhysicsRigidBody body)
    {
        return body::getAngularVelocity;
    }

    @Override
    protected Consumer<Vector3f> set(PhysicsRigidBody body)
    {
        return body::setAngularVelocity;
    }

    @Override
    public void reset()
    {
        set(0, 0, 0);
    }
}