package steve6472.orbiter.world.ecs.components.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;

import java.util.function.Consumer;
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
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("val").forGetter(S::val)
        ).apply(instance, constructor));
    }

    protected static <S extends Valuef> BufferCodec<ByteBuf, S> bufferCodec(Function<Float, S> constructor)
    {
        return BufferCodec.of(
            BufferCodecs.FLOAT, S::val,
            constructor);
    }

    private float val;

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
    }

    public void add(float val)
    {
        this.val += val;
    }

    public void sub(float val)
    {
        this.val -= val;
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

    protected abstract float get(PhysicsRigidBody body);
    protected abstract Consumer<Float> set(PhysicsRigidBody body);

    @Override
    public ModifyState modifyComponent(PhysicsRigidBody body)
    {
        float v = get(body);

        if (val == v)
            return ModifyState.noModification();

        set(v);
        return ModifyState.modifiedComponent();
    }

    @Override
    public void modifyBody(PhysicsRigidBody body)
    {
//        if (!OrbiterApp.getInstance().getSteam().isHost())
//            return;

        set(body).accept(val);
    }
}
