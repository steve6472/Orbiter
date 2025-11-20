package steve6472.dfu;

/**
 * Created by steve6472
 * Date: 11/17/2025
 * Project: Orbiter <br>
 */
public class ModelVersionMap
{
    public static int getVersionFromModelString(String version)
    {
        return switch (version)
        {
            case "5.0" -> 1;
            case "4.10" -> 2;

            default -> throw new IllegalArgumentException("Unknown version: " + version);
        };
    }

    public static String modelStringFromVersion(int version)
    {
        // remove the minor version
        version /= 10;
        return switch (version)
        {
            case 1 -> "5.0";
            case 2 -> "4.10";
            default -> throw new IllegalArgumentException("Unknown version: " + version);
        };
    }
}
