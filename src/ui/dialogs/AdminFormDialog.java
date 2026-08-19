package ui.dialogs;

import manager.UserManager;
import model.User;
import ui.components.FormDialog;
import utils.Validator;

import javax.swing.*;
import java.awt.*;

/**
 * Lets an existing admin create another admin account. There is no
 * public self-registration path for admin accounts -- this dialog is
 * only reachable from inside the admin area of the app.
 */
public class AdminFormDialog extends FormDialog {

    private final UserManager userManager = new UserManager();
    private final Runnable onSuccess;

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();

    public AdminFormDialog(Window owner, Runnable onSuccess) {
        super(owner, "Add Administrator", "Create another admin login for this system.");
        this.onSuccess = onSuccess;

        addField("Username", usernameField);
        addField("Password", passwordField);
        addField("Confirm Password", confirmPasswordField);

        finish(owner);
    }

    @Override
    protected String saveLabel() { return "Add Admin"; }

    @Override
    protected void onSave() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password) || Validator.isEmpty(confirm)) {
            showError("Please fill in all fields.");
            return;
        }
        if (!password.equals(confirm)) {
            showError("Password and confirmation do not match.");
            return;
        }
        if (userManager.findUser(username) != null) {
            showError("That username is already taken.");
            return;
        }

        boolean ok = userManager.addUser(new User(username, password, "ADMIN"));
        if (!ok) {
            showError("Could not create this admin account.");
            return;
        }
        onSuccess.run();
        dispose();
    }
}
