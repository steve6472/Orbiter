package steve6472.orbiter.commands;

import steve6472.orbiter.player.Player;
import steve6472.orbiter.world.World;

import java.awt.*;
import java.util.function.BiConsumer;

public class CommandSource
{
	private Player player;
	private World world;
	private BiConsumer<String, Color> chat;

	public CommandSource(Player player, World world, BiConsumer<String, Color> feedback)
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
		chat.accept(text, Color.GRAY);
	}

	public void sendError(String text)
	{
		chat.accept(text, Color.RED);
	}
}
