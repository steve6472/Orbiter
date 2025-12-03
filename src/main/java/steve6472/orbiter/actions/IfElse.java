package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.AST;
import steve6472.orlang.Orlang;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record IfElse(OrCode condition, EntitySelection entitySelection, List<Action> ifTrue, List<Action> onElse) implements Action
{
    private static final OrCode TRUE = new OrCode(List.of(new AST.Node.BoolLiteral(true)), "true");

    public static final Codec<IfElse> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Action.SINGLE_OR_LIST.fieldOf("if_true").forGetter(IfElse::ifTrue),
        Action.SINGLE_OR_LIST.fieldOf("else").forGetter(IfElse::onElse)
    ).apply(instance, IfElse::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        OrlangValue val = Orlang.interpreter.interpret(condition, environment.env);
        if (!(val instanceof OrlangValue.Bool bool))
            throw new IllegalArgumentException("Return value of a condition was not boolean");

        if (bool.value())
        {
            for (Action action : ifTrue)
            {
                Action.executeAction(action, world, environment, UnaryOperator.identity());
            }
        } else
        {
            for (Action action : onElse)
            {
                Action.executeAction(action, world, environment, UnaryOperator.identity());
            }
        }
    }

    @Override
    public OrCode condition()
    {
        return TRUE;
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.IF_ELSE;
    }
}
