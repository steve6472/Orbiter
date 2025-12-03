package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.Constants;
import steve6472.orbiter.scheduler.Scheduler;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record Schedule(OrCode condition, EntitySelection entitySelection, long delay, Unit unit, List<Action> actions) implements Action
{
    public static final Codec<Schedule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Codec.LONG.fieldOf("delay").forGetter(Schedule::delay),
        Unit.CODEC.optionalFieldOf("unit", Unit.TICK).forGetter(Schedule::unit),
        Action.CODEC.listOf().fieldOf("actions").forGetter(Schedule::actions)
    ).apply(instance, Schedule::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        // entity is what the scheduler action will be executed on
        // no other entities will be accessible from scheduled function (in case of iterating for example)

        // Scheduled actions should be somehow added to an entity instead of running from Scheduler

        // should access by UUID instead
        Scheduler.runTaskLater(() -> {
            for (Action action : actions)
            {
                Action.startAction(action, world, entity, Map.of(), UnaryOperator.identity());
            }
        }, (int) unit.toTicks(delay));
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.SCHEDULE;
    }

    public enum Unit implements StringValue
    {
        SECOND(l -> l * (long) Constants.TICKS_IN_SECOND),
        TICK(l -> l),
        MINUTE(l -> l * (long) Constants.TICKS_IN_SECOND * 60);

        private final Long2LongFunction toTicks;

        Unit(Long2LongFunction toTicks)
        {
            this.toTicks = toTicks;
        }

        public long toTicks(long value)
        {
            return toTicks.get(value);
        }

        public static final Codec<Unit> CODEC = StringValue.fromValues(Unit::values);

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
