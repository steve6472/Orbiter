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
import steve6472.orbiter.network.packets.game.GameListener;
import steve6472.orbiter.network.packets.play.GameClientboundListener;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record CreateEntity(UUID uuid, Key entityType) implements Packet<CreateEntity, GameClientboundListener>
{
    public static final Key KEY = Constants.key("create_entity");
    public static final BufferCodec<ByteBuf, CreateEntity> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, CreateEntity::uuid,
        BufferCodecs.KEY, CreateEntity::entityType,
        CreateEntity::new);

    public CreateEntity(UUID uuid, EntityBlueprint blueprint)
    {
        this(uuid, blueprint.key());
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
    public void handlePacket(GameClientboundListener gameListener)
    {
        gameListener.createEntity(uuid, entityType);
//        List<Component> components = new ArrayList<>(componentCount + 1);
//        components.add(new UUIDComp(uuid));
//        for (int i = 0; i < componentCount; i++)
//        {
//            Key componentKey = BufferCodecs.KEY.decode(buffer);
//            Component component = Registries.COMPONENT.get(componentKey).getNetworkCodec().decode(buffer);
//            components.add(component);
//        }
//
//        buffer.release();
//
//        gameListener.createEntity(uuid, components);
    }
}
