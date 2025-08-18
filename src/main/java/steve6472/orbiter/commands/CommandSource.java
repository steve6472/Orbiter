package steve6472.orbiter.commands;

import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.world.World;

import java.util.function.BiConsumer;

public class CommandSource
{
	private final Player player;
	private final World world;
	private final BiConsumer<String, ResponseStyle> chat;

	public CommandSource(Player player, World world, BiConsumer<String, ResponseStyle> feedback)
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
		chat.accept(text, ResponseStyle.FEEDBACK);
	}

	public void sendError(String text)
	{
		chat.accept(text, ResponseStyle.ERROR);
	}

	public enum ResponseStyle
	{
		ERROR(Constants.key("chat/error")),
		FEEDBACK(Constants.key("chat/feedback")),
		BLACK(Constants.key("chat/black")),
		WEIRD_GREEN(Constants.key("chat/weird_green")),
		SUCCESS(Constants.key("chat/success"));

		public final Key style;

        ResponseStyle(Key style)
        {
            this.style = style;
        }
    }
}
