package steve6472.orbiter.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.util.Obj;
import steve6472.orbiter.Registries;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.properties.*;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.OrlangValue;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class EntityInputArgument implements ArgumentType<EntityBlueprintInput>
{
	private static final char START_PROPERTIES = '[';
	private static final char END_PROPERTIES = ']';
	private static final char PROPERTIES_SEPARATOR = ',';
	private static final char PROPERTIES_ASSIGNMENT = '=';

	private static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType(obj -> new LiteralMessage("Entity '%s' not found".formatted(obj)));
	private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPAWN_ARGUMENT = new DynamicCommandExceptionType(obj -> new LiteralMessage("Spawn argument '%s' not found in required or optional".formatted(obj)));
	private static final DynamicCommandExceptionType ERROR_EXPECTED_VALUE = new DynamicCommandExceptionType(obj -> new LiteralMessage("Expected property value %s".formatted(obj)));
	private static final DynamicCommandExceptionType INCORRECT_ARGUMENT_VALUE = new DynamicCommandExceptionType(obj -> new LiteralMessage("Incorrect argument value, reason: %s".formatted(obj)));
	private static final DynamicCommandExceptionType ARGUMENT_NO_IN_RANGE = new DynamicCommandExceptionType(obj -> new LiteralMessage("Argument is not within range '%s'".formatted(obj)));
	private static final DynamicCommandExceptionType REPEATED_ARGUMENT = new DynamicCommandExceptionType(obj -> new LiteralMessage("Argument '%s' is repeated".formatted(obj)));
	private static final DynamicCommandExceptionType MISSING_ARGUMENT = new DynamicCommandExceptionType(obj -> new LiteralMessage("Argument '%s' is missing".formatted(obj)));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;

	public static EntityInputArgument entityInput()
	{
		return new EntityInputArgument();
	}

	public static EntityBlueprintInput getEntityInput(CommandContext<CommandSource> source, String name)
	{
		return source.getArgument(name, EntityBlueprintInput.class);
	}

	@Override
	public EntityBlueprintInput parse(StringReader reader) throws CommandSyntaxException
    {
	    Obj<EntityBlueprint> entityBlueprintObj = Obj.empty();
		Map<String, OrlangValue> arguments = new HashMap<>();
		parse(reader, new Visitor()
		{
			@Override
			public void visitBlueprint(EntityBlueprint blueprint)
			{
				entityBlueprintObj.set(blueprint);
			}

			@Override
			public void visitArgument(String argumentName, OrlangValue value)
			{
				arguments.put(argumentName, value);
			}
		});

	    EntityBlueprint blueprint = entityBlueprintObj.get();
		List<String> set = new ArrayList<>(blueprint.getSpawnArguments().required().keySet());
		set.removeAll(arguments.keySet());
		if (!set.isEmpty())
			throw MISSING_ARGUMENT.create(set.getFirst());

	    return new EntityBlueprintInput(blueprint, arguments);
	}

	private void parse(StringReader reader, Visitor visitor) throws CommandSyntaxException
    {
		int start = reader.getCursor();

        try
        {
            new State(reader, visitor).parse();
        } catch (CommandSyntaxException e)
        {
			reader.setCursor(start);
			throw e;
        }
    }

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		StringReader reader = new StringReader(builder.getInput());
		reader.setCursor(builder.getStart());

		SuggestionsVisitor visitor = new SuggestionsVisitor();
		State state = new State(reader, visitor);

        try
        {
            state.parse();
        } catch (CommandSyntaxException ignored)
        {}

		return visitor.resolveSuggestions(builder, reader);
	}

	private static class State
	{
		private final StringReader reader;
		private final Visitor visitor;
		private EntityBlueprint blueprint;

		State(StringReader stringReader, Visitor visitor)
		{
			this.reader = stringReader;
			this.visitor = visitor;
		}

		public void parse() throws CommandSyntaxException
		{
			visitor.visitSuggestions(this::suggestEntity);
			readEntity();
			visitor.visitSuggestions(builder -> this.suggestChars(builder, START_PROPERTIES));
			if (reader.canRead() && reader.peek() == START_PROPERTIES)
			{
				visitor.visitSuggestions(SUGGEST_NOTHING);
				readArguments();
			}
		}

		private void readEntity() throws CommandSyntaxException
		{
			int start = reader.getCursor();
			Key key = Command.parseKey(reader);
			EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(key);
			if (blueprint == null)
            {
				reader.setCursor(start);
                throw ERROR_UNKNOWN_ENTITY.create(key);
            }
			this.blueprint = blueprint;
			visitor.visitBlueprint(blueprint);
		}

		private void readArguments() throws CommandSyntaxException
		{
			reader.expect(START_PROPERTIES);
			Set<String> argumentNames = new HashSet<>();
			visitor.visitSuggestions(b -> suggestArgumentName(b, argumentNames));

			while (reader.canRead() && reader.peek() != END_PROPERTIES)
			{
				reader.skipWhitespace();
				String argumentName = readArgumentName();
				if (argumentNames.contains(argumentName))
					throw REPEATED_ARGUMENT.create(argumentName);
				argumentNames.add(argumentName);

				visitor.visitSuggestions(builder -> this.suggestChars(builder, PROPERTIES_ASSIGNMENT));
				reader.skipWhitespace();
				reader.expect(PROPERTIES_ASSIGNMENT);
				visitor.visitSuggestions(SUGGEST_NOTHING);
				reader.skipWhitespace();
				visitor.visitSuggestions(builder -> suggestArgumentValue(builder, argumentName));
				readArgumentValue(argumentName);
				reader.skipWhitespace();

				visitor.visitSuggestions(builder -> this.suggestChars(builder, PROPERTIES_SEPARATOR, END_PROPERTIES));
				if (!reader.canRead() || reader.peek() != ',')
					break;

				reader.skip();
				reader.skipWhitespace();
				visitor.visitSuggestions(b -> suggestArgumentName(b, argumentNames));
			}

			List<String> a = new ArrayList<>(blueprint.getSpawnArguments().required().keySet());
			a.removeAll(argumentNames);
			if (!a.isEmpty())
				throw MISSING_ARGUMENT.create(a.getFirst());

			reader.expect(']');
			visitor.visitSuggestions(SUGGEST_NOTHING);
		}

		private String readArgumentName() throws CommandSyntaxException
        {
			if (!reader.canRead())
				throw ERROR_EXPECTED_VALUE.create(reader);

			int start = reader.getCursor();
	        String name = reader.readString();
	        Map<String, Property> spawnArguments = blueprint.getSpawnArguments().union();
	        Property property = spawnArguments.get(name);
			if (property == null)
			{
				reader.setCursor(start);
				throw ERROR_UNKNOWN_SPAWN_ARGUMENT.create(name);
			}
			return name;
        }

		private void readArgumentValue(String argumentName) throws CommandSyntaxException
        {
			int start = reader.getCursor();
	        Property property = blueprint.getSpawnArguments().union().get(argumentName);
			try
			{
				OrlangValue toVisit;
				switch (property)
				{
					case PropertyString _ -> toVisit = OrlangValue.string(reader.readString());
					case PropertyEnum p ->
					{
						String s = reader.readString();
						if (!p.range().contains(s))
							throw ARGUMENT_NO_IN_RANGE.create(p.range());
						toVisit = OrlangValue.string(s);
					}
					case PropertyInt p ->
					{
						int i = reader.readInt();
						if (!p.range().fitsInInterval(i))
							throw ARGUMENT_NO_IN_RANGE.create(p.range());
						toVisit = OrlangValue.num(i);
					}
					case PropertyDouble p ->
					{
						double d = reader.readDouble();
						if (!p.range().fitsInInterval(d))
							throw ARGUMENT_NO_IN_RANGE.create(p.range());
						toVisit = OrlangValue.num(d);
					}
					default -> throw new IllegalStateException("Unexpected value: " + property);
				}
				visitor.visitArgument(argumentName, toVisit);
			} catch (Exception ex)
			{
				reader.setCursor(start);
				throw INCORRECT_ARGUMENT_VALUE.create(ex.getMessage());
			}
		}

		private CompletableFuture<Suggestions> suggestEntity(SuggestionsBuilder builder)
		{
			return Command.suggest(builder, Registries.ENTITY_BLUEPRINT);
		}

		private CompletableFuture<Suggestions> suggestArgumentName(SuggestionsBuilder builder, Collection<String> filter)
		{
			return suggestArgumentName(builder, filter, String.valueOf(PROPERTIES_ASSIGNMENT));
		}

		private CompletableFuture<Suggestions> suggestArgumentName(SuggestionsBuilder builder, Collection<String> filter, String suffix)
		{
			Map<String, Property> spawnArguments = blueprint.getSpawnArguments().union();

			HashSet<String> strings = new HashSet<>(spawnArguments.keySet());
			strings.removeAll(filter);
			return Command.suggest(builder, strings, s -> s + suffix);
		}

		private CompletableFuture<Suggestions> suggestArgumentValue(SuggestionsBuilder builder, String argumentName)
		{
			Map<String, Property> spawnArguments = blueprint.getSpawnArguments().union();
			Property property = spawnArguments.get(argumentName);

			if (property instanceof PropertyEnum propertyEnum)
			{
				Set<String> range = propertyEnum.range();
				for (String s : range)
				{
					if (!s.startsWith(builder.getRemaining()))
						continue;

					if (s.equals(propertyEnum.defaultValue()))
                    {
	                    builder.suggest(propertyEnum.defaultValue(), new LiteralMessage("Default value"));
                        continue;
                    }
					builder.suggest(s);
				}
				return builder.buildFuture();
			}

			builder.suggest(Objects.toString(property.getDefaultValue()),  new LiteralMessage("Default value"));
			return builder.buildFuture();
		}

		private CompletableFuture<Suggestions> suggestChars(SuggestionsBuilder builder, char... character)
		{
			if (builder.getRemaining().isEmpty())
            {
	            for (char c : character)
	            {
		            builder.suggest(String.valueOf(c));
	            }
            }
			return builder.buildFuture();
		}
	}

	private static class SuggestionsVisitor implements Visitor
	{
		private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

		@Override
		public void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> function) {
			this.suggestions = function;
		}

		public CompletableFuture<Suggestions> resolveSuggestions(SuggestionsBuilder suggestionsBuilder, StringReader stringReader)
		{
			return this.suggestions.apply(suggestionsBuilder.createOffset(stringReader.getCursor()));
		}
	}

	interface Visitor
	{
		default void visitBlueprint(EntityBlueprint key)
		{
		}

		default void visitArgument(String argumentName, OrlangValue value)
		{
		}

		default void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> function)
		{
		}
	}
}
