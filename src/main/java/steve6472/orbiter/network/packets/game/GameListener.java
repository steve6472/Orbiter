package steve6472.orbiter.network.packets.game;

import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.orbiter.Convert;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Tag;

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
}
