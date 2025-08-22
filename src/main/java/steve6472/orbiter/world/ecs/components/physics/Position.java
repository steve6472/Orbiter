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
public class Position extends Value3f implements Component
{
    public static final Codec<Position> CODEC = codec(Position::new);
    public static final BufferCodec<ByteBuf, Position> BUFFER_CODEC = bufferCodec(Position::new);

    public Position(float x, float y, float z)
    {
        super(x, y, z);
    }

    public Position()
    {
        super();
    }

    @Override
    protected Function<Vector3f, Vector3f> get(PhysicsRigidBody body)
    {
        return body::getPhysicsLocation;
    }

    @Override
    protected Consumer<Vector3f> set(PhysicsRigidBody body)
    {
        return body::setPhysicsLocation;
    }
}