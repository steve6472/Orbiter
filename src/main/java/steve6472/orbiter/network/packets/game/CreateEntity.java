package steve6472.orbiter.network.packets.game;

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
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.ecs.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record CreateEntity(UUID uuid, int componentCount, ByteBuf buffer) implements Packet<CreateEntity, GameListener>
{
    public static final Key KEY = Constants.key("create_entity");
    public static final BufferCodec<ByteBuf, CreateEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, CreateEntity::uuid,
        BufferCodecs.INT, CreateEntity::componentCount,
        ExtraBufferCodecs.BUFFER, CreateEntity::buffer,
        CreateEntity::new);

    public CreateEntity(Entity entity)
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
    public BufferCodec<ByteBuf, CreateEntity> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameListener gameListener)
    {
        List<Object> components = new ArrayList<>(componentCount + 1);
        components.add(uuid);
        for (int i = 0; i < componentCount; i++)
        {
            Key componentKey = BufferCodecs.KEY.decode(buffer);
            Object component = Registries.COMPONENT.get(componentKey).getNetworkCodec().decode(buffer);
            components.add(component);
        }

        buffer.release();

        gameListener.createEntity(uuid, components);
    }
}
