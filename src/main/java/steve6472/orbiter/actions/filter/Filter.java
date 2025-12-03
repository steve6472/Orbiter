package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.World;

import java.util.*;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public interface Filter
{
    Codec<Filter> CODEC = Registries.FILTER.byKeyCodec().dispatch("type", Filter::getType, FilterType::mapCodec);

    static <T extends Filter> RecordCodecBuilder<T, Integer> maxCodec()
    {
        return Codec.intRange(1, 9999).optionalFieldOf("max", 9999).forGetter(Filter::max);
    }

    static <T extends Filter> RecordCodecBuilder<T, Sort> sortCodec()
    {
        return Sort.CODEC.optionalFieldOf("sort", Sort.ARBITRARY).forGetter(Filter::sort);
    }

    static <T extends Filter> RecordCodecBuilder<T, Filter> initialCodec()
    {
        return CODEC.optionalFieldOf("initial", Empty.INSTANCE).forGetter(Filter::initial);
    }

    static Collection<Entity> getEntities(Filter filter, World world, Entity caller)
    {
        Collection<Entity> entities = filter.initial().initialSelection(world, caller);
        entities = filter.sortAndLimit(caller, entities);
        return filter.filterEntities(caller, entities);
    }

    default Collection<Entity> limit(Collection<Entity> entities)
    {
        if (entities.size() > max())
        {
            List<Entity> limitedList = new ArrayList<>(max());
            int i = 0;
            for (Entity entity : entities)
            {
                if (i >= max())
                    break;
                limitedList.add(entity);
                i++;
            }
            return limitedList;
        } else
        {
            return entities;
        }
    }

    default Collection<Entity> sort(Entity caller, Collection<Entity> entities)
    {
        if (sort() == Sort.ARBITRARY)
        {
            return entities;
        } else if (sort() == Sort.RANDOM)
        {
            if (entities instanceof List<Entity> list)
            {
                Collections.shuffle(list);
                return list;
            } else
            {
                List<Entity> randomList = new ArrayList<>(entities);
                Collections.shuffle(randomList);
                return randomList;
            }
        } else
        {
            throw new UnsupportedOperationException(sort() + " is not implemented yet");
        }
    }

    default Collection<Entity> sortAndLimit(Entity caller, Collection<Entity> entities)
    {
        return limit(sort(caller, entities));
    }

    int max();
    Sort sort();
    Filter initial();

    Collection<Entity> initialSelection(World world, Entity caller);
    Collection<Entity> filterEntities(Entity caller, Collection<Entity> input);

    FilterType<?> getType();
}
