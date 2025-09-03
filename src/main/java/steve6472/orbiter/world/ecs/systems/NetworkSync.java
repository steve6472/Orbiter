package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.core.log.Log;
import steve6472.orbiter.network.api.ConnectedUser;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.packets.game.clientbound.UpdateEntityComponents;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.*;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class NetworkSync extends IteratingProfiledSystem
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
        UUID uuid = Components.UUID.get(entity).uuid();
        NetworkUpdates updates = Components.NETWORK_UPDATES.get(entity);
        NetworkAdd adds = Components.NETWORK_ADD.get(entity);
        NetworkRemove removes = Components.NETWORK_REMOVE.get(entity);
        Predicate<Class<? extends Component>> updatePredicate = null, addPredicate = null;
        List<Component> components = new ArrayList<>();
        int[] toRemove;

        if (Components.MP_CONTROLLED.has(entity) && ((adds != null && !adds.components().contains(MPControlled.class)) && (removes != null && !removes.components().contains(MPControlled.class))))
        {
            return;
        }

        if (updates != null)
            updatePredicate = updates.test();

        if (adds != null)
            addPredicate = adds.test();


        if (updatePredicate != null && addPredicate != null)
        {
            Predicate<Class<? extends Component>> finalUpdatePredicate = updatePredicate;
            Predicate<Class<? extends Component>> finalAddPredicate = addPredicate;
            components.addAll(NetworkSerialization.selectComponents(entity, (c) -> finalUpdatePredicate.test(c) || finalAddPredicate.test(c)));
        } else if (updatePredicate != null)
        {
            components.addAll(NetworkSerialization.selectComponents(entity, updatePredicate));
        } else if (addPredicate != null)
        {
            components.addAll(NetworkSerialization.selectComponents(entity, addPredicate));
        }

        if (updates != null) updates.clear();
        if (adds != null) adds.clear();

        if (removes != null && !removes.components().isEmpty())
        {
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

        if (!components.isEmpty() || toRemove.length != 0)
        {
            UpdateEntityComponents packet = new UpdateEntityComponents(uuid, components, toRemove);
            for (ConnectedUser connectedUser : network.lobby().getConnectedUsers())
            {
                // Otherwise we'd be sending the players position back to them
                // Player stuff may have to be handled in a different way
                if (connectedUser.user().uuid().equals(uuid))
                    continue;
                network.connections().sendPacket(connectedUser.user(), packet);
            }
        }
    }
}
