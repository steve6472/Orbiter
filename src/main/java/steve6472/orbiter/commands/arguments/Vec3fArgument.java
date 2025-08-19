package steve6472.orbiter.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.joml.Vector3f;
import steve6472.orbiter.commands.CommandSource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public class Vec3fArgument implements ArgumentType<Vector3f>
{
	private static final Collection<String> EXAMPLES = List.of("0 0 0", "32 32 32");

	public static Vec3fArgument vec3()
	{
		return new Vec3fArgument();
	}

	public static Vector3f getCoords(CommandContext<CommandSource> source, String name)
	{
		return source.getArgument(name, Vector3f.class);
	}

	@Override
	public Vector3f parse(StringReader reader) throws CommandSyntaxException
	{
		float x = reader.readFloat();
		reader.skipWhitespace();
		float y = reader.readFloat();
		reader.skipWhitespace();
		float z = reader.readFloat();
		return new Vector3f(x, y, z);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		return Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples()
	{
		return EXAMPLES;
	}
}
