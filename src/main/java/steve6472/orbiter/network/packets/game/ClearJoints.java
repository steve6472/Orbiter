package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record ClearJoints(UUID uuid) implements Packet<ClearJoints, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("clear_joints");
    public static final BufferCodec<ByteBuf, ClearJoints> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, ClearJoints::uuid,
        ClearJoints::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, ClearJoints> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        gameListener.clearJoints(uuid);
    }
}
