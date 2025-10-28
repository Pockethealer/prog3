package nhf.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import nhf.logic.UserManager;

public class LoginDialog extends JDialog {

    private final UserManager userManager;
    private String selectedUsername = null;

    private JComboBox<String> userDropdown;
    private JTextField newUserNameField;

    public LoginDialog(UserManager userManager, JFrame parent) {
        super(parent, "Select a user to log in!", true);
        this.userManager = userManager;

        setupGUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        /* létező felhasználó kiválasztása panel */
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        try {
            List<String> allUsers = userManager.getAllUsernames();
            userDropdown = new JComboBox<>(allUsers.toArray(new String[0]));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error on loading users: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            userDropdown = new JComboBox<>();
        }

        JButton loginButton = new JButton("Log-in");
        loginButton.addActionListener(this::loginUser);

        JButton deleteButton = new JButton("Felhasználó törlése");
        deleteButton.addActionListener(this::deleteSelectedUser);

        selectionPanel.add(new JLabel("Choose a user."));
        selectionPanel.add(userDropdown);
        selectionPanel.add(loginButton);
        selectionPanel.add(deleteButton);
        /* új felhasználó felvétele panel */
        JPanel creationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        newUserNameField = new JTextField(15);
        JButton createButton = new JButton("Create new user");
        createButton.addActionListener(this::createUser);

        creationPanel.add(new JLabel("Or create new user:"));
        creationPanel.add(newUserNameField);
        creationPanel.add(createButton);

        // összekapcsolás wrapperrel
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        contentPanel.add(selectionPanel);
        contentPanel.add(creationPanel);

        add(contentPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loginUser(ActionEvent event) {
        String selected = (String) userDropdown.getSelectedItem();
        if (selected != null) {
            this.selectedUsername = selected;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please choose a user", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createUser(ActionEvent event) {
        String newName = newUserNameField.getText().trim();
        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please give a username!", "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userManager.registerUser(newName);
            this.selectedUsername = newName;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Hiba a felhasználó létrehozása közben: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser(ActionEvent event) {
        String selected = (String) userDropdown.getSelectedItem();

        if (selected == null || selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose a user to delete!", "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure to delete '" + selected + "' user?",
                "Delete confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            if (!userManager.deleteUser(selected)) {
                JOptionPane.showMessageDialog(this, "Error deleting user! ", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            userDropdown.removeItem(selected);
            JOptionPane.showMessageDialog(this, "'" + selected + "' user successfully delete!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        }
        if (userManager.getAllUsernames().isEmpty()) {
            // ha töröltünk mindenkit kilépünk
            System.exit(0);
        }
    }

    /**
     * Visszaadja a kiválasztott/létrehozott felhasználó nevét.
     * Ha a dialógus ablakot bezárták, NULL.
     */
    public String getSelectedUsername() {
        return selectedUsername;
    }
}