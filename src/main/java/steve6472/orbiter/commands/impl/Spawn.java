package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.arguments.EntityBlueprintArgument;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.UUID;

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
			NetworkMain network = OrbiterApp.getInstance().getNetwork();
			if ((network.connections() != null && network.lobby().isHost()) || !network.lobby().isLobbyOpen())
			{
				EntityBlueprint blueprint = EntityBlueprintArgument.getEntityBlueprint(c, "blueprint");

				c.getSource().getWorld().addEntity(blueprint, UUID.randomUUID(), true);
			} else
			{
				c.getSource().sendFeedback("Client spawning not supported yet! (need packet for command send and then client command handling... ugh)");
			}
			return 0;
		})));
	}
}
