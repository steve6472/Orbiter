package steve6472.orbiter.world.ecs.core;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.flare.module.Module;
import steve6472.flare.util.ResourceCrawl;
import steve6472.orbiter.Registries;

import java.io.File;
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

    private final List<Blueprint<?>> blueprints;
    private final Key key;

    public EntityBlueprint(Key key, List<Blueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
    }

    public List<Object> createComponents()
    {
        List<Object> components = new ArrayList<>(blueprints.size());
        blueprints.forEach(blueprint -> components.addAll(blueprint.createComponents()));
        return components;
    }

    @Override
    public Key key()
    {
        return key;
    }

    public static void load()
    {
        Map<Key, EntityBlueprint> blueprints = new LinkedHashMap<>();

        for (Module module : Flare.getModuleManager().getModules())
        {
            module.iterateNamespaces((folder, namespace) ->
            {
                File file = new File(folder, "entity_blueprint");
                ResourceCrawl.crawlAndLoadJsonCodec(file, Registries.BLUEPRINT.valueMapCodec(), (map, id) ->
                {
                    List<Blueprint<?>> objects = new ArrayList<>(map.size());

                    for (Object value : map.values())
                    {
                        if (!(value instanceof Blueprint<?> blueprint))
                        {
                            throw new RuntimeException("Not instance of blueprint!");
                        }

                        objects.add(blueprint);
                    }

                    Key key = Key.withNamespace(namespace, id);
                    EntityBlueprint blueprint = new EntityBlueprint(key, objects);
                    LOGGER.finest("Loaded entity blueprint " + key + " from " + module.name());
                    blueprints.put(key, blueprint);
                });
            });
        }
        blueprints.values().forEach(Registries.ENTITY_BLUEPRINT::register);
    }
}
