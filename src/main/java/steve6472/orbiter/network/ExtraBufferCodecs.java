package steve6472.orbiter.network;

import com.codedisaster.steamworks.SteamID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public interface ExtraBufferCodecs
{
    BufferCodec<ByteBuf, Vector3f> VEC3F = BufferCodec.of(BufferCodecs.FLOAT, Vector3f::x, BufferCodecs.FLOAT, Vector3f::y, BufferCodecs.FLOAT, Vector3f::z, Vector3f::new);
    /// @deprecated WIP
    @Deprecated
    BufferCodec<ByteBuf, SteamID> STEAM_USER = BufferCodec.of(BufferCodecs.LONG, SteamID::getNativeHandle, SteamID::createFromNativeHandle);
    BufferCodec<ByteBuf, DedicatedUser> DEDICATED_USER = BufferCodec.of(BufferCodecs.STRING, User::username, DedicatedUser::new);

    BufferCodec<ByteBuf, User> USER = new BufferCodec<ByteBuf, User>()
    {
        @Override
        public User decode(ByteBuf object)
        {
            boolean isDedicated = object.readBoolean();
            if (isDedicated)
            {
                return DEDICATED_USER.decode(object);
            } else
            {
                throw new IllegalStateException("Steam user decode not implemented yet!");
//                return STEAM_USER.decode(object);
            }
        }

        @Override
        public void encode(ByteBuf left, User right)
        {
            if (right instanceof DedicatedUser dedUser)
            {
                left.writeBoolean(true);
                DEDICATED_USER.encode(left, dedUser);
            }
        }
    };

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
