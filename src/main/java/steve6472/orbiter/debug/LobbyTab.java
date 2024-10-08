package steve6472.orbiter.debug;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import steve6472.core.log.Log;
import steve6472.orbiter.steam.LobbyInvite;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.steam.lobby.Lobby;
import steve6472.orbiter.steam.lobby.LobbyManager;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/5/2024
 * Project: Orbiter <br>
 */
public class LobbyTab
{
    private static final Logger LOGGER = Log.getLogger(LobbyTab.class);

    private final SteamMain steam;

    public JPanel mainPanel;

    LobbyTab(SteamMain steamMain)
    {
        this.steam = steamMain;
        start();
    }

    private void start()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Lobby Creation", createLobbyCreationScreen());
        tabbedPane.add("Lobby Finder", createLobbyFinderScreen());
        tabbedPane.add("Lobby Invites", createLobbyInvitesScreen());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private JComponent createLobbyInvitesScreen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        /*
         * Left panel: List of invites
         */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout()); // Set BorderLayout for the left panel

        // Dummy data for invites
        DefaultListModel<String> inviteListModel = new DefaultListModel<>();

        JList<String> inviteList = new JList<>(inviteListModel);
        JScrollPane inviteScrollPane = new JScrollPane(inviteList);
        leftPanel.add(inviteScrollPane, BorderLayout.CENTER); // List in the center of the left panel

        panel.add(leftPanel, BorderLayout.WEST); // Add the left panel to the main panel (West)

        /*
         * Center panel: Lobby metadata explorer (similar to the createLobbyFinderScreen method)
         */
        JPanel keyValuePanel = new JPanel();
        keyValuePanel.setLayout(new BorderLayout()); // BorderLayout for key-value explorer

        DefaultListModel<String> keyListModel = new DefaultListModel<>();
        JTextArea valueTextArea = new JTextArea();

        // Key list (top part)
        JList<String> keyList = new JList<>(keyListModel);
        JScrollPane keyScrollPane = new JScrollPane(keyList);
        keyValuePanel.add(keyScrollPane, BorderLayout.NORTH); // Key list on top

        // Value text area (bottom part)
        valueTextArea.setEditable(false);
        valueTextArea.setLineWrap(true);
        valueTextArea.setWrapStyleWord(true);
        JScrollPane valueScrollPane = new JScrollPane(valueTextArea);
        keyValuePanel.add(valueScrollPane, BorderLayout.CENTER); // Value text area on bottom

        // Add the keyValuePanel to the main panel (center)
        panel.add(keyValuePanel, BorderLayout.CENTER);

        /*
         * Right panel: Refresh, Accept, and Refuse buttons
         */
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // Vertical layout for buttons

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setAlignmentX(JButton.CENTER_ALIGNMENT); // Center the button horizontally

        // Accept button
        JButton acceptButton = new JButton("Accept");
        acceptButton.setAlignmentX(JButton.CENTER_ALIGNMENT); // Center the button horizontally

        // Refuse button
        JButton refuseButton = new JButton("Refuse");
        refuseButton.setAlignmentX(JButton.CENTER_ALIGNMENT); // Center the button horizontally

        // Add buttons to the right panel
        rightPanel.add(Box.createVerticalGlue()); // Push buttons to the center
        rightPanel.add(refreshButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        rightPanel.add(acceptButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        rightPanel.add(refuseButton);
        rightPanel.add(Box.createVerticalGlue()); // Push buttons to the center

        // Add the right panel to the main panel (East)
        panel.add(rightPanel, BorderLayout.EAST);

        /*
         * Logic to update the metadata explorer when an invite is selected
         */
        final ListSelectionListener[] lastListSelection = {null};
//        inviteList.addListSelectionListener(e -> {
//            int selectedIndex = inviteList.getSelectedIndex();
//            if (selectedIndex >= 0) {
//                keyListModel.clear();
//                valueTextArea.setText(""); // Clear previous data
//
//                String selectedLobby = inviteListModel.getElementAt(selectedIndex);
//                Map<String, String> lobbyData = selectedLobby.lobbyData();
//                lobbyData.forEach((key, _) -> keyListModel.addElement(key)); // Populate keys
//
//                // Add logic to display value when a key is selected
//                if (lastListSelection[0] != null) {
//                    keyList.removeListSelectionListener(lastListSelection[0]);
//                }
//
//                lastListSelection[0] = ev -> {
//                    int keyIndex = keyList.getSelectedIndex();
//                    if (keyIndex >= 0) {
//                        String selectedKey = keyListModel.getElementAt(keyIndex);
//                        valueTextArea.setText(lobbyData.get(selectedKey)); // Show value of selected key
//                    }
//                };
//                keyList.addListSelectionListener(lastListSelection[0]);
//            }
//        });

        // Accept button logic
        acceptButton.addActionListener(_ -> {
            int selectedIndex = inviteList.getSelectedIndex();
            if (selectedIndex >= 0) {
                LobbyInvite remove = steam.lobbyManager.lobbyInvites.remove(selectedIndex);

                steam.steamMatchmaking.joinLobby(remove.lobby().lobbyID());

                // Refresh the view
                valueTextArea.setText("");
                keyListModel.removeAllElements();
                inviteListModel.clear();

                for (LobbyInvite lobbyInvite : steam.lobbyManager.lobbyInvites)
                {
                    inviteListModel.addElement(steam.steamFriends.getFriendPersonaName(lobbyInvite.invitee()));
                }
            }
        });

        // Refuse button logic
        refuseButton.addActionListener(_ -> {
            int selectedIndex = inviteList.getSelectedIndex();
            if (selectedIndex >= 0) {
                steam.lobbyManager.lobbyInvites.remove(selectedIndex);

                // Refresh the view
                valueTextArea.setText("");
                keyListModel.removeAllElements();
                inviteListModel.clear();

                for (LobbyInvite lobbyInvite : steam.lobbyManager.lobbyInvites)
                {
                    inviteListModel.addElement(steam.steamFriends.getFriendPersonaName(lobbyInvite.invitee()));
                }
            }
        });

        // Refresh button logic
        refreshButton.addActionListener(_ -> {
            valueTextArea.setText("");
            keyListModel.removeAllElements();
            inviteListModel.clear();

            for (LobbyInvite lobbyInvite : steam.lobbyManager.lobbyInvites)
            {
                inviteListModel.addElement(steam.steamFriends.getFriendPersonaName(lobbyInvite.invitee()));
            }
        });

        return panel;
    }

    private JComponent createLobbyCreationScreen()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout()); // Set BorderLayout for the main panel
        JButton start = new JButton("START");

        // Left panel with lobby creation options
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // Vertical layout for left side components

        int padding = 10; // Define padding between components

        // Number spin field (with range from 2 to LobbyManager.MAX_MEMBERS)
        JLabel spinLabel = new JLabel("Number of Players:");
        spinLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT); // Align to the left
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, LobbyManager.MAX_MEMBERS, 1); // Default value 2, min 2, max 16, step 1
        JSpinner playerSpinner = new JSpinner(spinnerModel);
        playerSpinner.setAlignmentX(JSpinner.LEFT_ALIGNMENT); // Align spinner to the left
        leftPanel.add(spinLabel);
        leftPanel.add(playerSpinner);
        leftPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Add space between components

        // ComboBox for selecting an enum value (Lobby Type)
        JLabel comboLabel = new JLabel("Lobby Type:");
        comboLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        JComboBox<SteamMatchmaking.LobbyType> comboBox = new JComboBox<>(SteamMatchmaking.LobbyType.values());
        comboBox.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        leftPanel.add(comboLabel);
        leftPanel.add(comboBox);
        leftPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Add space between components

        // Create and Close Buttons
        JButton createButton = new JButton("Create");
        createButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        JButton closeButton = new JButton("Close");
        closeButton.setVisible(false);
        closeButton.setAlignmentX(JButton.LEFT_ALIGNMENT);

        createButton.addActionListener(_ -> {
            int selectedPlayers = (Integer) playerSpinner.getValue();
            SteamMatchmaking.LobbyType lobbyType = (SteamMatchmaking.LobbyType) comboBox.getSelectedItem();
            steam.lobbyManager.createLobby(lobbyType, selectedPlayers, _ ->
            {
                closeButton.setVisible(true);
                createButton.setVisible(false);
            });
        });

        closeButton.addActionListener(_ -> {
            steam.lobbyManager.closeLobby();
            closeButton.setVisible(false);
            createButton.setVisible(true);
        });

        leftPanel.add(createButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, padding)));
        leftPanel.add(closeButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, padding)));

        // Add glue to push components up and leave space at the bottom
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the left panel to the left side of the main panel
        panel.add(leftPanel, BorderLayout.WEST);

        // Right panel for player list and invite functionality
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // Ensure it stretches

        // List of players that can be invited
        JLabel inviteLabel = new JLabel("Available Players:");
        inviteLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        DefaultListModel<String> availablePlayersModel = new DefaultListModel<>();
        JList<String> availablePlayersList = new JList<>(availablePlayersModel);
        availablePlayersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane availablePlayersScroll = new JScrollPane(availablePlayersList);
        availablePlayersScroll.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        availablePlayersScroll.setPreferredSize(new Dimension(300, 150)); // Set preferred size to span the panel

        JButton inviteButton = new JButton("Invite");
        inviteButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        JButton inviteUpdateButton = new JButton("R"); // Load refresh icon here
        inviteUpdateButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        inviteUpdateButton.setToolTipText("Update Player List");

        inviteButton.addActionListener(e -> {
            int friendIndex = availablePlayersList.getSelectedIndex();
            SteamID friendByIndex = steam.steamFriends.getFriendByIndex(friendIndex, SteamFriends.FriendFlags.All);
            steam.steamMatchmaking.inviteUserToLobby(steam.lobbyManager.currentLobby().lobbyID(), friendByIndex);
        });

        inviteUpdateButton.addActionListener(_ -> {
            availablePlayersModel.clear();

            int friendCount = steam.steamFriends.getFriendCount(SteamFriends.FriendFlags.All);
            for (int i = 0; i < friendCount; i++)
            {
                SteamID friendByIndex = steam.steamFriends.getFriendByIndex(i, SteamFriends.FriendFlags.All);
                String friendPersonaName = steam.steamFriends.getFriendPersonaName(friendByIndex);
                availablePlayersModel.addElement(friendPersonaName);
            }
        });

        JPanel invitePanel = new JPanel();
        invitePanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the left
        invitePanel.add(inviteButton);
        invitePanel.add(Box.createRigidArea(new Dimension(5, 0))); // Add space between buttons
        invitePanel.add(inviteUpdateButton);

        rightPanel.add(inviteLabel);
        rightPanel.add(availablePlayersScroll);
        rightPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Space between components
        rightPanel.add(invitePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Space between components

        // List of players in the current lobby
        JLabel lobbyLabel = new JLabel("Players in Lobby:");
        lobbyLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        DefaultListModel<String> currentLobbyModel = new DefaultListModel<>();
        JList<String> currentLobbyList = new JList<>(currentLobbyModel);
        currentLobbyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane currentLobbyScroll = new JScrollPane(currentLobbyList);
        currentLobbyScroll.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        currentLobbyScroll.setPreferredSize(new Dimension(300, 150)); // Set preferred size to span the panel

        JButton kickButton = new JButton("Kick");
        kickButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        JButton currentLobbyRefresh = new JButton("R"); // Load refresh icon here
        currentLobbyRefresh.setAlignmentX(JButton.LEFT_ALIGNMENT);
        currentLobbyRefresh.setToolTipText("Update Lobby List");

        kickButton.addActionListener(_ -> {
            int selectedIndex = currentLobbyList.getSelectedIndex();
            SteamID toKick = steam.lobbyManager.currentLobby().getConnectedUsers().get(selectedIndex);
            steam.lobbyManager.currentLobby().kickUser(toKick);
        });

        currentLobbyRefresh.addActionListener(_ -> {
            currentLobbyModel.clear();

            for (SteamID connectedUser : steam.lobbyManager.currentLobby().updateUsers())
            {
                String name = steam.steamFriends.getFriendPersonaName(connectedUser);
                currentLobbyModel.addElement(name);
            }
        });

        JPanel kickPanel = new JPanel();
        kickPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the left
        kickPanel.add(kickButton);
        kickPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Space between buttons
        kickPanel.add(currentLobbyRefresh);

        rightPanel.add(lobbyLabel);
        rightPanel.add(currentLobbyScroll);
        rightPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Space between components
        rightPanel.add(kickPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, padding))); // Space between components

        // Add the right panel to the right side of the main panel
        panel.add(rightPanel, BorderLayout.EAST);


        /*
         * Center
         */

        start.addActionListener(_ -> {
            steam.lobbyManager.currentLobby().startGame();
        });

        panel.add(start, BorderLayout.CENTER);

        return panel;
    }

    private JComponent createLobbyFinderScreen()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Start button at the top
        JButton start = new JButton("Start search");
        panel.add(start, BorderLayout.NORTH);  // Start button at the top (North)

        /*
         * Left panel: Lobbies list with Refresh button
         */
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout()); // Set BorderLayout for left panel

        DefaultListModel<String> keyListModel = new DefaultListModel<>();
        JTextArea valueTextArea = new JTextArea();

        // Lobbies list
        DefaultListModel<Lobby> lobbyDefaultListModel = new DefaultListModel<>();
        JList<Lobby> lobbies = new JList<>(lobbyDefaultListModel);
        start.addActionListener(_ ->
        {
            keyListModel.clear();
            valueTextArea.setText("");
            lobbyDefaultListModel.clear();
            steam.lobbyManager.findLobbies(list ->
            {
                for (Lobby foundLobby : list)
                {
                    lobbyDefaultListModel.addElement(foundLobby);
                }
            });
        });
        JScrollPane lobbyScrollPane = new JScrollPane(lobbies);
        leftPanel.add(lobbyScrollPane, BorderLayout.CENTER); // Add list in the center of the left panel

        panel.add(leftPanel, BorderLayout.WEST); // Add left panel to the left (West)

        /*
         * Middle panel: Create a panel to hold both keys (top) and values (bottom)
         */
        JPanel keyValuePanel = new JPanel();
        keyValuePanel.setLayout(new BorderLayout()); // BorderLayout to arrange key list on top and value text on bottom

        // Key list (top part)
        JList<String> keyList = new JList<>(keyListModel);
        JScrollPane keyScrollPane = new JScrollPane(keyList);
        keyValuePanel.add(keyScrollPane, BorderLayout.NORTH); // Key list on top (North)

        // Value text area (bottom part)
        valueTextArea.setEditable(false);
        valueTextArea.setLineWrap(true); // Enable text wrapping
        valueTextArea.setWrapStyleWord(true); // Wrap at word boundaries
        JScrollPane valueScrollPane = new JScrollPane(valueTextArea);
        keyValuePanel.add(valueScrollPane, BorderLayout.CENTER); // Value text area on bottom (Center)

        // Add the keyValuePanel in the middle of the main panel
        panel.add(keyValuePanel, BorderLayout.CENTER);

        /*
         * Right panel: Optional, or you can add more components if needed.
         */

        final ListSelectionListener[] lastListSelection = {null};
        // Add ListSelectionListener to update key and value when a lobby is selected
        lobbies.addListSelectionListener(_ ->
        {
            int selectedIndex = lobbies.getSelectedIndex();
            if (selectedIndex >= 0)
            {
                keyListModel.clear();
                valueTextArea.setText(""); // Clear value text area

                Lobby selectedLobby = lobbyDefaultListModel.getElementAt(selectedIndex);
                Map<String, String> lobbyData = selectedLobby.lobbyData();
                lobbyData.forEach((key, _) ->
                {
                    keyListModel.addElement(key); // Add each key to the key list
                });

                if (lastListSelection[0] != null)
                    keyList.removeListSelectionListener(lastListSelection[0]);

                lastListSelection[0] = _ ->
                {
                    int keyIndex = keyList.getSelectedIndex();
                    if (keyIndex >= 0)
                    {
                        String selectedKey = keyListModel.getElementAt(keyIndex);
                        valueTextArea.setText(lobbyData.get(selectedKey)); // Display value of selected key
                    }
                };
                keyList.addListSelectionListener(lastListSelection[0]);
            }
        });

        return panel;
    }
}
