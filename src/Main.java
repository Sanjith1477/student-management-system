import ui.Login;
import ui.theme.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
