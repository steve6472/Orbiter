package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.util.OrbiterCodecs;
import steve6472.orbiter.util.ValueSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.Properties;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record ApplyProperties(OrCode condition,
                              EntitySelection entitySelection,
                              Map<Key, Pair<Property, ValueSource>> add,
                              List<Property> toDefault,
                              List<Key> toRemove) implements Action
{
    private static final Logger LOGGER = Log.getLogger(ApplyProperties.class);
    private static final Function<List<Key>, List<Property>> KEY_TO_PROPERTY = list -> list.stream().map(Registries.PROPERTY::get).toList();
    private static final Function<List<Property>, List<Key>> PROPERTY_TO_KEY = list -> list.stream().map(Property::key).toList();

    public static final Codec<ApplyProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Properties.RAW_MAP_EXPRESSION_CODEC.optionalFieldOf("add", Map.of()).forGetter(ApplyProperties::add),
        OrbiterCodecs.KEY_LIST_OR_SINGLE.optionalFieldOf("default", List.of()).xmap(KEY_TO_PROPERTY, PROPERTY_TO_KEY).forGetter(ApplyProperties::toDefault),
        OrbiterCodecs.KEY_LIST_OR_SINGLE.optionalFieldOf("remove", List.of()).forGetter(ApplyProperties::toRemove)
    ).apply(instance, ApplyProperties::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        // apply_properties order: add, default, remove
        Components.PROPERTIES.ifPresent(entity, properties ->
        {
            add.forEach((_, pair) ->
            {
                Object value = pair.getSecond().get(environment.env);
                try
                {
                    properties.set(pair.getFirst(), value);
                } catch (Exception ex)
                {
                    if (pair.getSecond().isScript())
                    {
                        LOGGER.severe("Can not set property '%s' to '%s': '%s'".formatted(pair.getFirst(), pair.getSecond().getRaw(), value));
                    } else
                    {
                        LOGGER.severe("Can not set property '%s' to '%s'".formatted(pair.getFirst(), pair.getSecond()));
                    }
                    throw ex;
                }
            });
            toDefault.forEach(property -> properties.set(property, property.getDefaultValue()));
            toRemove.forEach(properties::remove);
        });
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.APPLY_PROPERTIES;
    }
}
