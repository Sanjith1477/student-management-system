package ui.dialogs;

import manager.TeacherManager;
import model.Teacher;
import ui.components.FormDialog;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/** Single dialog used for both creating and editing a Teacher. */
public class TeacherFormDialog extends FormDialog {

    private final TeacherManager teacherManager = new TeacherManager();
    private final Teacher editing;
    private final Runnable onSuccess;

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField deptField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public TeacherFormDialog(Window owner, Teacher editing, Runnable onSuccess) {
        super(owner, editing == null ? "Add Teacher" : "Edit Teacher",
                editing == null ? "Create a new teacher profile and login." : "Update this teacher's profile.");
        this.editing = editing;
        this.onSuccess = onSuccess;

        addField("Teacher ID", idField);
        addField("Full Name", nameField);
        addField("Department", deptField);
        addField("Email", emailField);
        addField("Phone", phoneField);
        addField("Username", usernameField);
        if (editing == null) {
            addField("Password", passwordField);
        } else {
            JLabel note = new JLabel("Use Reset Password from the directory to change the login password.");
            note.setFont(Theme.TINY);
            note.setForeground(Theme.TEXT_MUTED);
            addRaw(note);
        }

        if (editing != null) {
            idField.setText(editing.getTeacherId());
            idField.setEditable(false);
            idField.setBackground(Theme.NEUTRAL_BG);
            nameField.setText(editing.getName());
            deptField.setText(editing.getDepartment());
            emailField.setText(editing.getEmail());
            phoneField.setText(editing.getPhone());
            usernameField.setText(editing.getUsername());
        }

        finish(owner);
    }

    @Override
    protected void onSave() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String dept = deptField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || username.isEmpty()) {
            showError("Teacher ID, Name and Username are required.");
            return;
        }

        Teacher teacher = new Teacher(id, name, dept, email, phone, username);

        if (editing == null) {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                showError("Please set an initial password.");
                return;
            }
            boolean ok = teacherManager.addTeacher(teacher, password);
            if (!ok) {
                showError("A teacher with this ID already exists.");
                return;
            }
        } else {
            boolean ok = teacherManager.editTeacher(teacher);
            if (!ok) {
                showError("Could not update this teacher.");
                return;
            }
        }
        onSuccess.run();
        dispose();
    }
}
