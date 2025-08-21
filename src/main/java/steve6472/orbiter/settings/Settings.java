package steve6472.orbiter.settings;

import steve6472.core.registry.StringValue;
import steve6472.core.setting.*;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Settings extends SettingRegister
{
    static {
        REGISTRY = Registries.SETTINGS;
        NAMESPACE = Constants.NAMESPACE;
    }

    public static final FloatSetting FOV = registerFloat("fov", 90);
    public static final FloatSetting SENSITIVITY = registerFloat("sensitivity", 0.15f);
    public static final IntSetting UI_SCALE = registerInt("ui_scale", 2);
    public static final EnumSetting<MultiplayerBackend> MULTIPLAYER_BACKEND = registerEnum("multiplayer_beckend", MultiplayerBackend.DEDICATED);
    public static final BoolSetting TRACK_BANDWIDTH = registerBool("track_bandwidth", false);

    public enum MultiplayerBackend implements StringValue
    {
        STEAM, DEDICATED;

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
