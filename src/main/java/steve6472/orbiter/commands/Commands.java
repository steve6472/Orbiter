package steve6472.orbiter.commands;

import com.mojang.brigadier.CommandDispatcher;
import steve6472.orbiter.commands.impl.CountPhysicsBodies;
import steve6472.orbiter.commands.impl.TpCommand;
import steve6472.orbiter.commands.impl.steam.ChatCommand;

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
        new CountPhysicsBodies(dispatcher);

        new ChatCommand(dispatcher);
    }
}
