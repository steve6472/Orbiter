package steve6472.orbiter;

import steve6472.core.registry.Key;
import steve6472.flare.FlareConstants;

import java.io.File;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Constants
{
    /// The main namespace of Orbiter
    public static final String NAMESPACE = "orbiter";

    public static final File SETTINGS = new File("settings/orbiter_settings.json");

    public static final float TICKS_IN_SECOND = 60f;

    public static final int PLAYER_MAGIC_CONSTANT = 42;

    /// Orbiter-generated resources
    public static final File GENERATED_ORBITER = new File(FlareConstants.GENERATED_FOLDER, NAMESPACE);

    public static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }
}
