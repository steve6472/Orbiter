package steve6472.orbiter.network.packets.game.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.game.GameClientboundListener;
import steve6472.orbiter.network.packets.game.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record ConfirmEnterWorld() implements Packet<ConfirmEnterWorld, GameHostboundListener>
{
    public static final Key KEY = Constants.key("game/hb/confirm_enter_world");
    public static final BufferCodec<ByteBuf, ConfirmEnterWorld> BUFFER_CODEC = BufferCodec.unit(new ConfirmEnterWorld());

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, ConfirmEnterWorld> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameHostboundListener listener)
    {
        listener.confirmEnterWorld();
    }
}
