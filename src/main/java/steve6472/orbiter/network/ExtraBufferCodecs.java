package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.joml.Matrix3f;
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

    BufferCodec<ByteBuf, Matrix3f> MAT3F = new BufferCodec<>()
    {
        @Override
        public Matrix3f decode(ByteBuf o)
        {
            Matrix3f mat = new Matrix3f();
            mat.m00 = o.readFloat();
            mat.m10 = o.readFloat();
            mat.m20 = o.readFloat();
            mat.m01 = o.readFloat();
            mat.m11 = o.readFloat();
            mat.m21 = o.readFloat();
            mat.m02 = o.readFloat();
            mat.m12 = o.readFloat();
            mat.m22 = o.readFloat();
            return mat;
        }

        @Override
        public void encode(ByteBuf o, Matrix3f mat)
        {
            o.writeFloat(mat.m00);
            o.writeFloat(mat.m10);
            o.writeFloat(mat.m20);
            o.writeFloat(mat.m01);
            o.writeFloat(mat.m11);
            o.writeFloat(mat.m21);
            o.writeFloat(mat.m02);
            o.writeFloat(mat.m12);
            o.writeFloat(mat.m22);
        }
    };

    /// Must manually release the buffer after decoding!
    BufferCodec<ByteBuf, ByteBuf> BUFFER = BufferCodec.of((networkBuff, passBuff) -> {
        networkBuff.writeInt(passBuff.writerIndex());
        networkBuff.writeBytes(passBuff);
        passBuff.release();
    }, (buff) -> {
        int size = buff.readInt();
        ByteBuf retBuf = PooledByteBufAllocator.DEFAULT.buffer(size);
        buff.readBytes(retBuf, size);
        return retBuf;
    });
}
