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
public class LinearFactor extends Value3f implements Component, Pool.Poolable
{
    public static final Codec<LinearFactor> CODEC = codec(LinearFactor::new);
    public static final BufferCodec<ByteBuf, LinearFactor> BUFFER_CODEC = bufferCodec(LinearFactor::new);

    public LinearFactor(float x, float y, float z)
    {
        super(x, y, z);
    }

    public LinearFactor()
    {
        super(1, 1, 1);
    }

    @Override
    protected Function<Vector3f, Vector3f> get(PhysicsRigidBody body)
    {
        return body::getLinearFactor;
    }

    @Override
    protected Consumer<Vector3f> set(PhysicsRigidBody body)
    {
        return body::setLinearFactor;
    }

    @Override
    public void reset()
    {
        set(1, 1, 1);
    }
}