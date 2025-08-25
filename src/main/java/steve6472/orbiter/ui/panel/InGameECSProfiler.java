package steve6472.orbiter.ui.panel;

import steve6472.core.registry.Key;
import steve6472.moondust.view.PanelView;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.ui.MDUtil;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class InGameECSProfiler extends PanelView
{
    public InGameECSProfiler(Key key)
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
        addCommandListener(Constants.key("back"), _ ->
        {
            MDUtil.removePanel(Constants.UI.IN_GAME_ECS_PROFILER);
            MDUtil.addPanel(Constants.UI.IN_GAME_MENU);
        });
    }
}
