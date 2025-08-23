package steve6472.orbiter.network.packets.play.hostbound;

import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.packets.play.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record PlayerMove(Vector3f position) implements Packet<PlayerMove, GameHostboundListener>
{
    public static final Key KEY = Constants.key("game/hb/player_move");
    public static final BufferCodec<ByteBuf, PlayerMove> BUFFER_CODEC = BufferCodec.of(ExtraBufferCodecs.VEC3F, PlayerMove::position, PlayerMove::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, PlayerMove> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameHostboundListener listener)
    {
        listener.movePlayer(position);
    }
}
