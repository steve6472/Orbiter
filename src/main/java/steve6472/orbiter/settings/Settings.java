package steve6472.orbiter.settings;

import steve6472.core.setting.FloatSetting;
import steve6472.core.setting.IntSetting;
import steve6472.core.setting.SettingRegister;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

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
}
