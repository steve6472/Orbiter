package steve6472.orbiter;

import steve6472.core.log.Log;
import steve6472.volkaniums.core.Volkaniums;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterMain
{
    private static final Logger LOGGER = Log.getLogger(OrbiterMain.class);

    public static void main(String[] args)
    {
        System.setProperty("joml.format", "false");
        System.setProperty("dominion.show-banner", "false");
        try
        {
            Volkaniums.start(new OrbiterApp());
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