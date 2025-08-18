package steve6472.orbiter.ui;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.moondust.MoonDust;
import steve6472.moondust.widget.Panel;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class MDUtil
{
    private static final Logger LOGGER = Log.getLogger(MDUtil.class);

    public static boolean isPanelOpen(Key key)
    {
        for (Panel panel : MoonDust.getInstance().getPanels())
        {
            if (panel.getKey().equals(key))
                return true;
        }

        return false;
    }

    public static Optional<Panel> getPanel(Key key)
    {
        for (Panel panel : MoonDust.getInstance().getPanels())
        {
            if (panel.getKey().equals(key))
                return Optional.of(panel);
        }

        return Optional.empty();
    }

    public static Panel addPanel(Key key)
    {
        Panel panel = Panel.create(key);
        panel.clearFocus();
        MoonDust.getInstance().addPanel(panel);
        return panel;
    }

    public static void removePanel(Key key)
    {
        Optional<Panel> first = MoonDust
            .getInstance()
            .getPanels()
            .stream()
            .filter(panel -> panel.getKey().equals(key))
            .findFirst();
        first.ifPresentOrElse(panel -> MoonDust.getInstance().removePanel(panel), () -> LOGGER.warning("Panel '%s' not found, can not remove".formatted(key)));
    }
}
