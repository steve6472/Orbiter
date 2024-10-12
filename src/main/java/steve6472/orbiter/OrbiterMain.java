package steve6472.orbiter;

import org.joml.Matrix3f;
import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.setting.SettingsLoader;
import steve6472.volkaniums.Constants;
import steve6472.volkaniums.core.Volkaniums;
import steve6472.volkaniums.registry.RegistryCreators;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterMain
{
    private static final Logger LOGGER = Log.getLogger(OrbiterMain.class);

    public static boolean ENABLE_STEAM = true;

    public static boolean FAKE_P2P = false;
    public static boolean STEAM_TEST = false;
    public static boolean FAKE_PEER = false;

    public static void main(String[] args)
    {
        System.setProperty("joml.format", "false");
        System.setProperty("dominion.show-banner", "false");

        if (test())
            return;

        List<String> list = Arrays.asList(args);
        STEAM_TEST = list.contains("steamTest");
        FAKE_P2P = list.contains("fakeP2P");
        FAKE_PEER = list.contains("fakePeer");

        if (FAKE_PEER)
            FAKE_P2P = true;

        if (FAKE_P2P)
        {
            try
            {
                // Get the class object for 'com.codedisaster.steamworks.SteveNativeFix'
                Class<?> clazz = Class.forName("com.codedisaster.steamworks.SteveNativeFix");

                // Get the declared field 'FIX_STEAM_ID'
                Field field = clazz.getDeclaredField("FIX_STEAM_ID");

                // Make the field accessible (if it's private or protected)
                field.setAccessible(true);

                // Set the value of the field to true
                field.set(null, true); // null because FIX_STEAM_ID is static

                // Verify if the field was successfully set
                System.out.println("FIX_STEAM_ID is now: " + field.get(null));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
            {
                System.err.println("Wrong Steamworks4j");
                throw new RuntimeException(e);
            }
        }

        try
        {
            if (STEAM_TEST)
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

    private static boolean test()
    {
        return false;
    }
}