package steve6472.orbiter;

import steve6472.core.SteveCore;
import steve6472.core.log.Log;
import steve6472.core.setting.SettingsLoader;
import steve6472.flare.FlareConstants;
import steve6472.orbiter.debug.PrimitiveLineWindow;
import steve6472.flare.core.Flare;
import steve6472.flare.registry.RegistryCreators;
import steve6472.flare.registry.FlareRegistries;

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

    public static boolean ENABLE_STEAM = false;

    public static boolean FAKE_P2P = false;
    public static boolean STEAM_TEST = false;
    public static boolean FAKE_PEER = false;

    public static void main(String[] args)
    {
        System.setProperty("joml.format", "false");

        System.setProperty("dominion.world.size", "LARGE");
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
                PrimitiveLineWindow lineWindow = new PrimitiveLineWindow();

                OrbiterApp orbiterApp = new OrbiterApp();
                SteveCore.DEFAULT_KEY_NAMESPACE = orbiterApp.defaultNamespace();
                orbiterApp.preInit();
                RegistryCreators.init(FlareRegistries.VISUAL_SETTINGS);
                orbiterApp.initRegistries();
                RegistryCreators.createContents();
                SettingsLoader.loadFromJsonFile(FlareRegistries.VISUAL_SETTINGS, FlareConstants.VISUAL_SETTINGS_FILE);
                orbiterApp.loadSettings();
                orbiterApp.postInit();

                lineWindow.run();

                while (!lineWindow.shouldClose())
                {
                    orbiterApp.render(null, null);
                    lineWindow.runFrame();
                    Thread.sleep(16);
                }

                lineWindow.cleanup();

            } else
            {
                Flare.start(new OrbiterApp());
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static boolean test()
    {
        return false;
    }
}