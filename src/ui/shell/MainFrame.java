package ui.shell;

import ui.components.HeaderBar;
import ui.components.Sidebar;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Reusable main application window shared by all three roles: left
 * sidebar navigation, a header with page title + logged-in user +
 * logout, and a CardLayout content panel that swaps between "pages"
 * registered via {@link #addPage}.
 */
public class MainFrame extends JFrame {

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentHost = new JPanel(contentLayout);
    private final Map<String, String> pageTitles = new HashMap<>();
    private final Map<String, Page> pages = new HashMap<>();
    private final HeaderBar header;
    private final Sidebar sidebar;

    public MainFrame(String appTitle, String roleLabel, String displayName,
                      Sidebar.NavItem[] navItems, Runnable onLogout) {
        super(appTitle);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        sidebar = new Sidebar("EduTrack", roleLabel, navItems);
        header = new HeaderBar(displayName, roleLabel, onLogout);

        contentHost.setOpaque(false);
        contentHost.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(header, BorderLayout.NORTH);
        right.add(contentHost, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        sidebar.setOnNavigate(this::showPage);
    }

    public void addPage(String key, String title, JComponent component, Page page) {
        pageTitles.put(key, title);
        pages.put(key, page);
        contentHost.add(wrap(component), key);
    }

    private JComponent wrap(JComponent component) {
        component.setOpaque(false);
        return component;
    }

    public void showPage(String key) {
        String title = pageTitles.get(key);
        if (title != null) header.setTitle(title);
        contentLayout.show(contentHost, key);
        Page page = pages.get(key);
        if (page != null) page.refresh();
    }

    public void openDefault(String key) {
        sidebar.navigate(key);
    }
}
