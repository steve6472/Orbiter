package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public interface ExtraBufferCodecs
{
    BufferCodec<ByteBuf, Vector3f> VEC3F = BufferCodec.of(BufferCodecs.FLOAT, Vector3f::x, BufferCodecs.FLOAT, Vector3f::y, BufferCodecs.FLOAT, Vector3f::z, Vector3f::new);
    BufferCodec<ByteBuf, SteamID> STEAM_ID = BufferCodec.of(BufferCodecs.LONG, SteamID::getNativeHandle, SteamID::createFromNativeHandle);
}
