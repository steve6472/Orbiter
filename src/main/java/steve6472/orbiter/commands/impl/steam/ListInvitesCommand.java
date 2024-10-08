package steve6472.orbiter.commands.impl.steam;

import com.codedisaster.steamworks.SteamID;
import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.steam.LobbyInvite;
import steve6472.orbiter.steam.SteamMain;

import java.util.List;

public class ListInvitesCommand extends Command
{
	public ListInvitesCommand(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{/*
		dispatcher.register(literal("listinvites").executes(c ->
		{
			SteamMain network = OrbiterApp.getInstance().getSteam();
			if (network.invites.isEmpty())
			{
				c.getSource().sendFeedback("No invites");
				return 0;
			}

            List<LobbyInvite> invites = network.invites;
            for (int i = 0; i < invites.size(); i++)
            {
	            LobbyInvite invite = invites.get(i);
                SteamID lobby = invite.lobby();
                SteamID invitee = invite.invitee();

                c.getSource().sendFeedback("Index: " + i + " > Invite to lobby " + lobby + " by " + network.steamFriends.getFriendPersonaName(invitee));
            }

			return 1;
		}));*/
	}
}
