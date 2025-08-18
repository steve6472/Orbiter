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
public class MainMenu extends PanelView
{
    public MainMenu(Key key)
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

        addCommandListener(Constants.key("enter_world"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            World world = new World();
            orbiter.setCurrentWorld(world);
            orbiter.setMouseGrab(true);
            MDUtil.removePanel(Constants.key("panel/main_menu"));
        });

        addCommandListener(Constants.key("open_settings"), _ ->
        {
            MDUtil.removePanel(Constants.UI.MAIN_MENU);
            MDUtil.addPanel(Constants.UI.SETTINGS);
        });
    }
}
