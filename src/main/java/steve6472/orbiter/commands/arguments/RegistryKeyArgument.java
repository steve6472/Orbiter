package steve6472.orbiter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.ObjectRegistry;
import steve6472.orbiter.Registries;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.concurrent.CompletableFuture;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class RegistryKeyArgument implements ArgumentType<Key>
{
	private final ObjectRegistry<?> registry;

	private RegistryKeyArgument(ObjectRegistry<?> registry)
	{
		this.registry = registry;
	}

	public static <T extends Keyable> RegistryKeyArgument key(ObjectRegistry<T> registry)
	{
		return new RegistryKeyArgument(registry);
	}

	public static Key getKey(CommandContext<CommandSource> source, String name)
	{
		return source.getArgument(name, Key.class);
	}

	public static <T extends Keyable> T resolveKey(CommandContext<CommandSource> source, String name, ObjectRegistry<T> registry)
	{
		Key key = source.getArgument(name, Key.class);
		T t = registry.get(key);
		if (t == null)
			throw new RuntimeException("Entry '%s' not found in registry '%s'".formatted(key, registry.getRegistryKey()));
		return t;
	}

	@Override
	public Key parse(StringReader reader)
    {
	    final String text = reader.getRemaining();
	    reader.setCursor(reader.getTotalLength());
        return Key.parse(text);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return Command.suggest(builder, registry);
	}
}
