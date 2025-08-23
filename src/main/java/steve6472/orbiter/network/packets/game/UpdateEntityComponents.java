package steve6472.orbiter.network.packets.game;

import com.badlogic.ashley.core.Component;
import io.netty.buffer.ByteBuf;
import steve6472.core.log.Log;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.network.packets.play.GameClientboundListener;
import steve6472.orbiter.world.ecs.Components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public record UpdateEntityComponents(UUID uuid, int componentsToAdd, ByteBuf addBuffer, int[] removes) implements Packet<UpdateEntityComponents, GameClientboundListener>
{
    private static final Logger LOGGER = Log.getLogger(UpdateEntityComponents.class);
    public static final Key KEY = Constants.key("game/cb/update_entity_components");
    public static final BufferCodec<ByteBuf, UpdateEntityComponents> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.UUID, UpdateEntityComponents::uuid,
        BufferCodecs.INT, UpdateEntityComponents::componentsToAdd,
        ExtraBufferCodecs.BUFFER, UpdateEntityComponents::addBuffer,
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
        List<Component> components = new ArrayList<>(componentsToAdd);
        for (int i = 0; i < componentsToAdd; i++)
        {
            int networkID = BufferCodecs.INT.decode(addBuffer);
            var componentEntryOptional = Components.getComponentByNetworkId(networkID);
            if (componentEntryOptional.isPresent())
            {
                components.add(componentEntryOptional.get().getNetworkCodec().decode(addBuffer));
            } else
            {
                LOGGER.warning("Recieved packet with component networkID of " + networkID + " but no such component exists (for add)");
            }
        }
        addBuffer.release();

        listener.updateEntity(uuid, components, removes);
    }
}
