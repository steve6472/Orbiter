package steve6472.orbiter.network.packets.play.clientbound;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.packets.play.GameClientboundListener;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record CreateCustomEntity(UUID uuid, int componentCount, ByteBuf buffer) implements Packet<CreateCustomEntity, GameClientboundListener>
{
    public static final Key KEY = Constants.key("create_custom_entity");
    public static final BufferCodec<ByteBuf, CreateCustomEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, CreateCustomEntity::uuid,
        BufferCodecs.INT, CreateCustomEntity::componentCount,
        ExtraBufferCodecs.BUFFER, CreateCustomEntity::buffer,
        CreateCustomEntity::new);

    public CreateCustomEntity(Entity entity)
    {
        Pair<Integer, ByteBuf> integerByteBufPair = NetworkSerialization.entityComponentsToBuffer(entity);
        this(Components.UUID.get(entity).uuid(), integerByteBufPair.getFirst(), integerByteBufPair.getSecond());
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, CreateCustomEntity> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener gameListener)
    {
        List<Component> components = new ArrayList<>(componentCount + 1);
        components.add(new UUIDComp(uuid));
        for (int i = 0; i < componentCount; i++)
        {
            Key componentKey = BufferCodecs.KEY.decode(buffer);
            Component component = Registries.COMPONENT.get(componentKey).getNetworkCodec().decode(buffer);
            components.add(component);
        }

        buffer.release();

        gameListener.createCustomEntity(uuid, components);
    }
}
