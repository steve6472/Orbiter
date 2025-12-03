package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.actions.filter.Filter;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record ForEach(OrCode condition, EntitySelection entitySelection, Filter filter, List<Action> actions) implements Action
{
    public static final Codec<ForEach> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Filter.CODEC.fieldOf("filter").forGetter(ForEach::filter),
        Action.CODEC.listOf().fieldOf("actions").forGetter(ForEach::actions)
    ).apply(instance, ForEach::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        Collection<Entity> entities = Filter.getEntities(filter, world, entity);
        for (Entity iteratedEntity : entities)
        {
            for (Action action : actions)
            {
                Action.executeAction(action, world, environment, ref -> ref.withIterated(iteratedEntity).withDefaultSelection(EntitySelection.ITERATED));
            }
        }
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.FOR_EACH;
    }
}
