package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.ExtraBufferCodecs;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record AddJoint(UUID bodyA, UUID bodyB, Vector3f pivotA, Vector3f pivotB, Matrix3f rotA, Matrix3f rotB) implements Packet<AddJoint, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("add_joints");
    public static final BufferCodec<ByteBuf, AddJoint> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, AddJoint::bodyA,
        BufferCodecs.UUID, AddJoint::bodyB,
        ExtraBufferCodecs.VEC3F, AddJoint::pivotA,
        ExtraBufferCodecs.VEC3F, AddJoint::pivotB,
        ExtraBufferCodecs.MAT3F, AddJoint::rotA,
        ExtraBufferCodecs.MAT3F, AddJoint::rotB,
        AddJoint::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, AddJoint> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        gameListener.addJoints(this);
    }
}
