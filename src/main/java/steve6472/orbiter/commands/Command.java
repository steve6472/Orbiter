package steve6472.orbiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.Nullable;
import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;
import steve6472.orbiter.Registries;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public abstract class Command
{
    private static final DynamicCommandExceptionType ERROR_KEY_INVALID = new DynamicCommandExceptionType(obj -> new LiteralMessage("'%s' is not a valid key".formatted(obj)));

    public Command(CommandDispatcher<CommandSource> dispatcher)
    {
        register(dispatcher);
    }

    protected abstract void register(CommandDispatcher<CommandSource> dispatcher);

    protected LiteralArgumentBuilder<CommandSource> literal(String s)
    {
        return LiteralArgumentBuilder.literal(s);
    }

    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static Key parseKey(StringReader reader) throws CommandSyntaxException
    {
        int start = reader.getCursor();
        while (reader.canRead() && isAllowedInKey(reader.peek()))
            reader.skip();
        String keyStr = reader.getString().substring(start, reader.getCursor());

        try
        {
            return Key.parse(keyStr);
        } catch (Exception exception)
        {
            throw ERROR_KEY_INVALID.create(keyStr);
        }
    }

    private static boolean isAllowedInKey(char c)
    {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == ':' || c == '_' || c == '/';
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, ObjectRegistry<?> registry)
    {
        return suggest(builder, registry, registry.keys(), s -> s);
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, ObjectRegistry<?> registry, Function<String, String> modifier)
    {
        return suggest(builder, registry, null, modifier);
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, ObjectRegistry<?> registry, @Nullable Collection<Key> limiter, Function<String, String> modifier)
    {
        Collection<Key> keys = registry.keys();
        String remaining = builder.getRemaining();
        boolean hasNamespace = remaining.contains(":");
        for (Key key : keys)
        {
            if (limiter != null && !limiter.contains(key))
                continue;

            String keyString = key.toString();
            if (hasNamespace)
            {
                if (keyString.startsWith(remaining))
                    builder.suggest(modifier.apply(keyString));
            } else
            {
                if (key.id().startsWith(remaining))
                    builder.suggest(modifier.apply(keyString));
                else if (key.namespace().startsWith(remaining))
                    builder.suggest(modifier.apply(keyString));
            }
        }

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Collection<String> possibleValues, Function<String, String> modifier)
    {
        String remaining = builder.getRemaining();
        for (String value : possibleValues)
        {
            if (value.startsWith(remaining))
            {
                builder.suggest(modifier.apply(value));
            }
        }

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Iterable<String> source)
    {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String s1 : source)
        {
            if (s1.toLowerCase(Locale.ROOT).startsWith(s))
            {
                builder.suggest(s1);
            }
        }

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Object... source)
    {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (Object s1 : source)
        {
            builder.suggest(s1.toString().toLowerCase());
        }

        return builder.buildFuture();
    }

    /* Integer Argument */

    protected IntegerArgumentType integer()
    {
        return IntegerArgumentType.integer();
    }

    protected IntegerArgumentType integer(int min)
    {
        return IntegerArgumentType.integer(min);
    }

    protected IntegerArgumentType integer(int min, int max)
    {
        return IntegerArgumentType.integer(min, max);
    }

    protected int getInteger(CommandContext<CommandSource> context, String name)
    {
        return IntegerArgumentType.getInteger(context, name);
    }

    /* Float Argument */

    protected FloatArgumentType floatArg()
    {
        return FloatArgumentType.floatArg();
    }

    protected FloatArgumentType floatArg(int min)
    {
        return FloatArgumentType.floatArg(min);
    }

    protected FloatArgumentType floatArg(int min, int max)
    {
        return FloatArgumentType.floatArg(min, max);
    }

    protected float getFloat(CommandContext<CommandSource> context, String name)
    {
        return FloatArgumentType.getFloat(context, name);
    }

    /* Double Argument */

    protected DoubleArgumentType doubleArg()
    {
        return DoubleArgumentType.doubleArg();
    }

    protected DoubleArgumentType doubleArg(int min)
    {
        return DoubleArgumentType.doubleArg(min);
    }

    protected DoubleArgumentType doubleArg(int min, int max)
    {
        return DoubleArgumentType.doubleArg(min, max);
    }

    protected double getDouble(CommandContext<CommandSource> context, String name)
    {
        return DoubleArgumentType.getDouble(context, name);
    }



    /* Long Argument */

    protected LongArgumentType longArg()
    {
        return LongArgumentType.longArg();
    }

    protected LongArgumentType longArg(long min)
    {
        return LongArgumentType.longArg(min);
    }

    protected LongArgumentType longArg(long min, long max)
    {
        return LongArgumentType.longArg(min, max);
    }

    protected long getLong(CommandContext<CommandSource> context, String name)
    {
        return LongArgumentType.getLong(context, name);
    }

    /* String Argument */

    protected StringArgumentType string()
    {
        return StringArgumentType.string();
    }

    protected String getString(CommandContext<CommandSource> context, String name)
    {
        return StringArgumentType.getString(context, name);
    }

    /* Bool Argument */

    protected BoolArgumentType bool()
    {
        return BoolArgumentType.bool();
    }

    protected boolean getBool(CommandContext<CommandSource> context, String name)
    {
        return BoolArgumentType.getBool(context, name);
    }
}
