package steve6472.orbiter.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import steve6472.orbiter.commands.CommandSource;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
class CommandInput extends JTextField
{
    private final CommandDispatcher<CommandSource> dispatcher;
    private final Supplier<CommandSource> commandSourceSupplier;

    public CommandInput(CommandDispatcher<CommandSource> dispatcher, Supplier<CommandSource> commandSourceSupplier)
    {
        this.dispatcher = dispatcher;
        this.commandSourceSupplier = commandSourceSupplier;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        String userInput = getText();

        ParseResults<CommandSource> parseResults = dispatcher.parse(userInput, commandSourceSupplier.get());

        if (!getText().isBlank())
        {
            CompletableFuture<Suggestions> suggestions = dispatcher.getCompletionSuggestions(parseResults, getCaretPosition());

            try
            {
                for (Suggestion suggestion : suggestions.get().getList())
                {
                    g.setColor(Color.LIGHT_GRAY);
                    g.setFont(getFont().deriveFont(Font.ITALIC));

                    FontMetrics metrics = g.getFontMetrics(getFont());
                    int x = metrics.stringWidth(userInput);
                    int y = getHeight() - (getHeight() - metrics.getHeight()) / 2 - metrics.getDescent();

                    g.drawString(suggestion.getText().substring(userInput.length()), x + 2, y);
                    break;
                }
            } catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
        }
    }
}
