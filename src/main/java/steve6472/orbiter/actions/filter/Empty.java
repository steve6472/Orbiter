package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import steve6472.orbiter.world.World;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/28/2025
 * Project: Orbiter <br>
 */
public record Empty(int max, Sort sort) implements Filter
{
    public static final Empty INSTANCE = new Empty(0, Sort.ARBITRARY);
    public static final Codec<Empty> CODEC = Codec.unit(INSTANCE);

    @Override
    public Filter initial()
    {
        return INSTANCE;
    }

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        return List.of();
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        return input;
    }

    @Override
    public FilterType<?> getType()
    {
        // TODO: set type
        return FilterType.EMPTY;
    }
}
