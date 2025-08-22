package steve6472.orbiter.network.packets.play.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.packets.play.GameClientboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record KickUser(User toKick, String reason) implements Packet<KickUser, GameClientboundListener>
{
    public static final Key KEY = Constants.key("game/cb/kick_user");
    public static final BufferCodec<ByteBuf, KickUser> BUFFER_CODEC = BufferCodec.of(
        ExtraBufferCodecs.USER, KickUser::toKick,
        BufferCodecs.STRING, KickUser::reason,
        KickUser::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, KickUser> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener listener)
    {
        listener.kickUser(toKick, reason);
    }
}
