package steve6472.orbiter.ui.panel;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.moondust.view.PanelView;
import steve6472.moondust.view.property.BooleanProperty;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.Lobby;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.world.World;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class InGameMenu extends PanelView
{
    private static final Logger LOGGER = Log.getLogger(InGameMenu.class);

    public InGameMenu(Key key)
    {
        super(key);
    }

    @Override
    protected void createProperties()
    {
        BooleanProperty mainMenuVisible = findProperty("main_menu:visible");
        BooleanProperty disconnectVisible = findProperty("disconnect:visible");

        NetworkMain network = OrbiterApp.getInstance().getNetwork();
        if (network != null)
        {
            Lobby lobby = network.lobby();
            if (lobby == null || !lobby.isLobbyOpen() || (lobby.isLobbyOpen() && lobby.isHost()))
            {
                mainMenuVisible.set(true);
                disconnectVisible.set(false);
            } else if (lobby.isLobbyOpen() && !lobby.isHost())
            {
                mainMenuVisible.set(false);
                disconnectVisible.set(true);
            } else
            {
                LOGGER.severe("Unknown state: lobby=" + "exists" + ", lobby.isLobbyOpen()=" + lobby.isLobbyOpen() + ", lobby.isHost()=" + lobby.isHost());
            }
        }
    }

    @Override
    protected void createCommandListeners()
    {
        addCommandListener(Constants.key("close"), _ -> OrbiterApp.getInstance().masterRenderer().getWindow().closeWindow());
        addCommandListener(Constants.key("resume"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            orbiter.setMouseGrab(true);
        });
        addCommandListener(Constants.key("open_settings"), _ ->
        {
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.addPanel(Constants.UI.SETTINGS);
        });

        addCommandListener(Constants.key("main_menu"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            orbiter.clearWorld();
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.addPanel(Constants.UI.MAIN_MENU);

            NetworkMain network = OrbiterApp.getInstance().getNetwork();
            network.shutdown();
        });

        addCommandListener(Constants.key("open_lobby_menu"), _ ->
        {
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.addPanel(Settings.MULTIPLAYER_BACKEND.get() == Settings.MultiplayerBackend.DEDICATED ? Constants.UI.LOBBY_MENU_DEDICATED : Constants.UI.LOBBY_MENU_STEAM);
        });
    }
}
