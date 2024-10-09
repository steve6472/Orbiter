package steve6472.orbiter.world.ecs.systems;

import com.codedisaster.steamworks.SteamID;
import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.engine.IntEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.Peer;
import steve6472.orbiter.network.packets.game.UpdateEntityComponents;
import steve6472.orbiter.network.test.FakeP2PConstants;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.SteamPeer;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.core.Component;
import steve6472.orbiter.world.ecs.core.ComponentSystem;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class NetworkSync implements ComponentSystem
{
    private static final int INITIAL_BYTES = 128;

    private final PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
    private final SteamMain steam;

    public NetworkSync(SteamMain steam)
    {
        this.steam = steam;
    }

    @Override
    public void tick(Dominion dominion, World world)
    {
        var found = dominion.findEntitiesWith(UUID.class);

        SteamPeer clientExclude = new SteamPeer(steam.userID);

        if (!steam.isHost())
            return;

        for (var entityComps : found)
        {
            UUID uuid = entityComps.comp();
            Entity entity = entityComps.entity();

            SteamPeer extraExclude = null;

            if (entity.has(MPControlled.class))
            {
                MPControlled mpControlled = entity.get(MPControlled.class);
                SteamID controller = mpControlled.controller();
                extraExclude = new SteamPeer(controller);
            }

            Object[] componentArray = ((IntEntity) entity).getComponentArray();

            ByteBuf buffer = allocator.buffer(INITIAL_BYTES + componentArray.length * 16);
            int encoded = 0;

            for (Object component : componentArray)
            {
                // Never changes
                if (component.getClass().equals(UUID.class))
                    continue;

                for (Component<?> componentType : Registries.COMPONENT.getMap().values())
                {
                    //noinspection unchecked
                    BufferCodec<ByteBuf, Object> networkCodec = (BufferCodec<ByteBuf, Object>) componentType.getNetworkCodec();
                    if (networkCodec == null)
                        continue;

                    if (componentType.componentClass().equals(component.getClass()))
                    {
                        BufferCodecs.KEY.encode(buffer, componentType.key());
                        networkCodec.encode(buffer, component);
                        encoded++;
                    }
                }
            }

            Set<SteamPeer> toExclude = extraExclude == null ? Set.of(clientExclude) : Set.of(extraExclude, clientExclude);

            steam.connections.broadcastMessageExclude(new UpdateEntityComponents(uuid, encoded, buffer), toExclude);
            buffer.release();
        }
    }
}
