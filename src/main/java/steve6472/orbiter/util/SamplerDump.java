package steve6472.orbiter.util;

import org.jetbrains.annotations.NotNull;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.FlareConstants;
import steve6472.flare.assets.TextureSampler;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.OrbiterApp;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 9/25/2025
 * Project: Orbiter <br>
 */
public class SamplerDump
{
    private static final Logger LOGGER = Log.getLogger(SamplerDump.class);

    public static void dumpSamplers()
    {
        OrbiterApp instance = OrbiterApp.getInstance();

        LOGGER.info("Dumping samplers");
        File file = getFile("/sampler");

        LOGGER.info("Generating new samplers");
        for (Key key : FlareRegistries.SAMPLER.keys())
        {
            LOGGER.info("Dumping " + key);
            File dumpFile = new File(file, key.namespace() + "-" + key.id().replaceAll("/", "__") + ".png");
            TextureSampler textureSampler = FlareRegistries.SAMPLER.get(key);
            textureSampler.texture.saveTextureAsPNG(instance.device(), instance.masterRenderer().getCommands(), instance.masterRenderer().getGraphicsQueue(), dumpFile);
        }
        LOGGER.info("Finished dumping samplers");
    }

    private static @NotNull File getFile(@SuppressWarnings("SameParameterValue") String suffix)
    {
        File file = new File(FlareConstants.FLARE_DEBUG_FOLDER, "dumped" + suffix);
        if (file.exists())
        {
            LOGGER.info("Removing old textures");
            File[] files = file.listFiles();
            if (files != null)
            {
                for (File listFile : files)
                {
                    if (!listFile.delete())
                    {
                        LOGGER.severe("Could not delete " + listFile.getAbsolutePath());
                    }
                }
            }
        } else
        {
            if (!file.mkdirs())
            {
                LOGGER.severe("Could not create " + file.getAbsolutePath());
            }
        }
        return file;
    }
}
