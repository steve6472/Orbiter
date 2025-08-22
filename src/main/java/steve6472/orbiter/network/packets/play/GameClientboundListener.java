package steve6472.orbiter.network.packets.play;

import com.badlogic.ashley.core.Component;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.settings.VisualSettings;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.world.World;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/20/2025
 * Project: Orbiter <br>
 */
public class GameClientboundListener extends OrbiterPacketListener
{
    private static final Logger LOGGER = Log.getLogger(GameClientboundListener.class);

    public void heartbeat()
    {
//        LOGGER.info("<3 from " + sender());
    }

    public void enterWorld()
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        World world = new World();
        orbiter.setCurrentWorld(world);
        orbiter.setMouseGrab(true);
        MDUtil.removePanel(Constants.UI.MAIN_MENU);
        MDUtil.removePanel(Constants.UI.SETTINGS);
        MDUtil.removePanel(Constants.UI.LOBBY_MENU_DEDICATED);
    }

    public void createEntity(UUID uuid, Key entityType)
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        World world = orbiter.getClient().getWorld();
        world.addEntity(Registries.ENTITY_BLUEPRINT.get(entityType), uuid);
    }

    public void createCustomEntity(UUID uuid, List<Component> components)
    {/*
        OrbiterApp orbiter = OrbiterApp.getInstance();
        World world = orbiter.getClient().getWorld();
        Entity entity = world.createEntity(components);

        if (Components.TAG_PHYSICS.has(entity))
        {
            Collision collision = Components.COLLISION.get(entity);
            if (collision == null)
            {
                LOGGER.severe("Physics entity has no collision!");
                return;
            }

            PhysicsRigidBody body = new PhysicsRigidBody(collision.shape());
            world.bodyMap().put(uuid, body);
            world.physics().add(body);
            var position = Components.POSITION.get(entity);
            if (position != null)
                body.setPhysicsLocation(Convert.jomlToPhys(position.toVec3f()));
        }*/
    }

    public void kickUser(User toKick, String reason)
    {
        if (toKick.username().equals(VisualSettings.USERNAME.get()))
        {
            network().shutdown();

            OrbiterApp orbiter = OrbiterApp.getInstance();
            orbiter.clearWorld();
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.removePanel(Constants.UI.SETTINGS);
            MDUtil.removePanel(Constants.UI.LOBBY_MENU_DEDICATED);
            MDUtil.addPanel(Constants.UI.MAIN_MENU);
        }
        // TODO: do.. something
        LOGGER.info("User " + sender() + " kicked! Reason: " + reason);
    }
}
