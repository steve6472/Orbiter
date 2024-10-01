package steve6472.orbiter;

import steve6472.core.registry.ObjectRegistry;
import steve6472.core.setting.Setting;
import steve6472.orbiter.settings.Settings;
import steve6472.volkaniums.registry.RegistryCreators;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Registries extends RegistryCreators
{
    public static final ObjectRegistry<Setting<?, ?>> SETTINGS = createObjectRegistry("setting", () -> Settings.FOV);
}
