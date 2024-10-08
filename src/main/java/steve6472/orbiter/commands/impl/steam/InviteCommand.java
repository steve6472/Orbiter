package steve6472.orbiter.commands.impl.steam;

import com.codedisaster.steamworks.SteamID;
import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.arguments.SteamFriendArgument;
import steve6472.orbiter.steam.LobbyInvite;
import steve6472.orbiter.steam.SteamMain;

import java.util.List;

public class InviteCommand extends Command
{
	public InviteCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		/*
		dispatcher.register(
			literal("invite")
				.then(literal("friend")
					.then(argument("friend", SteamFriendArgument.steamFriend())
						.executes(c ->
						{
							SteamMain network = OrbiterApp.getInstance().getSteam();
							if (network.lobby == null)
							{
								c.getSource().sendError("Lobby does not exist");
								return 0;
							}
							SteamID friendId = SteamFriendArgument.getFriend(c, "friend");
							c.getSource().sendFeedback("Inviting friend " + network.steamFriends.getFriendPersonaName(friendId));
							boolean b = network.steamMatchmaking.inviteUserToLobby(network.lobby, friendId);
							c.getSource().sendFeedback("Result: " + b);
							return 1;
						})
					)
				).then(literal("accept")
					.then(argument("lobbyId", integer(0))
						.executes(c ->
						{
							SteamMain network = OrbiterApp.getInstance().getSteam();
							List<LobbyInvite> invites = network.invites;
							int inviteIndex = getInteger(c, "lobbyId");

							if (invites.size() <= inviteIndex)
							{
								c.getSource().sendError("Invite index too big, see invites with \"listinvites\"");
								return 0;
							}

							LobbyInvite steamIDSteamIDPair = invites.get(inviteIndex);
							network.steamMatchmaking.joinLobby(steamIDSteamIDPair.lobby());

							return 1;
						})
					)
				)
		);*/
	}
}
