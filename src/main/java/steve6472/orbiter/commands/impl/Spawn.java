package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.core.registry.Key;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.arguments.EntityBlueprintArgument;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

public class Spawn extends Command
{
	public Spawn(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("spawn").then(argument("blueprint", EntityBlueprintArgument.entityBlueprint()).executes(c ->
		{
			EntityBlueprint blueprint = EntityBlueprintArgument.getEntityBlueprint(c, "blueprint");

			c.getSource().getWorld().addEntity(blueprint);

			return 0;
		})));
	}
}
