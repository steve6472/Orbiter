package steve6472.orbiter.commands.arguments;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class SteamFriendArgument implements ArgumentType<SteamID>
{
	public static SteamFriendArgument steamFriend()
	{
		return new SteamFriendArgument();
	}

	public static SteamID getFriend(CommandContext<CommandSource> source, String name)
	{
		return source.getArgument(name, SteamID.class);
	}

	@Override
	public SteamID parse(StringReader reader)
    {
	    final String text = reader.getRemaining();
	    reader.setCursor(reader.getTotalLength());
		SteamFriends steamFriends = OrbiterApp.getInstance().getSteam().steamFriends;
		int friendCount = steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
		for (int i = 0; i < friendCount; i++)
		{
			SteamID friendByIndex = steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
			String friendPersonaName = steamFriends.getFriendPersonaName(friendByIndex);
			if (friendPersonaName.equalsIgnoreCase(text))
				return friendByIndex;
		}
		return null;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
	{
		List<String> friendNames = new ArrayList<>();
		SteamFriends steamFriends = OrbiterApp.getInstance().getSteam().steamFriends;
		int friendCount = steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
		for (int i = 0; i < friendCount; i++)
		{
			SteamID friendByIndex = steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
			String friendPersonaName = steamFriends.getFriendPersonaName(friendByIndex);
			friendNames.add(friendPersonaName);
		}
		return Command.suggest(builder, friendNames);
	}
}
