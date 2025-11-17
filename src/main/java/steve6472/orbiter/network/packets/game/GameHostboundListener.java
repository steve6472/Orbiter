package steve6472.orbiter.network.packets.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.packets.game.clientbound.CreateCustomEntity;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.List;
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
        world.updateClientData(uuid, entity ->
        {
            Position posComp = Components.POSITION.get(entity);
            if (posComp != null)
                posComp.set(position.x, position.y, position.z);
            world.markModified(entity, Components.POSITION.componentClass());
        });
    }

    public void confirmEnterWorld()
    {
        World world = OrbiterApp.getInstance().getClient().getWorld();

        for (Entity entity : world.ecsEngine().getEntities())
        {
            // Replace host entity
            if (entity == OrbiterApp.getInstance().getClient().player().ecsEntity())
            {
                Entity fakeEntity = new Entity();
                List<Component> components = Registries.ENTITY_BLUEPRINT.get(Constants.key("mp_player")).createEntityComponents(OrbiterApp.getInstance().getClient().getClientUUID());
                for (Component component : components)
                {
                    fakeEntity.add(component);
                }
                // Add host hat
                fakeEntity.add(new IndexModel(FlareRegistries.STATIC_MODEL.get(Constants.key("blockbench/static/player_capsule_host"))));
                // Copy at least position
                Position position = Components.POSITION.get(entity);
                if (position != null)
                    fakeEntity.add(position);

                connections().sendPacket(sender(), new CreateCustomEntity(fakeEntity));
            } else
            {
                connections().sendPacket(sender(), new CreateCustomEntity(entity));
            }
        }
    }
}
