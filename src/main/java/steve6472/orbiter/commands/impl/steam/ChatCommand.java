package steve6472.orbiter.commands.impl.steam;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.network.packets.lobby.LobbyChatMessage;
import steve6472.orbiter.steam.lobby.LobbyManager;

public class ChatCommand extends Command
{
	public ChatCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(literal("c").then(argument("text", StringArgumentType.greedyString()).executes(c -> {

			LobbyManager lobbyManager = OrbiterApp.getInstance().getSteam().lobbyManager;
			if (lobbyManager.currentLobby() != null)
			{
				lobbyManager.currentLobby().broadcastLobbyPacket(new LobbyChatMessage(getString(c, "text")));
			}

			return 0;
		})));
	}
}
