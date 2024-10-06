package steve6472.orbiter.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import org.joml.Vector3f;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.arguments.Vec3fArgument;

public class TpCommand extends Command
{
	public TpCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("teleport").then(argument("location", Vec3fArgument.vec3()).executes(c ->
		{
			Vector3f location = Vec3fArgument.getCoords(c, "location");
			c.getSource().getPlayer().teleport(location);
			c.getSource().sendFeedback("Teleported player to " + location);
			return 1;
		})));
	}
}
