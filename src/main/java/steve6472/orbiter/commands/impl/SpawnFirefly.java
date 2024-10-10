package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

public class SpawnFirefly extends Command
{
	public SpawnFirefly(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("firefly").executes(c ->
		{
			c.getSource().getWorld().addEntity(VolkaniumsRegistries.STATIC_MODEL.get(Key.defaultNamespace("blockbench/static/firefly")), new Position(0, 0.8, 0), Tag.FIREFLY_AI);

			return 1;
		}));
	}
}
