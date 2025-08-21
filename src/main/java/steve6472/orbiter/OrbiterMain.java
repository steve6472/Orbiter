package steve6472.orbiter;

import steve6472.core.log.Log;
import steve6472.core.util.JarExport;
import steve6472.flare.util.PackerUtil;
import steve6472.moondust.MoonDust;
import steve6472.flare.core.Flare;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        PackerUtil.PADDING = 0;
        PackerUtil.DUPLICATE_BORDER = false;

        System.setProperty("joml.format", "false");

        System.setProperty("dominion.world.size", "LARGE");
        System.setProperty("dominion.show-banner", "false");

        File parentFile = Constants.BULLET_NATIVE.getParentFile();
        if (!parentFile.exists())
        {
            if (!parentFile.mkdirs())
            {
                LOGGER.severe("Failed to create folder for export " + parentFile);
                return;
            }
        }
        JarExport.exportFile("native/windows/x86_64/bulletjme.dll", Constants.BULLET_NATIVE);

        if (test())
            return;

        MoonDust.getInstance().init();
        Flare.start(new OrbiterApp());
    }

    private static boolean test()
    {
        return false;
    }
}