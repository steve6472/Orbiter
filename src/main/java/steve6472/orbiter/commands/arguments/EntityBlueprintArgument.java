package steve6472.orbiter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
// TODO: split to KeyArgument for Key and then extend that one
public class EntityBlueprintArgument implements ArgumentType<EntityBlueprint>
{
	public static EntityBlueprintArgument entityBlueprint()
	{
		return new EntityBlueprintArgument();
	}

	public static EntityBlueprint getEntityBlueprint(CommandContext<CommandSource> source, String name)
	{
		return source.getArgument(name, EntityBlueprint.class);
	}

	@Override
	public EntityBlueprint parse(StringReader reader)
    {
	    final String text = reader.getRemaining();
	    reader.setCursor(reader.getTotalLength());
		Key key = Key.parse(text);
	    EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(key);
		if (blueprint == null)
			throw new RuntimeException("Entity " + key + " not found");
	    return blueprint;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		Collection<Key> keys = Registries.ENTITY_BLUEPRINT.keys();
		List<String> entityKeys = new ArrayList<>();
		for (Key key : keys)
		{
			if (!key.namespace().startsWith(builder.getRemaining()))
				entityKeys.add(key.id());
			else
				entityKeys.add(key.toString());
		}
		return Command.suggest(builder, entityKeys);
	}
}
