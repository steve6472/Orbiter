package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record Sequence(OrCode condition, EntitySelection entitySelection, List<Action> actions) implements Action
{
    public static final Codec<Sequence> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Action.CODEC.listOf().fieldOf("actions").forGetter(Sequence::actions)
    ).apply(instance, Sequence::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        for (Action action : actions)
        {
            Action.executeAction(action, world, environment, UnaryOperator.identity());
        }
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.SEQUENCE;
    }
}
