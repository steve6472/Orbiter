package steve6472.orbiter.ui.panel;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.ui.font.render.Text;
import steve6472.flare.ui.font.render.TextPart;
import steve6472.moondust.MoonDust;
import steve6472.moondust.view.PanelView;
import steve6472.moondust.view.property.StringProperty;
import steve6472.moondust.view.property.TableProperty;
import steve6472.moondust.widget.Widget;
import steve6472.moondust.widget.component.MDText;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.world.World;
import steve6472.radiant.LuauTable;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class InGameChat extends PanelView
{
    private static final Logger LOGGER = Log.getLogger(InGameChat.class);
    private static final int MAX_COMMAND_SUGGESTIONS = 16;

    private final Commands commands;
    private final Client client;

    public InGameChat(Key key)
    {
        super(key);
        commands = OrbiterApp.getInstance().getCommands();
        client = OrbiterApp.getInstance().getClient();
    }

    private StringProperty chatFieldText;
    private TableProperty commandSuggestions;
    private static MDText chatLog;
    private List<Suggestion> lastSuggestions;

    @Override
    protected void createProperties()
    {
        chatFieldText = findProperty("chat_field:text");
        Optional<Widget> logChild = panel.getChild("log");
        logChild.ifPresentOrElse(widget -> {
            if (chatLog == null)
            {
                widget.getComponent(MDText.class).ifPresentOrElse(
                    comp -> chatLog = comp,
                    () -> LOGGER.severe("text component of log widget not found, this will crash the program")
                );
            } else
            {
                widget.addComponent(chatLog);
            }
        }, () -> LOGGER.severe("log child of chat not found, this will crash the program"));

        commandSuggestions = findProperty("suggestions:lines");
        chatFieldText.addListener((_, _, nVal) -> {
            if (nVal.isBlank()) return;

            LuauTable table = new LuauTable();
            lastSuggestions = getSuggestions(nVal, nVal.length());

            for (int i = 0; i < Math.min(lastSuggestions.size(), MAX_COMMAND_SUGGESTIONS); i++)
            {
                table.add(i + 1, lastSuggestions.get(i).getText());
            }

            commandSuggestions.set(table);
        });
    }

    private List<Suggestion> getSuggestions(String text, int caret)
    {
        ParseResults<CommandSource> parseResults = commands.dispatcher.parse(text, createSource());
        CompletableFuture<Suggestions> suggestions = commands.dispatcher.getCompletionSuggestions(parseResults, caret);

        try
        {
            return suggestions.get().getList();
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    protected void createCommandListeners()
    {
        addCommandListener(Constants.key("close"), _ -> OrbiterApp.getInstance().masterRenderer().getWindow().closeWindow());

        addCommandListener(Constants.key("enter_world"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            World world = new World();
            orbiter.setCurrentWorld(world);
            orbiter.setMouseGrab(true);
            MDUtil.removePanel(Constants.key("panel/main_menu"));
        });

        addCommandListener(Constants.key("execute_command"), _ ->
        {
            String commandText = chatFieldText.get();
            LOGGER.info("Executing command: " + commandText);

            boolean error = false;

            try
            {
                commands.dispatcher.execute(commandText, createSource());
            } catch (CommandSyntaxException e)
            {
                appendToLog(e.getMessage(), CommandSource.ResponseStyle.ERROR);
                error = true;
            }

            if (!Keybinds.KEEP_CHAT_OPEN.isActive() && !error)
            {
                MDUtil.removePanel(Constants.UI.IN_GAME_CHAT);
                OrbiterApp.getInstance().setMouseGrab(true);
                chatFieldText.set("");
            }
        });

        addCommandListener(Constants.key("select_command_suggestion"), entryName -> {
            int entry = Integer.parseInt(((String) entryName).substring("entry_".length())) - 1; // -1 'cause lua
            chatFieldText.set(lastSuggestions.get(entry).apply(chatFieldText.get()));
        });
    }

    private void updateLog()
    {
        Text textText = chatLog.text();
        textText = textText.withMutableParts();

        // Count lines
        int lineCount = 0;
        for (TextPart part : textText.parts())
        {
            int count = 0;
            int index = 0;
            while ((index = part.text().indexOf("\n", index)) != -1)
            {
                count++;
                index += "\n".length();
            }
            lineCount += count;
        }

        // Measure current max visible lines
        int pixelHeight = (int) (OrbiterApp.getInstance().masterRenderer().getWindow().getHeight() / MoonDust.getInstance().getPixelScale());
        // sub 18 for the offsets
        int maxLines = (pixelHeight - 18) / 7;
        if (lineCount > maxLines)
        {
            textText = textText.withParts(textText.parts().subList(lineCount - maxLines, lineCount));
        }

        chatLog = new MDText(textText, chatLog.position(), chatLog.inheritWidth());

        Optional<Widget> logChild = panel.getChild("log");
        logChild.ifPresent(w -> w.addComponent(chatLog));
    }

    private CommandSource createSource()
    {
        return new CommandSource(client.player(), client.getWorld(), this::appendToLog);
    }

    private void appendToLog(String text, CommandSource.ResponseStyle color)
    {
        Text textText = chatLog.text();
        textText = textText.withMutableParts();
        textText.parts().add(new TextPart(text + "\n", FlareRegistries.FONT_STYLE.get(color.style)));
        chatLog = new MDText(textText, chatLog.position(), chatLog.inheritWidth());
        updateLog();

        if (color == CommandSource.ResponseStyle.ERROR)
            LOGGER.severe(text);
        else
            LOGGER.info(text);
    }
}
