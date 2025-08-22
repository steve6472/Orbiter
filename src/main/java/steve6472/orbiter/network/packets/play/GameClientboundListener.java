package steve6472.orbiter.network.packets.play;

import steve6472.core.log.Log;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.OrbiterPacketListener;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.world.World;

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
        MDUtil.removePanel(Constants.key("panel/main_menu"));
        MDUtil.removePanel(Constants.key("panel/settings"));
        MDUtil.removePanel(Constants.key("panel/lobby_dedicated/menu"));
    }
}
