package steve6472.orbiter.network.packets.game;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.api.User;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public record SpawnPlayerCharacter(User player, boolean VR) implements Packet<SpawnPlayerCharacter, GameListener>
{
    public static final Key KEY = Constants.key("spawn_player");
    public static final BufferCodec<ByteBuf, SpawnPlayerCharacter> BUFFER_CODEC = BufferCodec.of(
        ExtraBufferCodecs.USER, SpawnPlayerCharacter::player,
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
