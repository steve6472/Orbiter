package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
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
public class Friction extends Valuef implements Component
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
    protected float get(PhysicsRigidBody body)
    {
        return body.getFriction();
    }

    @Override
    protected Consumer<Float> set(PhysicsRigidBody body)
    {
        return body::setFriction;
    }
}