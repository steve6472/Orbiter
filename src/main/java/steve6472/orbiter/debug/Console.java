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

    /* Singleton */
    private static Console console;

    private static final int WIDTH = 16 * 40;
    private static final int HEIGHT = 9 * 40;

    private final Commands commands;
    private final Client client;
    private final World world;

    private JFrame frame;
    private JTextPane logPane;
    private CommandInput commandInput;

    private Console(Commands commands, Client client, World world) {
        this.commands = commands;
        this.client = client;
        this.world = world;
    }

    private void start()
    {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);

        JPanel logPanel = new OverlayPanel(() ->
        {
            ParseResults<CommandSource> parseResults = commands.dispatcher.parse(commandInput.getText(), createSource());
            return commands.dispatcher.getCompletionSuggestions(parseResults, commandInput.getCaretPosition());
        });
        logPanel.setLayout(new BorderLayout());
        frame.getContentPane().add(logPanel);

        logPane = new JTextPane();
        logPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logPane);
        logPanel.add(scrollPane, BorderLayout.CENTER);

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
        }, logPanel::repaint));

        logPanel.add(commandInput, BorderLayout.SOUTH);

        frame.setVisible(true);
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
        return new CommandSource(client.player(), world, feedback -> appendToLog(feedback, Color.GRAY));
    }

    public static void openConsole(Commands commands, Client client, World world)
    {
        if (console != null)
        {
            LOGGER.warning("Console is already open");
            return;
        }
        console = new Console(commands, client, world);
        console.start();
    }

    public static void closeConsole()
    {
        if (console != null)
        {
            console.frame.dispose();
        }
    }
}
