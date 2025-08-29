package steve6472.orbiter.world.particle.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.orlang.OrlangEnvironment;

import java.util.*;

/**
 * Created by steve6472
 * Date: 8/28/2025
 * Project: Orbiter <br>
 */
public class ParticleBlueprint implements Keyable
{
    private final List<PCBlueprint<?>> blueprints;
    private final Key key;

    public ParticleBlueprint(Key key, List<PCBlueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
    }

    public List<Component> createComponents(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        List<Component> components = new ArrayList<>(blueprints.size());
        blueprints.forEach(blueprint -> components.add(blueprint.create(particleEngine, environment)));
        return components;
    }

    @Override
    public Key key()
    {
        return key;
    }

    public static void load()
    {
        Map<Key, ParticleBlueprint> blueprints = new LinkedHashMap<>();

        Flare.getModuleManager().loadParts(OrbiterParts.PARTICLE_BLUEPRINT, Registries.COMPONENT_BLUEPRINT.valueMapCodec(), (map, key) -> {

            List<PCBlueprint<?>> objects = new ArrayList<>(map.size());

            for (Object value : map.values())
            {
                if (!(value instanceof PCBlueprint<?> blueprint))
                {
                    throw new RuntimeException("Not instance of particle component blueprint!");
                }

                objects.add(blueprint);
            }

            ParticleBlueprint blueprint = new ParticleBlueprint(key, objects);
            blueprints.put(key, blueprint);
        });
        blueprints.values().forEach(Registries.PARTICLE_BLUEPRINT::register);
    }
}
