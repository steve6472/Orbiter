package steve6472.orbiter.network.packets.game;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.network.ExtraBufferCodecs;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record SpawnPlayerCharacter(SteamID player, boolean VR) implements Packet<SpawnPlayerCharacter, GameListener>
{
    public static final Key KEY = Key.defaultNamespace("spawn_player");
    public static final BufferCodec<ByteBuf, SpawnPlayerCharacter> BUFFER_CODEC = BufferCodec.of(
        ExtraBufferCodecs.STEAM_ID, SpawnPlayerCharacter::player,
        BufferCodecs.BOOL, SpawnPlayerCharacter::VR,
        SpawnPlayerCharacter::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, SpawnPlayerCharacter> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener listener)
    {
        listener.spawnPlayer(player, VR);
    }
}
