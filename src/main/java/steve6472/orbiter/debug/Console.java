package steve6472.orbiter.debug;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import steve6472.core.log.Log;
import steve6472.orbiter.Client;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.world.World;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public class Console
{
    private static final Logger LOGGER = Log.getLogger(Console.class);

    private final Commands commands;
    private final Client client;
    private final World world;

    private JTextPane logPane;
    private CommandInput commandInput;
    JPanel mainPanel;

    Console(Commands commands, Client client, World world)
    {
        this.commands = commands;
        this.client = client;
        this.world = world;
        start();
    }

    private void start()
    {
        mainPanel = new OverlayPanel(() ->
        {
            ParseResults<CommandSource> parseResults = commands.dispatcher.parse(commandInput.getText(), createSource());
            return commands.dispatcher.getCompletionSuggestions(parseResults, commandInput.getCaretPosition());
        });
        mainPanel.setLayout(new BorderLayout());

        logPane = new JTextPane();
        logPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.getViewport().addChangeListener(_ -> mainPanel.repaint());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        commandInput = new CommandInput(commands.dispatcher, this::createSource);

        commandInput.addKeyListener(new CommandInputListener(() ->
        {
            if (commandInput.getText().isEmpty())
                return;

            try
            {
                commands.dispatcher.execute(commandInput.getText(), createSource());
            } catch (CommandSyntaxException ex)
            {
                appendToLog(ex.getMessage(), Color.RED);
            }
        }, mainPanel::repaint));

        mainPanel.add(commandInput, BorderLayout.SOUTH);
    }

    private void appendToLog(String text, Color color)
    {
        StyledDocument doc = logPane.getStyledDocument();

        Style style = logPane.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, color);

        try
        {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException e)
        {
            LOGGER.severe(e.getMessage());
        }

        // scroll to the bottom after appending
        logPane.setCaretPosition(doc.getLength());
    }

    private CommandSource createSource()
    {
        return new CommandSource(client.player(), world, this::appendToLog);
    }

    public static void log(String text, Color color)
    {
        if (DebugWindow.instance() == null)
            return;

        DebugWindow.instance().console.appendToLog(text, color);
    }
}
