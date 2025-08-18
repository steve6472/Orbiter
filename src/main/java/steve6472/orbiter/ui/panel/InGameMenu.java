package steve6472.orbiter.ui.panel;

import steve6472.core.registry.Key;
import steve6472.moondust.view.PanelView;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.world.World;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class InGameMenu extends PanelView
{
    public InGameMenu(Key key)
    {
        super(key);
    }

    @Override
    protected void createProperties()
    {

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
        addCommandListener(Constants.key("main_menu"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            orbiter.clearWorld();
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.addPanel(Constants.UI.MAIN_MENU);
        });
        addCommandListener(Constants.key("open_settings"), _ ->
        {
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            MDUtil.addPanel(Constants.UI.SETTINGS);
        });
    }
}
