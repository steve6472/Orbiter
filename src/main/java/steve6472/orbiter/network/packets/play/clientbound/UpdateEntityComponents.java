package steve6472.orbiter.network.packets.play.clientbound;

import com.badlogic.ashley.core.Component;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.packets.play.GameClientboundListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record UpdateEntityComponents(UUID uuid, List<Component> components, int[] removes) implements Packet<UpdateEntityComponents, GameClientboundListener>
{
    public static final Key KEY = Constants.key("game/cb/update_entity_components");
    public static final BufferCodec<ByteBuf, UpdateEntityComponents> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, UpdateEntityComponents::uuid,
        ExtraBufferCodecs.COMPONENT_LIST, UpdateEntityComponents::components,
        BufferCodecs.INT_ARRAY, UpdateEntityComponents::removes,
        UpdateEntityComponents::new);

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, UpdateEntityComponents> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener listener)
    {
        listener.updateEntity(uuid, components, removes);
    }

    @Override
    public String toString()
    {
        return "UpdateEntityComponents{" + "uuid=" + uuid + ", components=" + components + ", removes=" + Arrays.toString(removes) + '}';
    }
}
