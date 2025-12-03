package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.util.ValueSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.Properties;
import steve6472.orlang.AST;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.VarContext;

import java.util.*;

/**
 * Created by steve6472
 * Date: 11/30/2025
 * Project: Orbiter <br>
 */
public record TestProperties(int max, Sort sort, Filter initial, Map<Key, Pair<Property, ValueSource>> all, List<Property> exclude) implements Filter
{
    public static final AST.Node.Identifier TEMP_VALUE = new AST.Node.Identifier(VarContext.TEMP, "value");
    public static final Codec<TestProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Filter.maxCodec(),
        Filter.sortCodec(),
        Filter.initialCodec(),
        Properties.RAW_MAP_EXPRESSION_CODEC.optionalFieldOf("all", Map.of()).forGetter(TestProperties::all),
        Property.ENTRY_CODEC_SINGLE_OR_LIST.optionalFieldOf("exclude", List.of()).forGetter(TestProperties::exclude)
    ).apply(instance, TestProperties::new));

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        ImmutableArray<Entity> entitiesFor = world.ecsEngine().getEntitiesFor(Family.all(Properties.class).get());
        List<Entity> filtered = new ArrayList<>();

        if (!exclude.isEmpty())
            throw new RuntimeException("Exclude not implemented but used (:");

        for (Entity entity : entitiesFor)
        {
            Properties properties = Components.PROPERTIES.get(entity);
            OrlangEnv orlangEnv = Components.ENVIRONMENT.get(entity);
            if (orlangEnv == null)
                continue;

            if (properties.containsAnyProperties(exclude))
                continue;

            boolean allPassed = true;
            for (Key key : all.keySet())
            {
                if (!properties.contains(key))
                {
                    allPassed = false;
                    break;
                }

                Object o = properties.get(key);
                if (!Objects.equals(o, all.get(key).getSecond().get(orlangEnv.env)))
                {
                    allPassed = false;
                    break;
                }
            }
            if (allPassed)
                filtered.add(entity);
            // TODO: exclude
        }

        return filtered;
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        List<Entity> filtered = new ArrayList<>(input.size());

        if (!exclude.isEmpty())
            throw new RuntimeException("Exclude not implemented but used (:");

        for (Entity entity : input)
        {
            Properties properties = Components.PROPERTIES.get(entity);
            if (properties == null)
                continue;

            OrlangEnv orlangEnv = Components.ENVIRONMENT.get(entity);
            if (orlangEnv == null)
                continue;

            if (properties.containsAnyProperties(exclude))
                continue;

            boolean allPassed = true;
            for (Key key : all.keySet())
            {
                if (!properties.contains(key))
                {
                    allPassed = false;
                    break;
                }

                Object o = properties.get(key);
                ValueSource source = all.get(key).getSecond();
                if (source.isScript())
                {
                    orlangEnv.env.setValue(TEMP_VALUE, OrlangValue.smartCast(o));
                    Object o1 = source.get(orlangEnv.env);
                    if (!(o1 instanceof Boolean b))
                    {
                        allPassed = false;
                        break;
                    }
                    if (!b)
                    {
                        allPassed = false;
                        break;
                    }

                } else
                {
                    if (!Objects.equals(o, source.get(orlangEnv.env)))
                    {
                        allPassed = false;
                        break;
                    }
                }
            }

            if (allPassed)
                filtered.add(entity);
        }
        return filtered;
    }

    @Override
    public FilterType<?> getType()
    {
        return FilterType.TEST_PROPERTIES;
    }
}
