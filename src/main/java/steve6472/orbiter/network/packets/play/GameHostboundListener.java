package steve6472.orbiter.network.packets.play;

import com.badlogic.ashley.core.Entity;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class GameHostboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(GameHostboundListener.class);

    public void heartbeat()
    {
//        LOGGER.info("<3 from " + sender());
    }

    public void disconnect()
    {
        network().lobby().kickUser(sender(), "Disconnected");
        LOGGER.info("User " + sender() + " disconnected!");

        World world = OrbiterApp.getInstance().getClient().getWorld();
        if (world != null)
        {
            world.removeEntity(sender().uuid(), true);
        }
    }

    public void movePlayer(Vector3f position)
    {
        UUID uuid = sender().uuid();
        World world = OrbiterApp.getInstance().getClient().getWorld();
        Optional<Entity> entityByUUID = world.getEntityByUUID(uuid);
        entityByUUID.ifPresentOrElse(entity -> {

            entity.add(new Position(position.x, position.y, position.z));
            world.markModified(entity, Components.POSITION.componentClass());

        }, () -> {
            LOGGER.warning("Player entity was not spawned!");
        });
    }
}
