package steve6472.orbiter.world.ecs.components.physics;

import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.Convert;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/12/2024
 * Project: Orbiter <br>
 */
abstract class Value3f implements PhysicsProperty
{
    protected static <S extends Value3f> Codec<S> codec(Function3<Float, Float, Float, S> constructor)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(S::x),
            Codec.FLOAT.fieldOf("y").forGetter(S::y),
            Codec.FLOAT.fieldOf("z").forGetter(S::z)
        ).apply(instance, constructor));
    }

    protected static <S extends Value3f> BufferCodec<ByteBuf, S> bufferCodec(Function3<Float, Float, Float, S> constructor)
    {
        return BufferCodec.of(
            BufferCodecs.FLOAT, S::x,
            BufferCodecs.FLOAT, S::y,
            BufferCodecs.FLOAT, S::z,
            constructor);
    }

    private float x, y, z;
    private boolean ecsModifyFlag;

    public Value3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Value3f()
    {
        this(0, 0, 0);
    }

    public void set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        setEcsModifyFlag();
    }

    public void add(float x, float y, float z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        setEcsModifyFlag();
    }

    public void sub(float x, float y, float z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        setEcsModifyFlag();
    }

    public float x()
    {
        return x;
    }

    public float y()
    {
        return y;
    }

    public float z()
    {
        return z;
    }

    public Vector3f toVec3f()
    {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString()
    {
        return String.format("%s{" + "x=%.6f, y=%.6f, z=%.6f" + '}', getClass().getSimpleName(), x, y, z);
    }

    protected abstract Vec3Arg get(BodyInterface bi, int body);
    protected abstract void set(BodyInterface bi, int body, Vec3Arg vec);

    protected static final Vector3f STORE = new Vector3f();

    @Override
    public ModifyState modifyComponent(BodyInterface bi, int body)
    {
        Convert.physToJoml(get(bi, body), STORE);

        if (STORE.x == x && STORE.y == y && STORE.z == z)
            return ModifyState.noModification();

        set(STORE.x, STORE.y, STORE.z);
        return ModifyState.modifiedComponent();
    }

    @Override
    public void modifyBody(BodyInterface bi, int body)
    {
        set(bi, body, Convert.jomlToPhys(toVec3f()));
    }

    @Override
    public void setEcsModifyFlag()
    {
        ecsModifyFlag = true;
    }

    @Override
    public void resetEcsModifyFlag()
    {
        ecsModifyFlag = false;
    }

    @Override
    public boolean wasEcsModified()
    {
        return ecsModifyFlag;
    }
}
