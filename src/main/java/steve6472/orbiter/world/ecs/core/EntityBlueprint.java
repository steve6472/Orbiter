package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.UUIDComp;

import java.util.*;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class EntityBlueprint implements Keyable
{
    private final List<Blueprint<?>> blueprints;
    private final Key key;

    public EntityBlueprint(Key key, List<Blueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
    }

    public List<Component> createEntityComponents(UUID uuid)
    {
        List<Component> components = new ArrayList<>(blueprints.size());
        blueprints.forEach(blueprint -> components.addAll(blueprint.createComponents()));
        components.add(new UUIDComp(uuid));
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

        Flare.getModuleManager().loadParts(OrbiterParts.ENTITY_BLUEPRINT, Registries.COMPONENT_BLUEPRINT.valueMapCodec(), (map, key) -> {

            List<Blueprint<?>> objects = new ArrayList<>(map.size());

            for (Object value : map.values())
            {
                if (!(value instanceof Blueprint<?> blueprint))
                {
                    throw new RuntimeException("Not instance of blueprint!");
                }

                objects.add(blueprint);
            }

            EntityBlueprint blueprint = new EntityBlueprint(key, objects);
            blueprints.put(key, blueprint);
        });
        blueprints.values().forEach(Registries.ENTITY_BLUEPRINT::register);
    }
}
