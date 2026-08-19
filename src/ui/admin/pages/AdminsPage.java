package ui.admin.pages;

import manager.UserManager;
import model.User;
import ui.components.*;
import ui.dialogs.AdminFormDialog;
import ui.shell.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Lets an admin see and manage other administrator accounts. New admin
 * accounts can only be created from here -- there's no public
 * self-registration path for the ADMIN role. A safety check prevents an
 * admin from deleting their own account or the last remaining admin,
 * so the system can never end up with nobody able to log in as admin.
 */
public class AdminsPage extends JPanel implements Page {

    private final UserManager userManager = new UserManager();
    private final String currentUsername;
    private final TablePanel tablePanel;

    public AdminsPage(String currentUsername) {
        this.currentUsername = currentUsername;
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Administrators", new String[]{"Username", "Role", "Session"}, new int[]{0});

        AppButton addBtn = new AppButton("+ Add Admin", AppButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> new AdminFormDialog(SwingUtilities.getWindowAncestor(this), this::refresh));

        AppButton deleteBtn = new AppButton("Delete", AppButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());

        tablePanel.addToolbarButton(deleteBtn);
        tablePanel.addToolbarButton(addBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private void deleteSelected() {
        int row = tablePanel.selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an admin first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String username = (String) tablePanel.getModel().getValueAt(row, 0);

        if (username.equalsIgnoreCase(currentUsername)) {
            JOptionPane.showMessageDialog(this, "You can't delete the account you're currently logged in with.",
                    "Not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userManager.getUsersByRole("ADMIN").size() <= 1) {
            JOptionPane.showMessageDialog(this, "At least one administrator account must remain.",
                    "Not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ConfirmDialog.confirmDelete(this, "admin account \"" + username + "\"")) {
            userManager.deleteUser(username);
            refresh();
        }
    }

    @Override
    public void refresh() {
        List<User> admins = userManager.getUsersByRole("ADMIN");
        Object[][] rows = new Object[admins.size()][];
        for (int i = 0; i < admins.size(); i++) {
            User u = admins.get(i);
            boolean isCurrent = u.getUsername().equalsIgnoreCase(currentUsername);
            rows[i] = new Object[]{u.getUsername(), "Administrator", isCurrent ? "This session" : ""};
        }
        tablePanel.setRows(rows);
    }
}
