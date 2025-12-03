package steve6472.orbiter.world;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.world.emitter.EmitterQueryFunctions;
import steve6472.orlang.QueryFunctionSet;

import java.util.Locale;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public enum QueryFunction implements StringValue
{
    EMPTY(_ -> new QueryFunctionSet()),
    ENTITY(EntityQueryFunctions::new),
    EMITTER(EmitterQueryFunctions::new);

    public static final Codec<QueryFunction> CODEC = StringValue.fromValues(QueryFunction::values);

    private final Function<Entity, QueryFunctionSet> set;

    QueryFunction(Function<Entity, QueryFunctionSet> set)
    {
        this.set = set;
    }

    public QueryFunctionSet createFunctionSet(Entity entity)
    {
        return set.apply(entity);
    }

    @Override
    public String stringValue()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}
