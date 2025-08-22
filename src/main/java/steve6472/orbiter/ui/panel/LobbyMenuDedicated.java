package steve6472.orbiter.ui.panel;

import steve6472.core.registry.Key;
import steve6472.core.util.MathUtil;
import steve6472.core.util.RandomUtil;
import steve6472.flare.settings.VisualSettings;
import steve6472.moondust.view.PanelView;
import steve6472.moondust.view.property.BooleanProperty;
import steve6472.moondust.view.property.StringProperty;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.api.UserStage;
import steve6472.orbiter.network.impl.dedicated.DedicatedLobby;
import steve6472.orbiter.network.impl.dedicated.DedicatedMain;
import steve6472.orbiter.network.impl.dedicated.DedicatedUserConnection;
import steve6472.orbiter.network.packets.login.hostbound.LoginStart;
import steve6472.orbiter.network.packets.play.hostbound.JunkData;
import steve6472.orbiter.ui.MDUtil;

import java.io.IOException;
import java.util.Random;

/**
 * Created by steve6472
 * Date: 8/18/2025
 * Project: Orbiter <br>
 */
public class LobbyMenuDedicated extends PanelView
{
    public LobbyMenuDedicated(Key key)
    {
        super(key);
    }

    StringProperty joinIpFieldText;
    StringProperty joinPortFieldText;
    StringProperty createPortFieldText;
    public static BooleanProperty lobbyOpen;

