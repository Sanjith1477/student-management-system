package ui.dialogs;

import manager.StudentManager;
import model.Student;
import ui.components.FormDialog;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Single dialog used for both creating and editing a Student -- pass an
 * existing Student to edit it, or null to create a new one.
 */
public class StudentFormDialog extends FormDialog {

    private final StudentManager studentManager = new StudentManager();
    private final Student editing;
    private final Runnable onSuccess;

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField deptField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JTextField sectionField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public StudentFormDialog(Window owner, Student editing, Runnable onSuccess) {
        super(owner, editing == null ? "Add Student" : "Edit Student",
                editing == null ? "Create a new student profile and login." : "Update this student's profile.");
        this.editing = editing;
        this.onSuccess = onSuccess;

        addField("Student ID", idField);
        addField("Full Name", nameField);
        addField("Department", deptField);
        addField("Year", yearField);
        addField("Section", sectionField);
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
            idField.setText(editing.getStudentId());
            idField.setEditable(false);
            idField.setBackground(Theme.NEUTRAL_BG);
            nameField.setText(editing.getName());
            deptField.setText(editing.getDepartment());
            yearField.setText(editing.getYear());
            sectionField.setText(editing.getSection());
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
        String year = yearField.getText().trim();
        String section = sectionField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || username.isEmpty()) {
            showError("Student ID, Name and Username are required.");
            return;
        }

        Student student = new Student(id, name, dept, year, section, email, phone, username);

        if (editing == null) {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                showError("Please set an initial password.");
                return;
            }
            boolean ok = studentManager.addStudent(student, password);
            if (!ok) {
                showError("A student with this ID already exists.");
                return;
            }
        } else {
            boolean ok = studentManager.editStudent(student);
            if (!ok) {
                showError("Could not update this student.");
                return;
            }
        }
        onSuccess.run();
        dispose();
    }
}
