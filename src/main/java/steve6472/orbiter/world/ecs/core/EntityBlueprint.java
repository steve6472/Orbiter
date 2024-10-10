package steve6472.orbiter.world.ecs.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.orbiter.Registries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class EntityBlueprint implements Keyable
{
    private static final Logger LOGGER = Log.getLogger(EntityBlueprint.class);
    private static final String BLUEPRINT_FOLDER = "resources" + File.separator + "entity_blueprint";

    private final List<Blueprint<?>> blueprints;
    private final Key key;

    public EntityBlueprint(Key key, List<Blueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
    }

    public Set<Object> createComponents()
    {
        Set<Object> components = new HashSet<>(blueprints.size());
        blueprints.forEach(blueprint -> components.addAll(blueprint.createComponents()));
        return components;
    }

    @Override
    public Key key()
    {
        return key;
    }

    public static EntityBlueprint load()
    {
        loadBlueprint(new File(BLUEPRINT_FOLDER));
        return new EntityBlueprint(Key.defaultNamespace("null"), List.of());
    }

    private static void loadBlueprint(File file)
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files == null)
                return;

            for (File listFile : files)
            {
                loadBlueprint(listFile);
            }
            return;
        }

        JsonElement jsonElement;

        try
        {
            jsonElement = JsonParser.parseReader(new FileReader(file));
        } catch (FileNotFoundException e)
        {
            LOGGER.severe("File not found for " + file.getAbsolutePath());
            return;
        }

        var decode = Registries.BLUEPRINT.valueMapCodec().decode(JsonOps.INSTANCE, jsonElement);

        if (decode.isError())
        {
            LOGGER.severe("Error when loading EntityBlueprint from " + file.getAbsolutePath());
            decode.error().ifPresent(err -> LOGGER.severe(err.message()));
            return;
        }

        Pair<Map<BlueprintEntry<?>, Object>, JsonElement> decoded = decode.getOrThrow();
        Map<BlueprintEntry<?>, Object> decodedMap = decoded.getFirst();

        List<Blueprint<?>> objects = new ArrayList<>(decodedMap.size());

        for (Object value : decodedMap.values())
        {
            if (!(value instanceof Blueprint<?> blueprint))
            {
                throw new RuntimeException("Not instance of blueprint!");
            }

            objects.add(blueprint);
        }

        String id = file.getAbsolutePath();
        id = id.substring(id.indexOf(BLUEPRINT_FOLDER) + BLUEPRINT_FOLDER.length());
        id = id.substring(1, id.lastIndexOf("."));
        
        Key key = Key.defaultNamespace(id);
        LOGGER.finest("Created entity blueprint " + key);
        EntityBlueprint blueprint = new EntityBlueprint(key, objects);
        Registries.ENTITY_BLUEPRINT.register(blueprint);
    }
}
