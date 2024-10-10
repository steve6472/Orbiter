package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import dev.dominion.ecs.engine.IntEntity;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;

import java.util.Arrays;
import java.util.UUID;

public class DebugECS extends Command
{
	public DebugECS(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("ecs").executes(c ->
		{
			c.getSource().getWorld().ecs().findEntitiesWith(UUID.class).forEach(e -> {
				c.getSource().sendFeedback(Arrays.toString(((IntEntity) e.entity()).getComponentArray()));
			});

			return 0;
		}));
	}
}
