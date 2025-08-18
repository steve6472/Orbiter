package steve6472.orbiter.ui;

import steve6472.core.registry.Key;
import steve6472.moondust.view.PanelView;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;

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
    }
}
