package steve6472.orbiter.settings;

import steve6472.core.setting.BoolSetting;
import steve6472.core.setting.FloatSetting;
import steve6472.core.setting.SettingRegister;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Settings extends SettingRegister
{
    static { REGISTRY = Registries.SETTINGS; }

    public static final FloatSetting FOV = registerFloat("fov",90);
    public static final FloatSetting SENSITIVITY = registerFloat("sensitivity",0.3f);
    public static final BoolSetting PEER_BROADCAST_SINGLE_BUFFER = registerBool("peer_broadcast_single_buffer",true);
}
