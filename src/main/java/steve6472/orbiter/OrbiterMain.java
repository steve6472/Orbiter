package steve6472.orbiter;

import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.setting.SettingsLoader;
import steve6472.volkaniums.Constants;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.core.Volkaniums;
import steve6472.volkaniums.registry.RegistryCreators;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterMain
{
    private static final Logger LOGGER = Log.getLogger(OrbiterMain.class);
    public static boolean STEAM_TEST = false;

    public static void main(String[] args)
    {
        System.setProperty("joml.format", "false");
        System.setProperty("dominion.show-banner", "false");

        boolean steamTest = false;

        if (args.length > 0)
            steamTest = "steamTest".equals(args[0]);

        STEAM_TEST = steamTest;

        try
        {
            if (steamTest)
            {
                OrbiterApp orbiterApp = new OrbiterApp();
                SteveCore.DEFAULT_KEY_NAMESPACE = orbiterApp.defaultNamespace();
                orbiterApp.preInit();
                RegistryCreators.init(VolkaniumsRegistries.VISUAL_SETTINGS);
                orbiterApp.initRegistries();
                RegistryCreators.createContents();
                SettingsLoader.loadFromJsonFile(VolkaniumsRegistries.VISUAL_SETTINGS, Constants.VISUAL_SETTINGS_FILE);
                orbiterApp.loadSettings();
                orbiterApp.postInit();

                while (true)
                {
                    orbiterApp.render(null, null);
                    Thread.sleep(16);
                }
            } else
            {
                Volkaniums.start(new OrbiterApp());
            }
        } catch (Exception ex)
        {
            Log.exceptionSevere(LOGGER, ex);
            for (StackTraceElement stackTraceElement : ex.getStackTrace())
            {
                LOGGER.severe("\t" + stackTraceElement.toString());
            }
        }
    }
}