package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/28/2025
 * Project: Orbiter <br>
 */
public record HasProperties(int max, Sort sort, Filter initial, List<Property> all, List<Property> exclude) implements Filter
{
    public static final Codec<HasProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Filter.maxCodec(),
        Filter.sortCodec(),
        Filter.initialCodec(),
        Property.ENTRY_CODEC_SINGLE_OR_LIST.optionalFieldOf("all", List.of()).forGetter(HasProperties::all),
        Property.ENTRY_CODEC_SINGLE_OR_LIST.optionalFieldOf("exclude", List.of()).forGetter(HasProperties::exclude)
    ).apply(instance, HasProperties::new));

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        ImmutableArray<Entity> entitiesFor = world.ecsEngine().getEntitiesFor(Family.all(Properties.class).get());
        List<Entity> filtered = new ArrayList<>();

        for (Entity entity : entitiesFor)
        {
            Properties properties = Components.PROPERTIES.get(entity);
            if (!properties.containsAnyProperties(exclude) && properties.containsAllProperties(all))
                filtered.add(entity);
        }

        return filtered;
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        List<Entity> filtered = new ArrayList<>(input.size());

        for (Entity entity : input)
        {
            Properties properties = Components.PROPERTIES.get(entity);
            if (properties == null)
                continue;

            if (!properties.containsAnyProperties(exclude) && properties.containsAllProperties(all))
                filtered.add(entity);
        }
        return filtered;
    }

    @Override
    public FilterType<?> getType()
    {
        return FilterType.HAS_PROPERTIES;
    }
}
