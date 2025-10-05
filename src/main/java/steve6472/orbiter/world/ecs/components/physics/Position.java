package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Position extends Value3f implements Component, Pool.Poolable
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
    protected Vec3Arg get(BodyInterface bi, int body)
    {
        return bi.getPosition(body).toVec3();
    }

    @Override
    protected void set(BodyInterface bi, int body, Vec3Arg vec)
    {
        bi.setPosition(body, vec.toRVec3(), EActivation.Activate);
    }

    @Override
    public void reset()
    {
        set(0, 0, 0);
    }
}