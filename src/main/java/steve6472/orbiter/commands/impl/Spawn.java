package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import org.joml.Vector3f;
import steve6472.jolt.JoltApp;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.arguments.EntityBlueprintInput;
import steve6472.orbiter.commands.arguments.EntityInputArgument;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.scheduler.Scheduler;

import java.util.List;
import java.util.UUID;

public class Spawn extends Command
{
	public Spawn(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	private void spawn(CommandContext<CommandSource> context, EntityBlueprintInput input, int count)
	{
		NetworkMain network = OrbiterApp.getInstance().getNetwork();
		if ((network.connections() != null && network.lobby().isHost()) || !network.lobby().isLobbyOpen())
		{
			List<Vector3f> vector3fs = JoltApp.generatePositions(count, 0.1f, 0.3f);
			for (int i = 0; i < vector3fs.size(); i++)
			{
				Vector3f v = vector3fs.get(i);
				Scheduler.runTaskLater(() -> context.getSource().getWorld().addEntity(input.blueprint(), UUID.randomUUID(), input.arguments(), v, true), i / 8);
			}
		} else
		{
			context.getSource().sendFeedback("Client spawning not supported yet! (need packet for command send and then client command handling... ugh)");
		}
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			literal("spawn")
				.then(
					argument("blueprint", EntityInputArgument.entityInput())
						.executes(c -> {
							spawn(c, EntityInputArgument.getEntityInput(c, "blueprint"), 1);
							return 0;
						})
						.then(
							argument("count", integer(0))
								.executes(c -> {
									spawn(c, EntityInputArgument.getEntityInput(c, "blueprint"), getInteger(c, "count")
									);
									return 0;
								})
						)
				)
		);
	}
}
