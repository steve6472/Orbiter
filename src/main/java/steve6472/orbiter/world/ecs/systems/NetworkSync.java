package steve6472.orbiter.world.ecs.systems;

import com.codedisaster.steamworks.SteamID;
import com.mojang.datafixers.util.Pair;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import io.netty.buffer.ByteBuf;
import steve6472.orbiter.network.packets.game.AddEntityComponents;
import steve6472.orbiter.network.packets.game.RemoveEntityComponents;
import steve6472.orbiter.network.packets.game.UpdateEntityComponents;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.SteamPeer;
import steve6472.orbiter.world.NetworkSerialization;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.NetworkAdd;
import steve6472.orbiter.world.ecs.components.NetworkRemove;
import steve6472.orbiter.world.ecs.components.NetworkUpdates;
import steve6472.orbiter.world.ecs.core.ComponentSystem;

import java.util.Set;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class NetworkSync implements ComponentSystem
{
    private final SteamMain steam;

    public NetworkSync(SteamMain steam)
    {
        this.steam = steam;
    }

    @Override
    public void tick(Dominion dominion, World world)
    {
        if (!steam.isHost())
            return;

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkUpdates.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkUpdates updates = entityComps.comp2();

            if (updates.components().isEmpty())
                continue;

            Entity entity = entityComps.entity();

            SteamPeer extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                SteamID controller = mpControlled.controller();
                extraExclude = new SteamPeer(controller);
            }

            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(entity, updates.test());
            if (serialized.getFirst() == 0)
                continue;

            updates.clear();

            Set<SteamPeer> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            steam.connections.broadcastMessageExclude(new UpdateEntityComponents(uuid, serialized.getFirst(), serialized.getSecond()), toExclude);
        }

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkAdd.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkAdd updates = entityComps.comp2();

            if (updates.components().isEmpty())
                continue;

            Entity entity = entityComps.entity();

            SteamPeer extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                SteamID controller = mpControlled.controller();
                extraExclude = new SteamPeer(controller);
            }

            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(entity, updates.test());
            if (serialized.getFirst() == 0)
                continue;

            updates.clear();

            Set<SteamPeer> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            steam.connections.broadcastMessageExclude(new AddEntityComponents(uuid, serialized.getFirst(), serialized.getSecond()), toExclude);
        }

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkRemove.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkRemove updates = entityComps.comp2();

            Entity entity = entityComps.entity();

            SteamPeer extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                SteamID controller = mpControlled.controller();
                extraExclude = new SteamPeer(controller);
            }

            StringBuilder componentKeys = new StringBuilder();

            for (Class<?> component : updates.components())
            {
                Components.getComponentByClass(component).ifPresent(c ->
                {
                    if (c.getNetworkCodec() != null)
                    {
                        componentKeys.append(c.key().toString());
                        componentKeys.append(";");
                    }
                });
            }
            componentKeys.setLength(componentKeys.length() - 1);

            updates.clear();

            Set<SteamPeer> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            steam.connections.broadcastMessageExclude(new RemoveEntityComponents(uuid, componentKeys.toString()), toExclude);
        }
    }
}
