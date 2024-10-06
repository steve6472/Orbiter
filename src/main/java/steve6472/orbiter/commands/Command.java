package steve6472.orbiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public abstract class Command
{
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
