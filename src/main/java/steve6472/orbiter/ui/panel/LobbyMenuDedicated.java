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
import steve6472.orbiter.ui.MDUtil;

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
        setupCreate();
        setupJoin();
    }

    private void setupCreate()
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        DedicatedMain network = (DedicatedMain) orbiter.getNetwork();

        lobbyOpen = new BooleanProperty(network.lobby().isLobbyOpen());

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
            lobbyOpen.set(network.lobby().isLobbyOpen());
        });

        addCommandListener(Constants.key("close_lobby"), _ ->
        {
            OrbiterApp orbiter = OrbiterApp.getInstance();
            DedicatedMain network = (DedicatedMain) orbiter.getNetwork();
            network.lobby().closeLobby();
            lobbyOpen.set(network.lobby().isLobbyOpen());
        });

        /*
         * Manage tab
         */

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
            user.changeUserStage(UserStage.LOGIN_CLIENTBOUND);
            network.connections().sendPacket(user, new LoginStart(VisualSettings.USERNAME.get()));
        });
    }
}
