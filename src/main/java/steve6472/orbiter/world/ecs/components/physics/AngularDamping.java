package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
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
public class AngularDamping extends Valuef implements Component
{
    public static final Codec<AngularDamping> CODEC = codec(AngularDamping::new);
    public static final BufferCodec<ByteBuf, AngularDamping> BUFFER_CODEC = bufferCodec(AngularDamping::new);

    public AngularDamping(float val)
    {
        super(val);
    }

    public AngularDamping()
    {
        super(0);
    }

    @Override
    protected float get(PhysicsRigidBody body)
    {
        return body.getAngularDamping();
    }

    @Override
    protected Consumer<Float> set(PhysicsRigidBody body)
    {
        return body::setAngularDamping;
    }
}