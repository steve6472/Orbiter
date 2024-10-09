package steve6472.orbiter.network.packets.game;

import com.codedisaster.steamworks.SteamID;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.SteamPeer;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Tag;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class GameListener extends OrbiterPacketListener
{
    public static final Logger LOGGER = Log.getLogger(GameListener.class);

    private final World world;

    public GameListener(SteamMain steamMain, World world)
    {
        super(steamMain);
        this.world = world;
    }

    public void teleport(Vector3f destination)
    {
        var entityList = world.ecs().findEntitiesWith(MPControlled.class, UUID.class, Tag.Physics.class);

        for (var entityData : entityList)
        {
            MPControlled mpControlled = entityData.comp1();
            if (!mpControlled.controller().equals(sender()))
                continue;

            UUID uuid = entityData.comp2();

            world.bodyMap.get(uuid).setPhysicsLocation(Convert.jomlToPhys(destination));
        }
    }

    public void acceptedPeerConnection()
    {
        if (!OrbiterMain.FAKE_P2P)
            LOGGER.info(steamMain.friendNames.getUserName(sender()) + " accepted peer connection!");
        else
            LOGGER.info("Fake P2P accepted");
        connections.broadcastMessageExclude(new SpawnPlayerCharacter(sender()), peer());
        sendExistingData();
        world.spawnDebugPlayer(sender());
    }

    private void sendExistingData()
    {
        connections.sendMessage(peer(), new SpawnPlayerCharacter(steamMain.userID));

        for (SteamPeer listPeer : connections.listPeers())
        {
            if (listPeer.equals(peer()))
                continue;

            connections.sendMessage(peer(), new SpawnPlayerCharacter(listPeer.steamID()));
        }
    }

    public void spawnPlayer(SteamID player)
    {
        world.spawnDebugPlayer(player);
    }

    public void disconnectPlayer(SteamID disconnectedPlayer)
    {
        world
            .ecs()
            .findEntitiesWith(MPControlled.class, UUID.class)
            .stream()
            .filter(e -> e.comp1().controller().equals(disconnectedPlayer))
            .forEach(e -> world.removeEntity(e.comp2()));
    }

    public void updateEntity(UUID uuid, List<Object> components)
    {
        world.getEntityByUUID(uuid).ifPresentOrElse(entity -> {
            for (Object component : components)
            {
                entity.removeType(component.getClass());
                entity.add(component);
            }
        }, () -> {
            components.add(uuid);
            world.ecs().createEntity(components.toArray());
        });
    }
}
