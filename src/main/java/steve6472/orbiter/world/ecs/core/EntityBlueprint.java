package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.world.ecs.components.BlueprintReference;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.UUIDComp;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class EntityBlueprint implements Keyable
{
    public record SpawnArguments(Map<String, Property> required, Map<String, Property> optional)
    {
        private static final Codec<Map<String, Property>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, Property.ENTRY_CODEC);
        public static final Codec<SpawnArguments> REQUIRED_INLINE = MAP_CODEC.xmap(map -> new SpawnArguments(map, Map.of()), e -> e.required);
        public static final Codec<SpawnArguments> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
            MAP_CODEC.optionalFieldOf("required", Map.of()).forGetter(SpawnArguments::required),
            MAP_CODEC.optionalFieldOf("optional", Map.of()).forGetter(SpawnArguments::optional)
        ).apply(instance, SpawnArguments::new));

        public static final Codec<SpawnArguments> CODEC = Codec.withAlternative(CODEC_FULL, REQUIRED_INLINE);

        public Map<String, Property> union()
        {
            Map<String, Property> union = new HashMap<>(required);
            union.putAll(optional);
            return union;
        }
    }

    public static final Codec<EntityBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Registries.COMPONENT_BLUEPRINT.valueMapCodec().optionalFieldOf("blueprints", Map.of()).forGetter(e -> cast(e.blueprints)),
        Registries.COMPONENT.valueMapCodec().optionalFieldOf("components", Map.of()).forGetter(e -> cast(e.components)),
        Codec.unboundedMap(Codec.STRING, Registries.COMPONENT.valueMapCodec()).optionalFieldOf("component_groups", Map.of()).forGetter(e -> cast(e.componentGroups)),
        Codec.unboundedMap(Constants.KEY_CODEC, Action.CODEC).optionalFieldOf("events", Map.of()).forGetter(e -> e.events),
        SpawnArguments.CODEC.optionalFieldOf("spawn_arguments", new SpawnArguments(Map.of(), Map.of())).forGetter(e -> e.spawnArguments)
    ).apply(instance, EntityBlueprint::new));

    private final Map<BlueprintEntry<?>, Blueprint<?>> blueprints;

    // TODO: make final once migrated to new
    private Map<ComponentEntry<?>, Supplier<Component>> components;
    private Map<String, Map<ComponentEntry<?>, Supplier<Component>>> componentGroups;
    private Map<Key, Action> events;
    private SpawnArguments spawnArguments;

    private Key key;

    public EntityBlueprint(Key key, Map<BlueprintEntry<?>, Blueprint<?>> blueprints)
    {
        this.key = key;
        this.blueprints = blueprints;
    }

    public EntityBlueprint(
        Map<BlueprintEntry<?>, Object> blueprints,
        Map<ComponentEntry<?>, Object> components,
        Map<String, Map<ComponentEntry<?>, Object>> componentGroups,
        Map<Key, Action> events,
        SpawnArguments spawnArguments)
    {
        this.blueprints = cast(blueprints);
        this.components = cast(components);
        this.componentGroups = cast(componentGroups);
        this.events = events;
        this.spawnArguments = spawnArguments;
    }

    public List<Component> createEntityComponents(Entity entity, UUID uuid)
    {
        List<Component> components = new ArrayList<>(blueprints.size());
        blueprints.forEach((_, blueprint) -> components.addAll(blueprint.createComponents()));
        components.add(new UUIDComp(uuid));
        components.add(new BlueprintReference(key));

        if (this.components != null)
        {
            this.components.forEach((_, compSupplier) -> {
                Component component = compSupplier.get();
                if (component instanceof OrlangEnv env && env.queryFunction != null)
                    env.env.queryFunctionSet = env.queryFunction.createFunctionSet(entity);
                components.add(component);
            });
        }

        /*
         * Special tags for lookup
         */
        if (getEvent(Constants.Events.ON_TICK).isPresent()) components.add(Tag.HAS_ON_TICK_EVENT);
        if (getEvent(Constants.Events.ON_INTERACTION).isPresent()) components.add(Tag.HAS_ON_INTERACT_EVENT);

        return components;
    }

    @Override
    public Key key()
    {
        return key;
    }

    public Optional<Action> getEvent(Key key)
    {
        // TODO: remove once migrated fully to new
        if (events == null)
            return Optional.empty();
        return Optional.ofNullable(events.get(key));
    }

    public Optional<Map<ComponentEntry<?>, Supplier<Component>>> getComponentGroup(String group)
    {
        return Optional.ofNullable(componentGroups.get(group));
    }

    public SpawnArguments getSpawnArguments()
    {
        // TODO: remove once migrated fully to new
        if (spawnArguments == null)
            spawnArguments = new SpawnArguments(Map.of(), Map.of());
        return spawnArguments;
    }

    public static void load()
    {
        Map<Key, EntityBlueprint> blueprints = new LinkedHashMap<>();

        Flare.getModuleManager().loadParts(OrbiterParts.ENTITY_BLUEPRINT, Registries.COMPONENT_BLUEPRINT.valueMapCodec(), (map, key) -> {
            EntityBlueprint blueprint = new EntityBlueprint(key, cast(map));
            blueprints.put(key, blueprint);
        });

        Flare.getModuleManager().loadParts(OrbiterParts.ENTITY_BLUEPRINT_NEW, CODEC, (blueprint, key) -> {
            blueprint.key = key;
            blueprints.put(key, blueprint);
        });

        blueprints.values().forEach(Registries.ENTITY_BLUEPRINT::register);
    }

    private static <A, B> Map<A, B> cast(Map<?, ?> entryMap)
    {
        //noinspection unchecked
        return entryMap
            .entrySet()
            .stream()
            .map(e -> Map.entry((A) e.getKey(), (B) e.getValue()))
            .collect(HashMap::new, (mp, entry) -> mp.put(entry.getKey(), entry.getValue()), Map::putAll);
    }
}
