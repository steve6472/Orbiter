package steve6472.orbiter.network.packets.game;

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
public record PlayerDisconnected(SteamID disconnectedPlayer) implements Packet<PlayerDisconnected, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("kick_user");
    public static final BufferCodec<ByteBuf, PlayerDisconnected> BUFFER_CODEC = BufferCodec.of(ExtraBufferCodecs.STEAM_ID, PlayerDisconnected::disconnectedPlayer, PlayerDisconnected::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, PlayerDisconnected> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        listener.disconnectPlayer(disconnectedPlayer);
    }
}
