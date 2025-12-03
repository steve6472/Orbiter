package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.World;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/28/2025
 * Project: Orbiter <br>
 */
public record AllOf(int max, Sort sort, Filter initial, List<Filter> filters) implements Filter
{
    public static final Codec<AllOf> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Filter.maxCodec(),
        Filter.sortCodec(),
        Filter.initialCodec(),
        Filter.CODEC.listOf().fieldOf("filters").forGetter(AllOf::filters)
    ).apply(instance, AllOf::new));

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        return List.of();
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        for (Filter filter : filters)
        {
            input = filter.filterEntities(caller, input);
        }
        input = sortAndLimit(caller, input);
        return input;
    }

    @Override
    public FilterType<?> getType()
    {
        return FilterType.ALL_OF;
    }
}
