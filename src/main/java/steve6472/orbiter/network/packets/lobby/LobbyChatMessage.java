package steve6472.orbiter.network.packets.lobby;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;

import java.awt.*;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record LobbyChatMessage(String message) implements Packet<LobbyChatMessage, LobbyListener>
{
    public static final Key KEY = Constants.key("lobby_chat");
    public static final BufferCodec<ByteBuf, LobbyChatMessage> BUFFER_CODEC = BufferCodec.of(BufferCodecs.STRING, LobbyChatMessage::message, LobbyChatMessage::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, LobbyChatMessage> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LobbyListener listener)
    {
        listener.chatMessage(message);
    }
}
