package ui;

import manager.UserManager;
import model.User;
import ui.admin.AdminMainFrame;
import ui.components.AppButton;
import ui.components.RoundedPanel;
import ui.theme.Theme;
import utils.PasswordFieldWithToggle;
import utils.Validator;

import javax.swing.*;
import java.awt.*;

/**
 * App entry screen: a centered branded card with username/password and
 * a primary Login button. There is no self-registration here -- every
 * account (admin, teacher, or student) is created by an admin from
 * inside the app.
 */
public class Login extends JFrame {

    private JTextField usernameField;
    private PasswordFieldWithToggle passwordField;
    private JLabel errorLabel;
    private final UserManager userManager = new UserManager();

    public Login() {
        setTitle("EduTrack - Sign In");
        setSize(440, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.BG);
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL, Theme.SPACE_XL));
        card.setPreferredSize(new Dimension(360, 460));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("EduTrack");
        logo.setFont(Theme.font(Font.BOLD, 26));
        logo.setForeground(Theme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Student Management System");
        subtitle.setFont(Theme.SMALL);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(2, 0, Theme.SPACE_LG, 0));

        headerPanel.add(logo);
        headerPanel.add(subtitle);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.SMALL_BOLD);
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setOpaque(true);
        errorLabel.setBackground(Theme.DANGER_BG);
        errorLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new PasswordFieldWithToggle(15);

        formPanel.add(fieldLabel("Username"));
        formPanel.add(styled(usernameField));
        formPanel.add(Box.createVerticalStrut(Theme.SPACE_SM));
        formPanel.add(fieldLabel("Password"));
        formPanel.add(styledPasswordWrapper(passwordField));
        formPanel.add(Box.createVerticalStrut(Theme.SPACE_MD));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(Theme.SPACE_MD));

        AppButton loginBtn = new AppButton("Log In", AppButton.Variant.PRIMARY);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn);

        JLabel hint = new JLabel("<html><center>Demo logins:<br>admin/admin123 &nbsp; teacher01/teacher123 &nbsp; student01/student123</center></html>");
        hint.setFont(Theme.TINY);
        hint.setForeground(Theme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, 0, 0, 0));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BorderLayout());
        center.add(formPanel, BorderLayout.NORTH);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        card.add(hint, BorderLayout.SOUTH);

        add(card);

        passwordField.addActionListener(e -> doLogin());
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.SMALL_BOLD);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JComponent styled(JTextField field) {
        field.setFont(Theme.BODY);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return field;
    }

    private JComponent styledPasswordWrapper(PasswordFieldWithToggle field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 4)));
        return field;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            showError("Please enter both username and password.");
            return;
        }

        User user = userManager.login(username, password);
        if (user == null) {
            showError("Invalid username or password.");
            passwordField.setText("");
            return;
        }

        dispose();
        switch (user.getRole().toUpperCase()) {
            case "ADMIN":
                AdminMainFrame.create(user.getUsername()).setVisible(true);
                break;
            case "TEACHER":
                ui.teacher.TeacherMainFrame.create(user.getUsername()).setVisible(true);
                break;
            case "STUDENT":
                ui.student.StudentMainFrame.create(user.getUsername()).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Unknown role: " + user.getRole());
                new Login().setVisible(true);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
