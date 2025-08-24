package steve6472.orbiter.network.packets.game.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.game.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record JunkData(byte[] data) implements Packet<JunkData, GameHostboundListener>
{
    public static final Key KEY = Constants.key("game/hb/junk");
    public static final BufferCodec<ByteBuf, JunkData> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.BYTE_ARRAY, JunkData::data,
        JunkData::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, JunkData> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameHostboundListener gameListener)
    {

    }
}
