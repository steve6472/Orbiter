package steve6472.orbiter.debug;

import steve6472.core.log.Log;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterMain;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.world.World;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
public class DebugWindow
{
    private static final Logger LOGGER = Log.getLogger(DebugWindow.class);

    private static final int WIDTH = 16 * 50;
    private static final int HEIGHT = 9 * 50;
    private static DebugWindow debugWindow;

    private final JFrame frame;
    private final JTabbedPane tabbedPane;

    private final Commands commands;
    private final Client client;
    private final World world;
    private final SteamMain steamMain;

    public Console console;
    public LobbyTab lobbyTab;

    private DebugWindow(Commands commands, Client client, World world, SteamMain steamMain)
    {
        this.commands = commands;
        this.client = client;
        this.world = world;
        this.steamMain = steamMain;
        this.frame = createFrame();
        this.tabbedPane = createTabbedPane();

        this.frame.setVisible(true);
    }

    private JFrame createFrame()
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(OrbiterMain.STEAM_TEST ? JFrame.EXIT_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        if (OrbiterMain.STEAM_TEST)
        {
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent e)
                {
                    super.windowClosed(e);
                    steamMain.shutdown();
                }
            });
        }
        return frame;
    }

    private JTabbedPane createTabbedPane()
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        console = new Console(commands, client, world);
        lobbyTab = new LobbyTab(steamMain);

        tabbedPane.addTab("Console", console.mainPanel);
        tabbedPane.addTab("Lobby", lobbyTab.mainPanel);
        frame.getContentPane().add(tabbedPane);

        return tabbedPane;
    }

    public static void openDebugWindow(Commands commands, Client client, World world, SteamMain steamMain)
    {
        if (debugWindow != null)
        {
            LOGGER.warning("Console is already open");
            return;
        }
        debugWindow = new DebugWindow(commands, client, world, steamMain);
    }

    public static void closeDebugWindow()
    {
        if (debugWindow != null)
        {
            debugWindow.frame.dispose();
        }
    }

    static DebugWindow instance()
    {
        return debugWindow;
    }
}
