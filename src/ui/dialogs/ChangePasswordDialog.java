package ui.dialogs;

import manager.UserManager;
import model.User;
import ui.components.FormDialog;
import utils.PasswordFieldWithToggle;
import utils.Validator;

import javax.swing.*;
import java.awt.*;

/** Lets the currently logged-in user change their own password. */
public class ChangePasswordDialog extends FormDialog {

    private final UserManager userManager = new UserManager();
    private final User user;

    private final PasswordFieldWithToggle oldPasswordField = new PasswordFieldWithToggle(15);
    private final PasswordFieldWithToggle newPasswordField = new PasswordFieldWithToggle(15);
    private final PasswordFieldWithToggle confirmPasswordField = new PasswordFieldWithToggle(15);

    public ChangePasswordDialog(Window owner, User user) {
        super(owner, "Change Password", "Update the password for " + user.getUsername() + ".");
        this.user = user;

        addField("Current Password", oldPasswordField);
        addField("New Password", newPasswordField);
        addField("Confirm New Password", confirmPasswordField);

        finish(owner);
    }

    @Override
    protected String saveLabel() { return "Update Password"; }

    @Override
    protected void onSave() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (Validator.isEmpty(oldPass) || Validator.isEmpty(newPass) || Validator.isEmpty(confirmPass)) {
            showError("Please fill in all fields.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            showError("New password and confirmation do not match.");
            return;
        }
        boolean success = userManager.changePassword(user.getUsername(), oldPass, newPass);
        if (success) {
            showSuccess("Password updated successfully.");
            Timer t = new Timer(700, e -> dispose());
            t.setRepeats(false);
            t.start();
        } else {
            showError("Current password is incorrect.");
        }
    }
}
