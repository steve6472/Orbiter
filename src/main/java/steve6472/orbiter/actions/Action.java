package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Stack;
import steve6472.core.log.Log;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.EntityQueryFunctions;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.AST;
import steve6472.orlang.Orlang;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public interface Action
{
    Logger ACTION_LOGGER = Log.getLogger(Action.class);

    Codec<Action> CODEC = Registries.ACTION.byKeyCodec().dispatch("type", Action::getType, ActionType::mapCodec);
    Codec<List<Action>> SINGLE_OR_LIST = Codec.withAlternative(CODEC.listOf(), CODEC, List::of);

    static <T extends Action> RecordCodecBuilder<T, OrCode> conditionCodec()
    {
        return OrCode.CODEC.optionalFieldOf("condition", new OrCode(List.of(new AST.Node.BoolLiteral(true)), "true")).forGetter(Action::condition);
    }

    static <T extends Action> RecordCodecBuilder<T, EntitySelection> entitySelectionCodec()
    {
        return EntitySelection.CODEC.optionalFieldOf("entity", EntitySelection.UNSELECTED).forGetter(Action::entitySelection);
    }

    static void executeAction(Action action, World world, OrlangEnv environment, UnaryOperator<EntityReference> modifier)
    {
        executeAction(action, world, environment, null, modifier);
    }

    static void startAction(Action action, World world, Entity entity, Map<String, OrlangValue> args, UnaryOperator<EntityReference> modifier)
    {
        OrlangEnv environment = Components.ENVIRONMENT.get(entity);
        Objects.requireNonNull(environment, "Environment must exist");
        if (!(environment.env.queryFunctionSet instanceof EntityQueryFunctions entityQuery))
            throw new IllegalStateException("Entity does not hold EntityQueryFunctions");

        if (!entityQuery.arguments.isEmpty())
            throw new IllegalStateException("Arguments should be empty when starting action");

        if (entityQuery.getCurrentReference() != null)
            throw new IllegalStateException("Entity reference should be null when starting action");

        executeAction(action, world, environment, args, ref -> modifier.apply(ref.withInitiator(entity)));
        environment.clearTemp();
    }

    static void executeAction(Action action, World world, OrlangEnv environment, Map<String, OrlangValue> args, UnaryOperator<EntityReference> modifier)
    {
        Objects.requireNonNull(environment, "Environment must exist");
        if (!(environment.env.queryFunctionSet instanceof EntityQueryFunctions entityQuery))
            throw new IllegalStateException("Entity does not hold EntityQueryFunctions");

        // Setup Entity Query arguments & entity reference
        Stack<Map<String, OrlangValue>> arguments = entityQuery.arguments;
        if (args != null && !args.isEmpty())
            arguments.push(args);
        entityQuery.pushEntityReference(action.entitySelection(), modifier);

        Optional<Entity> selectionOpt = entityQuery.getCurrentReference().get();
        if (selectionOpt.isEmpty())
        {
            ACTION_LOGGER.warning("Entity for selection '%s' not present".formatted(entityQuery.getCurrentReference().defaultSelection()));
            ACTION_LOGGER.warning("Current action: %s".formatted(action));

            // Return Entity Query back to normal
            if (args != null && !args.isEmpty())
                arguments.pop();
            entityQuery.popEntityReference();
            return;
        }

        Entity selectedEntity = selectionOpt.orElse(null);
        OrlangEnv selectedOrlangEnv = Components.ENVIRONMENT.get(selectedEntity);
        Objects.requireNonNull(selectedOrlangEnv, "Environment must exist");
        if (!(selectedOrlangEnv.env.queryFunctionSet instanceof EntityQueryFunctions selectedEntityQuery))
            throw new IllegalStateException("Entity does not hold EntityQueryFunctions");

        selectedEntityQuery.pushFrom(entityQuery);
        selectedOrlangEnv.pushTempFrom(environment);

        // Evaluate condition
        boolean shouldRun;
        OrlangValue val = Orlang.interpreter.interpret(action.condition(), selectedOrlangEnv.env);
        if (val instanceof OrlangValue.Bool bool)
        {
            shouldRun = bool.value();
        } else
        {
            throw new IllegalArgumentException("Return value of a condition was not boolean");
        }

        // Execute action
        if (shouldRun)
        {
            action.execute(world, selectedEntity, selectedOrlangEnv);
        }

        selectedEntityQuery.pop();

        // Return Entity Query back to normal
        if (args != null && !args.isEmpty())
            arguments.pop();
        entityQuery.popEntityReference();
    }

    void execute(World world, Entity entity, OrlangEnv environment);

    OrCode condition();
    EntitySelection entitySelection();

    ActionType<?> getType();
}
