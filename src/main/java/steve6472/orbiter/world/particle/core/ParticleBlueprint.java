package steve6472.orbiter.world.particle.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.particle.ParticleComponentBlueprints;
import steve6472.orbiter.world.particle.blueprints.ParticleEnvironmentBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleMaxAgeBlueprint;

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

    /// Just a disgusting boolean because emitter needs to know this
    public final boolean containsLocalSpace;
    public final ParticleEnvironmentBlueprint environmentBlueprint;

    public ParticleBlueprint(Key key, List<PCBlueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
        containsLocalSpace = blueprints.stream().anyMatch(e -> e.key().equals(ParticleComponentBlueprints.LOCAL_SPACE.key()));
        environmentBlueprint = blueprints.stream().filter(e -> e.key().equals(ParticleComponentBlueprints.ENVIRONMENT.key())).map(b -> (ParticleEnvironmentBlueprint) b).findFirst().orElseThrow();
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

        Flare.getModuleManager().loadParts(OrbiterParts.PARTICLE_BLUEPRINT, Registries.PARTICLE_COMPONENT_BLUEPRINT.valueMapCodec(), (map, key) -> {
            ParticleBlueprint blueprint = new ParticleBlueprint(key, createObjects(map).getOrThrow());
            blueprints.put(key, blueprint);
        });
        blueprints.values().forEach(Registries.PARTICLE_BLUEPRINT::register);
    }

    private static DataResult<List<PCBlueprint<?>>> createObjects(Map<PCBlueprintEntry<?>, Object> map)
    {
        List<PCBlueprint<?>> objects = new ArrayList<>(map.size());

        for (Object value : map.values())
        {
            if (!(value instanceof PCBlueprint<?> blueprint))
            {
                return DataResult.error(() -> "Not instance of particle component blueprint!");
            }

            objects.add(blueprint);
        }

        // Add default 1s max age to prevent forever particles
        if (objects.stream().noneMatch(e -> e.key().equals(ParticleComponentBlueprints.MAX_AGE.key())))
        {
            objects.add(new ParticleMaxAgeBlueprint(new OrNumValue(1)));
        }

        if (objects.stream().noneMatch(e -> e.key().equals(ParticleComponentBlueprints.ENVIRONMENT.key())))
        {
            objects.add(new ParticleEnvironmentBlueprint(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }

        return DataResult.success(objects);
    }

    private static final Codec<ParticleBlueprint> INLINE_CODEC = Registries.PARTICLE_COMPONENT_BLUEPRINT.valueMapCodec().comapFlatMap(map -> {
        ParticleBlueprint blueprint = new ParticleBlueprint(Constants.key("inline"), createObjects(map).getOrThrow());
        return DataResult.success(blueprint);
    }, blueprint -> {
        Map<PCBlueprintEntry<?>, Object> map = new HashMap<>(blueprint.blueprints.size());

        for (PCBlueprint<?> pcBlueprint : blueprint.blueprints)
        {
            PCBlueprintEntry<?> pcBlueprintEntry = Registries.PARTICLE_COMPONENT_BLUEPRINT.get(pcBlueprint.key());
            map.put(pcBlueprintEntry, pcBlueprintEntry);
        }
        return map;
    });

    public static final Codec<Holder<ParticleBlueprint>> REGISTRY_OR_INLINE_CODEC =
        Codec.withAlternative(Holder.create(Registries.PARTICLE_BLUEPRINT), INLINE_CODEC.xmap(Holder::fromValue, Holder::get));
}
