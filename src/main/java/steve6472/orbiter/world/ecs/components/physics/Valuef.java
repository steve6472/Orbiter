package steve6472.orbiter.world.ecs.components.physics;

import com.github.stephengold.joltjni.BodyInterface;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.util.ComponentCodec;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/12/2024
 * Project: Orbiter <br>
 */
abstract class Valuef implements PhysicsProperty
{
    protected static <S extends Valuef> Codec<S> codec(Function<Float, S> constructor)
    {
        return ComponentCodec.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("val").forGetter(S::val)
        ).apply(instance, a -> () -> constructor.apply(a)));
    }

    protected static <S extends Valuef> BufferCodec<ByteBuf, S> bufferCodec(Function<Float, S> constructor)
    {
        return BufferCodec.of(
            BufferCodecs.FLOAT, S::val,
            constructor);
    }

    private float val;
    private boolean ecsModifyFlag;

    public Valuef(float val)
    {
        this.val = val;
    }

    public Valuef()
    {
        this(0);
    }

    public void set(float val)
    {
        this.val = val;
        setEcsModifyFlag();
    }

    public void add(float val)
    {
        this.val += val;
        setEcsModifyFlag();
    }

    public void sub(float val)
    {
        this.val -= val;
        setEcsModifyFlag();
    }

    public float val()
    {
        return val;
    }

    @Override
    public String toString()
    {
        return String.format("%s{" + "val=%.6f" + '}', getClass().getSimpleName(), val);
    }

    protected abstract float get(BodyInterface bi, int body);
    protected abstract void set(BodyInterface bi, int body, float value);

    @Override
    public ModifyState modifyComponent(BodyInterface bi, int body)
    {
        float v = get(bi, body);

        if (val == v)
            return ModifyState.noModification();

        set(v);
        return ModifyState.modifiedComponent();
    }

    @Override
    public void modifyBody(BodyInterface bi, int body)
    {
        set(bi, body, val);
    }

    public void setEcsModifyFlag()
    {
        ecsModifyFlag = true;
    }

    public void resetEcsModifyFlag()
    {
        ecsModifyFlag = false;
    }

    public boolean wasEcsModified()
    {
        return ecsModifyFlag;
    }
}
