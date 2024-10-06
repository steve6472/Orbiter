package steve6472.orbiter.commands;

import steve6472.orbiter.player.Player;
import steve6472.orbiter.world.World;

import java.util.function.Consumer;

public class CommandSource
{
	private Player player;
	private World world;
	private Consumer<String> chat;

	public CommandSource(Player player, World world, Consumer<String> feedback)
	{
		this.player = player;
		this.world = world;
		this.chat = feedback;
	}

	public World getWorld()
	{
		return world;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void sendFeedback(String text)
	{
		chat.accept(text);
	}
}
