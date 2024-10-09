package steve6472.orbiter.world.ecs.components;

import com.codedisaster.steamworks.SteamID;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.util.OrbiterCodecs;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
public record MPControlled(SteamID controller)
{
    public static final Codec<MPControlled> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrbiterCodecs.STEAM_ID.fieldOf("user_id").forGetter(MPControlled::controller)
    ).apply(instance, MPControlled::new));

    public static final BufferCodec<ByteBuf, MPControlled> BUFFER_CODEC = BufferCodec.of(
        ExtraBufferCodecs.STEAM_ID, MPControlled::controller,
        MPControlled::new);
}
