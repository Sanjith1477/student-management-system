package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A JPasswordField with a small eye button on the right that shows/hides
 * the password when clicked. Drop-in replacement: add it to a layout the
 * same way you would a JPasswordField, and call getPassword() to read it.
 */
public class PasswordFieldWithToggle extends JPanel {

    private final JPasswordField passwordField;
    private final JButton toggleButton;
    private final char hiddenEchoChar;
    private boolean revealed = false;

    public PasswordFieldWithToggle(int columns) {
        super(new BorderLayout(2, 0));
        setOpaque(false);

        passwordField = new JPasswordField(columns);
        hiddenEchoChar = passwordField.getEchoChar();

        toggleButton = new JButton(new EyeIcon(16, true));
        toggleButton.setFocusable(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setMargin(new Insets(0, 4, 0, 4));
        toggleButton.setToolTipText("Show password");
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> toggleVisibility());

        add(passwordField, BorderLayout.CENTER);
        add(toggleButton, BorderLayout.EAST);
    }

    private void toggleVisibility() {
        revealed = !revealed;
        if (revealed) {
            passwordField.setEchoChar((char) 0);
            toggleButton.setIcon(new EyeIcon(16, false));
            toggleButton.setToolTipText("Hide password");
        } else {
            passwordField.setEchoChar(hiddenEchoChar);
            toggleButton.setIcon(new EyeIcon(16, true));
            toggleButton.setToolTipText("Show password");
        }
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void setText(String text) {
        passwordField.setText(text);
    }

    public void addActionListener(ActionListener listener) {
        passwordField.addActionListener(listener);
    }

    /** Access to the underlying field, if finer control is ever needed. */
    public JPasswordField getField() {
        return passwordField;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
    }
}
