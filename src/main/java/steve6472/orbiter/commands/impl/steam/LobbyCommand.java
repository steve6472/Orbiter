package steve6472.orbiter.commands.impl.steam;

import com.codedisaster.steamworks.*;
import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.steam.OrbiterSteamMatchmaking;
import steve6472.orbiter.steam.SteamMain;

public class LobbyCommand extends Command
{
	public LobbyCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		/*dispatcher.register(literal("lobby").then(literal("create").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();

			if (network.lobby != null)
			{
				c.getSource().sendError("Lobby already exists, use \"lobby close\" to close it");
				return 0;
			}

			network.steamMatchmaking.createLobby(SteamMatchmaking.LobbyType.Public, 2);
			c.getSource().sendFeedback("Created FriendsOnly lobby with max player count of 2");
			return 1;
		})).then(literal("update").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();

			if (network.lobby == null)
			{
				c.getSource().sendError("Lobby does not exist, use \"lobby create\" to create it");
				return 0;
			}

			boolean b = network.steamMatchmaking.setLobbyData(network.lobby, "AppName", "Orbiter");

			c.getSource().sendFeedback("Updated lobby, key: AppName, value: Orbiter result: " + b);
			return 1;
		})).then(literal("close").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();

			if (network.lobby == null)
			{
				c.getSource().sendError("Lobby does not exist, use \"lobby create\" to create it");
				return 0;
			}

			network.steamMatchmaking.leaveLobby(network.lobby);
			c.getSource().sendFeedback("Lobby left");

			return 1;
		})).then(literal("listStart").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();

			network.steamMatchmaking.addRequestLobbyListStringFilter("AppName", "Orbiter", SteamMatchmaking.LobbyComparison.Equal);
			SteamAPICall steamAPICall = network.steamMatchmaking.requestLobbyList();

			return 1;
		})).then(literal("listRequest").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();
			int lastMatchList = OrbiterSteamMatchmaking.lastMatchList;
			for (int i = 0; i < lastMatchList; i++)
			{
				SteamID lobbyByIndex = network.steamMatchmaking.getLobbyByIndex(i);
				network.steamMatchmaking.requestLobbyData(lobbyByIndex);
			}

			return 1;
		})).then(literal("list").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();
			for (SteamID lobby : OrbiterSteamMatchmaking.lobbies)
			{
				System.out.println("\n\nLobby: " + lobby);
				int lobbyDataCount = network.steamMatchmaking.getLobbyDataCount(lobby);
				for (int i = 0; i < lobbyDataCount; i++)
				{
					SteamMatchmakingKeyValuePair steamMatchmakingKeyValuePair = new SteamMatchmakingKeyValuePair();
                    if (!network.steamMatchmaking.getLobbyDataByIndex(lobby, i, steamMatchmakingKeyValuePair))
                    {
						c.getSource().sendError("Error when getting lobby data at index " + i);
                        break;
                    }
					System.out.println(steamMatchmakingKeyValuePair.getKey() + " " + steamMatchmakingKeyValuePair.getValue());
				}
			}

			return 1;
		})));*/
	}
}
