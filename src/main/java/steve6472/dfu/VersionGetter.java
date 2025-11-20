package steve6472.dfu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 11/17/2025
 * Project: Orbiter <br>
 */
public class VersionGetter
{
    public static Optional<String> getString(JsonObject root, String path)
    {
        if (root == null)
            return Optional.empty();

        if (!path.contains("."))
        {
            if (root.has(path) && !root.get(path).isJsonNull())
                return Optional.of(root.get(path).getAsString());

            return Optional.empty();
        }

        String[] split = path.split("\\.");
        String propertyName = split[split.length - 1];
        String[] objects2Traverse = new String[split.length - 1];

        System.arraycopy(split, 0, objects2Traverse, 0, split.length - 1);

        JsonObject currentLevel = root;

        for (String objectName : objects2Traverse)
        {
            if (currentLevel.has(objectName) && !currentLevel.get(objectName).isJsonNull())
            {
                currentLevel = currentLevel.getAsJsonObject(objectName);
            } else
            {
                return Optional.empty();
            }
        }

        JsonElement jsonElement = currentLevel.get(propertyName);

        if (currentLevel.has(propertyName) && !jsonElement.isJsonNull())
            return Optional.of(jsonElement.getAsString());

        return Optional.empty();
    }
}
