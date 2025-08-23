package steve6472.orbiter.network;

import com.badlogic.ashley.core.Component;
import io.netty.buffer.ByteBuf;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.core.ComponentEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public interface ExtraBufferCodecs
{
    BufferCodec<ByteBuf, Vector3f> VEC3F = BufferCodec.of(BufferCodecs.FLOAT, Vector3f::x, BufferCodecs.FLOAT, Vector3f::y, BufferCodecs.FLOAT, Vector3f::z, Vector3f::new);

    BufferCodec<ByteBuf, User> USER = new BufferCodec<>()
    {
        @Override
        public User decode(ByteBuf object)
        {
            boolean isDedicated = object.readBoolean();
            if (isDedicated)
            {
                return new DedicatedUser(BufferCodecs.UUID.decode(object), "");
            } else
            {
                throw new IllegalStateException("Steam user decode not implemented yet!");
            }
        }

        @Override
        public void encode(ByteBuf left, User right)
        {
            left.writeBoolean(right instanceof DedicatedUser);
            BufferCodecs.UUID.encode(left, right.uuid());
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

    BufferCodec<ByteBuf, List<Component>> COMPONENT_LIST = BufferCodec.of((buffer, components) -> {

        int componentCountIndex = buffer.writerIndex();
        buffer.writeInt(0);
        int componentCount = 0;
        for (Component component : components)
        {
            var componentEntryOptional = Components.getComponentByClass(component.getClass());
            if (componentEntryOptional.isEmpty())
                continue;

            ComponentEntry<?> componentEntry = componentEntryOptional.get();
            //noinspection unchecked
            BufferCodec<ByteBuf, Object> networkCodec = (BufferCodec<ByteBuf, Object>) componentEntry.getNetworkCodec();
            if (networkCodec == null)
                continue;

            buffer.writeInt(componentEntry.networkID());
            networkCodec.encode(buffer, component);
            componentCount++;
        }
        buffer.setInt(componentCountIndex, componentCount);
    }, (buffer) -> {
        int componentCount = buffer.readInt();
        List<Component> components = new ArrayList<>(componentCount);

        for (int i = 0; i < componentCount; i++)
        {
            int networkID = buffer.readInt();
            var componentEntryOptional = Components.getComponentByNetworkId(networkID);
            componentEntryOptional.ifPresent(componentEntry -> components.add(componentEntry.getNetworkCodec().decode(buffer)));
        }
        return components;
    });
}
