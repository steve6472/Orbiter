package steve6472.orbiter.world.ecs.components.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import org.joml.Quaternionf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Rotation implements PhysicsProperty
{
    public static final Codec<Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("x").forGetter(Rotation::x),
        Codec.FLOAT.fieldOf("y").forGetter(Rotation::y),
        Codec.FLOAT.fieldOf("z").forGetter(Rotation::z),
        Codec.FLOAT.fieldOf("w").forGetter(Rotation::w)
    ).apply(instance, Rotation::new));

    public static final BufferCodec<ByteBuf, Rotation> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.FLOAT, Rotation::x,
        BufferCodecs.FLOAT, Rotation::y,
        BufferCodecs.FLOAT, Rotation::z,
        BufferCodecs.FLOAT, Rotation::w,
        Rotation::new);

    private float x, y, z, w;

    public Rotation(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Rotation()
    {
        this(0, 0, 0, 1);
    }

    public void set(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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

    public float w()
    {
        return w;
    }

    public Quaternionf toQuat()
    {
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public String toString()
    {
        return String.format("Rotation{" + "x=%.6f, y=%.6f, z=%.6f, w=%.6f" + '}', x, y, z, w);
    }

    private static final Quaternionf STORE = new Quaternionf();

    @Override
    public ModifyState modifyComponent(PhysicsRigidBody body)
    {
        Convert.physGetToJoml(body::getPhysicsRotation, STORE);

        if (STORE.x == x && STORE.y == y && STORE.z == z && STORE.w == w)
            return ModifyState.noModification();

        set(STORE.x, STORE.y, STORE.z, STORE.w);
        return ModifyState.modifiedComponent();
    }

    @Override
    public void modifyBody(PhysicsRigidBody body)
    {
//        if (!OrbiterApp.getInstance().getSteam().isHost())
//            return;

        body.setPhysicsRotation(Convert.jomlToPhys(toQuat()));
    }
}
