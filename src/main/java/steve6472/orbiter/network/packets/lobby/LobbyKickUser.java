package steve6472.orbiter.network.packets.lobby;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.ExtraBufferCodecs;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record LobbyKickUser(SteamID toKick) implements Packet<LobbyKickUser, LobbyListener>
{
    public static final Key KEY = Key.defaultNamespace("lobby_kick_user");
    public static final BufferCodec<ByteBuf, LobbyKickUser> BUFFER_CODEC = BufferCodec.of(ExtraBufferCodecs.STEAM_ID, LobbyKickUser::toKick, LobbyKickUser::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, LobbyKickUser> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(LobbyListener listener)
    {
        listener.kickUser(toKick);
    }
}
