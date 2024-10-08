package steve6472.orbiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.commands.impl.TpCommand;
import steve6472.orbiter.commands.impl.steam.InviteCommand;
import steve6472.orbiter.commands.impl.steam.ListInvitesCommand;
import steve6472.orbiter.commands.impl.steam.LobbyCommand;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public class Commands
{
    public final CommandDispatcher<CommandSource> dispatcher;

    public Commands()
    {
        dispatcher = new CommandDispatcher<>();
        init();
    }

    public void init()
    {
        new TpCommand(dispatcher);
        new InviteCommand(dispatcher);
        new ListInvitesCommand(dispatcher);
    }
}
