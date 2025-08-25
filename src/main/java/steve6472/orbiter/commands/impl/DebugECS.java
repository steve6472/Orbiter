package steve6472.orbiter.commands.impl;

import com.badlogic.ashley.core.Entity;
import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;

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
			for (Entity entity : c.getSource().getWorld().ecsEngine().getEntities())
			{
				c.getSource().sendFeedback(entity.getComponents().toString());
			}
//			c.getSource().getWorld().ecs().findEntitiesWith(UUID.class).forEach(e -> {
//				c.getSource().sendFeedback(Arrays.toString(((IntEntity) e.entity()).getComponentArray()));
//			});

			return 0;
		}));
	}
}