    @Override
    protected void createProperties()
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
        lobbyOpen = new BooleanProperty(network.lobby().isLobbyOpen());
        setupCreate();
        setupJoin();
        setupFind();
    }

    private void setupCreate()
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        DedicatedMain network = (DedicatedMain) orbiter.getNetwork();

        BooleanProperty ipFieldPassword = findProperty("create_ip_field:password");
        StringProperty ipFieldText = findProperty("create_ip_field:text");
        createPortFieldText = findProperty("create_port_field:text");
        BooleanProperty invalidPortMessageVisible = findProperty("create_invalid_port_message:visible");
        BooleanProperty portFieldEnabled = findProperty("create_port_field:enabled");
        BooleanProperty createLobbyEnabled = findProperty("create_lobby:enabled");
        BooleanProperty closeLobbyEnabled = findProperty("close_lobby:enabled");
        BooleanProperty lobbyExistsMessageVisible = findProperty("create_lobby_already_exists:visible");
        BooleanProperty ipVisibilityChecked = findProperty("create_ip_visibility:checked");

        createPortFieldText.set(Integer.toString(50000));
        createLobbyEnabled.bind(() -> isValidPort(createPortFieldText.get()) && !lobbyOpen.get(), createPortFieldText, lobbyOpen);
        closeLobbyEnabled.bind(lobbyOpen.copyFrom());
        invalidPortMessageVisible.bind(() -> !isValidPort(createPortFieldText.get()), createPortFieldText);
        portFieldEnabled.bind(lobbyOpen.copyFromInverted());

        ipFieldText.set("localhost");

        lobbyExistsMessageVisible.bind(lobbyOpen.copyFrom());
        ipVisibilityChecked.set(false);
        ipFieldPassword.bind(ipVisibilityChecked.copyFrom());

        BooleanProperty broadcastServerChecked = findProperty("broadcast_server:checked");
        BooleanProperty broadcastServerEnabled = findProperty("broadcast_server:enabled");
        broadcastServerEnabled.bind(() -> lobbyOpen.get() && network.lobby().isHost(), lobbyOpen);
        broadcastServerChecked.set(network.getBroadcaster().isRunning());
        lobbyOpen.addListener((_, _, nVal) -> {
            if (!nVal)
                broadcastServerChecked.set(false);
        });
        broadcastServerChecked.addListener((_, _, nVal) -> {
            if (nVal)
                network.getBroadcaster().start();
            else
                network.getBroadcaster().shutdown();
        });
    }

    private void setupJoin()
    {
        BooleanProperty ipFieldPassword = findProperty("join_ip_field:password");
        BooleanProperty ipVisibilityChecked = findProperty("join_ip_visibility:checked");
        BooleanProperty invalidPortMessageVisible = findProperty("join_invalid_port_message:visible");
        BooleanProperty lobbyExistsMessageVisible = findProperty("join_lobby_already_exists:visible");
        joinIpFieldText = findProperty("join_ip_field:text");
        ipVisibilityChecked.set(ipFieldPassword.get());
        ipFieldPassword.bind(ipVisibilityChecked.copyFrom());

        joinPortFieldText = findProperty("join_port_field:text");
        joinPortFieldText.set("50000");

        joinIpFieldText.set("localhost");

        BooleanProperty joinLobbyEnable = findProperty("join_lobby:enabled");
        joinLobbyEnable.bind(() -> isValidPort(joinPortFieldText.get()) && !lobbyOpen.get(), joinPortFieldText, lobbyOpen);
        invalidPortMessageVisible.bind(() -> !isValidPort(joinPortFieldText.get()), joinPortFieldText);

        lobbyExistsMessageVisible.bind(lobbyOpen.copyFrom());
    }

    private void setupFind()
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        DedicatedMain network = (DedicatedMain) orbiter.getNetwork();

        BooleanProperty detectServerChecked = findProperty("detect_servers:checked");
        BooleanProperty detectServerEnabled = findProperty("detect_servers:enabled");
        detectServerEnabled.bind(() -> !lobbyOpen.get() && !network.lobby().isHost(), lobbyOpen);
        detectServerChecked.set(network.getDetector().isRunning());
        lobbyOpen.addListener((_, _, nVal) -> {
            if (!nVal)
                detectServerChecked.set(false);
        });
        detectServerChecked.addListener((_, _, nVal) -> {
            if (nVal)
                network.getDetector().start();
            else
                network.getDetector().shutdown();
        });
    }

    private boolean isValidPort(String text)
    {
        if (!MathUtil.isInteger(text))
            return false;

        int newPort = Integer.parseInt(text);
        return newPort >= DedicatedMain.MIN_PORT && newPort <= DedicatedMain.MAX_PORT;
    }

    @Override
    protected void createCommandListeners()
    {
        /*
         * Screen
         */

        addCommandListener(Constants.key("back"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            if (orbiter.getClient().getWorld() != null)
            {
                MDUtil.removePanel(Constants.UI.LOBBY_MENU_DEDICATED);
                MDUtil.addPanel(Constants.UI.IN_GAME_MENU);
            } else
            {
                MDUtil.removePanel(Constants.UI.LOBBY_MENU_DEDICATED);
                MDUtil.addPanel(Constants.UI.MAIN_MENU);
            }
        });

        /*
         * Create tab
         */

        addCommandListener(Constants.key("create_lobby"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
            network.lobby().openLobby(Integer.parseInt(createPortFieldText.get()), true);
        });

        addCommandListener(Constants.key("close_lobby"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
            network.lobby().closeLobby();
        });

        /*
         * Manage tab
         */

        addCommandListener(Constants.key("send_test_message"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
            network.connections().broadcastPacket(new JunkData(createRandomByteArray()));
        });

        /*
         * Join tab
         */

        addCommandListener(Constants.key("join_lobby"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
            network.lobby().openLobby(RandomUtil.randomInt(DedicatedMain.MIN_PORT, DedicatedMain.MAX_PORT), false);
            lobbyOpen.set(network.lobby().isLobbyOpen());
            User user = ((DedicatedLobby) network.lobby()).expectConnection(new DedicatedUserConnection(network, joinIpFieldText.get(), Integer.parseInt(joinPortFieldText.get())));
            user.changeUserStage(UserStage.LOGIN);
            network.connections().sendPacket(user, new LoginStart(VisualSettings.USERNAME.get()));
        });

        /*
         * Find
         */
    }

    private static final int MIN_SIZE = 64;
    private static final int MAX_SIZE = 4096;
    private static final Random random = new Random();
    public static byte[] createRandomByteArray()
    {
        // Generate a random size in [MIN_SIZE, MAX_SIZE]
        int size = MIN_SIZE + random.nextInt(MAX_SIZE - MIN_SIZE + 1);

        // Allocate array
        byte[] array = new byte[size];

        // Fill with random values
        random.nextBytes(array);

        return array;
    }
}
