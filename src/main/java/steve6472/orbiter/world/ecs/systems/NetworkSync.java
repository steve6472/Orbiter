package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import steve6472.core.log.Log;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.packets.game.UpdateEntityComponents;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.*;

import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class NetworkSync extends IteratingSystem
{
    private static final Logger LOGGER = Log.getLogger(NetworkSync.class);
    private final NetworkMain network;

    public NetworkSync(NetworkMain network)
    {
        super(Family.all(UUIDComp.class).one(NetworkUpdates.class, NetworkAdd.class, NetworkRemove.class).get());
        this.network = network;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        // TODO: enable/disable the whole system instead
        if (!network.lobby().isHost() || !network.lobby().isLobbyOpen())
            return;

        UUID uuid = Components.UUID.get(entity).uuid();
        NetworkUpdates updates = Components.NETWORK_UPDATES.get(entity);
        NetworkAdd adds = Components.NETWORK_ADD.get(entity);
        NetworkRemove removes = Components.NETWORK_REMOVE.get(entity);
        Predicate<Class<? extends Component>> updatePredicate = null, addPredicate = null;
        Pair<Integer, ByteBuf> addsPair = null;
        int[] toRemove;

        User extraExclude = null;
        if (Components.MP_CONTROLLED.has(entity))
        {
            MPControlled mpControlled = Components.MP_CONTROLLED.get(entity);
            extraExclude = mpControlled.controller();
        }
        Set<User> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

        if (updates != null)
        {
            if (updates.components().isEmpty())
                return;
            updatePredicate = updates.test();
        }

        if (adds != null)
        {
            if (adds.components().isEmpty())
                return;
            addPredicate = adds.test();
        }

        if (updatePredicate != null && addPredicate != null)
        {
            Predicate<Class<? extends Component>> finalUpdatePredicate = updatePredicate;
            Predicate<Class<? extends Component>> finalAddPredicate = addPredicate;
            addsPair = NetworkSerialization.entityComponentsToBuffer(entity, (c) -> finalUpdatePredicate.test(c) || finalAddPredicate.test(c));
        } else if (updatePredicate != null)
        {
            addsPair = NetworkSerialization.entityComponentsToBuffer(entity, updatePredicate);
        } else if (addPredicate != null)
        {
            addsPair = NetworkSerialization.entityComponentsToBuffer(entity, addPredicate);
        }

        if (addsPair == null)
        {
            // Create with empty buffer
            addsPair = Pair.of(0, PooledByteBufAllocator.DEFAULT.heapBuffer(0, 0));
        }

        if (updates != null) updates.clear();
        if (adds != null) adds.clear();

        if (removes != null)
        {
            if (removes.components().isEmpty())
                return;

            toRemove = new int[removes.components().size()];
            int i = 0;
            for (Class<? extends Component> component : removes.components())
            {
                var entryOptional = Components.getComponentByClass(component);
                if (entryOptional.isPresent())
                {
                    toRemove[i] = entryOptional.get().networkID();
                    i++;
                } else
                {
                    LOGGER.warning("Unknown component for " + component);
                }
            }

            if (i != removes.components().size() - 1)
            {
                int[] newArr = new int[i];
                System.arraycopy(toRemove, 0, newArr, 0, i);
                toRemove = newArr;
            }

            removes.clear();
        } else
        {
            toRemove = new int[0];
        }

        network.connections().broadcastPacketExclude(new UpdateEntityComponents(uuid, addsPair.getFirst(), addsPair.getSecond(), toRemove), toExclude);
    }
}
