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
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.particle.ParticleComponentBlueprints;
import steve6472.orbiter.world.particle.blueprints.ParticleDirectionBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleEnvironmentBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleInitialSpeedBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleMaxAgeBlueprint;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.codec.OrNumValue;

import java.util.*;
import java.util.function.Supplier;

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
    public final boolean containsLocalSpace, containsFlipbook;
    /// Environment has to be created before all other components
    public final ParticleEnvironmentBlueprint environmentBlueprint;
    /// Velocity is a composition of two blueprints
    public final ParticleDirectionBlueprint direction;
    public final ParticleInitialSpeedBlueprint initialSpeed;

    public ParticleBlueprint(Key key, List<PCBlueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
        containsLocalSpace = blueprints.stream().anyMatch(e -> e.key().equals(ParticleComponentBlueprints.LOCAL_SPACE.key()));
        containsFlipbook = blueprints.stream().anyMatch(e -> e.key().equals(ParticleComponentBlueprints.FLIPBOOK_MODEL.key()));
        environmentBlueprint = find(ParticleComponentBlueprints.ENVIRONMENT);
        direction = find(ParticleComponentBlueprints.DIRECTION);
        initialSpeed = find(ParticleComponentBlueprints.INITIAL_SPEED);
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
        addDefault(objects, ParticleComponentBlueprints.MAX_AGE, () -> new ParticleMaxAgeBlueprint(new OrNumValue(1)));
        addDefault(objects, ParticleComponentBlueprints.ENVIRONMENT, () -> ParticleEnvironmentBlueprint.EMPTY);

        return DataResult.success(objects);
    }

    private static <T extends PCBlueprint<?>> void addDefault(List<PCBlueprint<?>> objects, PCBlueprintEntry<T> entry, Supplier<T> component)
    {
        if (objects.stream().noneMatch(e -> e.key().equals(entry.key())))
        {
            objects.add(component.get());
        }
    }

    private <T extends PCBlueprint<?>> T find(PCBlueprintEntry<T> entry)
    {
        var blueprint = blueprints
            .stream()
            .filter(e -> e.key().equals(entry.key()))
            .findFirst()
            .orElse(null);

        if (blueprint == null)
            return null;

        blueprints.remove(blueprint);
        //noinspection unchecked
        return (T) blueprint;
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
