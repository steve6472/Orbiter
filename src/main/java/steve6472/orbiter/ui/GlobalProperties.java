package steve6472.orbiter.ui;

import steve6472.moondust.view.property.BooleanProperty;
import steve6472.moondust.view.property.NumberProperty;
import steve6472.orbiter.player.PCPlayer;

/**
 * Created by steve6472
 * Date: 8/23/2025
 * Project: Orbiter <br>
 */
public class GlobalProperties
{
    public static final BooleanProperty LOBBY_OPEN = new BooleanProperty();
    public static final BooleanProperty IS_LOBBY_HOST = new BooleanProperty();

    // DEBUG
    public static final NumberProperty EYE_EIGHT = new NumberProperty(PCPlayer.EYE_HEIGHT);
}
