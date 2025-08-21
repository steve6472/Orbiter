package steve6472.orbiter.network.packets.login.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record LoginResponse(boolean accepted, String hostUsername) implements Packet<LoginResponse, LoginClientboundListener>
{
    public static final Key KEY = Constants.key("login/login_response");
    public static final BufferCodec<ByteBuf, LoginResponse> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.BOOL, LoginResponse::accepted,
        BufferCodecs.STRING, LoginResponse::hostUsername,
        LoginResponse::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, LoginResponse> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LoginClientboundListener listener)
    {
        listener.loginResponse(accepted, hostUsername);
    }
}
