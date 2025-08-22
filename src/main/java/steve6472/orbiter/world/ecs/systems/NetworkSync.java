package steve6472.orbiter.world.ecs.systems;

import steve6472.orbiter.network.api.NetworkMain;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class NetworkSync
{
    private final NetworkMain network;

    public NetworkSync(NetworkMain network)
    {
        this.network = network;
    }
/*
    @Override
    public void tick(Dominion dominion, World world)
    {
//        if (!network.isHost() || network.disabled())
//            return;

        // TODO: disable if no peers exist

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkUpdates.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkUpdates updates = entityComps.comp2();

            if (updates.components().isEmpty())
                continue;

            Entity entity = entityComps.entity();

//            if (OrbiterApp.getInstance().getSteam().isHost() && entity.has(Tag.ClientHandled.class))
//                continue;

            User extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                extraExclude = mpControlled.controller();
            }

            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(entity, updates.test());
            if (serialized.getFirst() == 0)
                continue;

            updates.clear();

            Set<User> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            network.connections().broadcastPacketExclude(new UpdateEntityComponents(uuid, serialized.getFirst(), serialized.getSecond()), toExclude);
        }

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkAdd.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkAdd updates = entityComps.comp2();

            if (updates.components().isEmpty())
                continue;

            Entity entity = entityComps.entity();

//            if (OrbiterApp.getInstance().getSteam().isHost() && entity.has(Tag.ClientHandled.class))
//                continue;

            User extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                extraExclude = mpControlled.controller();
            }

            Pair<Integer, ByteBuf> serialized = NetworkSerialization.entityComponentsToBuffer(entity, updates.test());
            if (serialized.getFirst() == 0)
                continue;

            updates.clear();

            Set<User> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            network.connections().broadcastPacketExclude(new AddEntityComponents(uuid, serialized.getFirst(), serialized.getSecond()), toExclude);
        }

        for (var entityComps : dominion.findEntitiesWith(UUID.class, NetworkRemove.class))
        {
            UUID uuid = entityComps.comp1();
            NetworkRemove updates = entityComps.comp2();

            Entity entity = entityComps.entity();

//            if (OrbiterApp.getInstance().getSteam().isHost() && entity.has(Tag.ClientHandled.class))
//                continue;

            User extraExclude = null;
            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                extraExclude = mpControlled.controller();
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

            Set<User> toExclude = extraExclude == null ? Set.of() : Set.of(extraExclude);

            network.connections().broadcastPacketExclude(new RemoveEntityComponents(uuid, componentKeys.toString()), toExclude);
        }
    }*/
}
