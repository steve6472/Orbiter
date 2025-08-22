package steve6472.orbiter.network.packets.play.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;
import steve6472.orbiter.network.packets.play.GameClientboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record EnterWorld() implements Packet<EnterWorld, GameClientboundListener>
{
    public static final Key KEY = Constants.key("game/cb/enter_world");
    public static final BufferCodec<ByteBuf, EnterWorld> BUFFER_CODEC = BufferCodec.unit(new EnterWorld());

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, EnterWorld> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener listener)
    {
        listener.enterWorld();
    }
}
