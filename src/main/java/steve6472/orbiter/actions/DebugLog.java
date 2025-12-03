package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.log.Log;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.util.StringSource;
import steve6472.orbiter.util.ValueSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record DebugLog(OrCode condition, EntitySelection entitySelection, Level level, ValueSource message) implements Action
{
    private static final Logger LOGGER = Log.getLogger(DebugLog.class);

    public static final Codec<DebugLog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Level.CODEC.optionalFieldOf("level", Level.FINEST).forGetter(DebugLog::level),
        ValueSource.CODEC.fieldOf("message").forGetter(DebugLog::message)
    ).apply(instance, DebugLog::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        level.log(Objects.toString(message.get(environment.env)));
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.DEBUG_LOG;
    }

    public enum Level implements StringValue
    {
        FINE(LOGGER::fine),
        FINER(LOGGER::finer),
        FINEST(LOGGER::finest),
        INFO(LOGGER::info),
        WARNING(LOGGER::warning),
        SEVERE(LOGGER::severe);

        private final Consumer<String> logFunc;

        Level(Consumer<String> logFunc)
        {
            this.logFunc = logFunc;
        }

        public void log(String text)
        {
            logFunc.accept(text);
        }

        public static final Codec<Level> CODEC = StringValue.fromValues(Level::values);

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
