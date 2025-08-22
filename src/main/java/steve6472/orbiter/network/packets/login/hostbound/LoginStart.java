package steve6472.orbiter.network.packets.login.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.login.LoginHostboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record LoginStart(String username) implements Packet<LoginStart, LoginHostboundListener>
{
    public static final Key KEY = Constants.key("login/hb/login_start");
    public static final BufferCodec<ByteBuf, LoginStart> BUFFER_CODEC = BufferCodec.of(BufferCodecs.STRING, LoginStart::username, LoginStart::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, LoginStart> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LoginHostboundListener listener)
    {
        listener.loginStart(this);
    }
}
