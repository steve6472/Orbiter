package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;

public class CountPhysicsBodies extends Command
{
	public CountPhysicsBodies(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("cpb").executes(c ->
		{
			int i = c.getSource().getWorld().physics().getNumBodies();

			c.getSource().sendFeedback("Body count: " + i);

			return 0;
		}));
	}
}
