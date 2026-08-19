package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The app's left navigation rail: a brand header, a list of nav items
 * (each mapped to a page key), and a logout button pinned to the
 * bottom. Shared by every role -- only the list of nav items differs.
 */
public class Sidebar extends JPanel {

    public static class NavItem {
        final String key;
        final String label;
        final String glyph;
        public NavItem(String key, String glyph, String label) {
            this.key = key; this.glyph = glyph; this.label = label;
        }
    }

    private final Map<String, JPanel> itemPanels = new LinkedHashMap<>();
    private String activeKey;
    private Consumer<String> onNavigate;

    public Sidebar(String appName, String roleLabel, NavItem[] items) {
        super(new BorderLayout());
        setBackground(Theme.SIDEBAR_BG);
        setPreferredSize(new Dimension(230, 0));
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, 0, Theme.SPACE_LG, 0));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(BorderFactory.createEmptyBorder(0, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));

        JLabel logo = new JLabel(appName);
        logo.setForeground(Theme.TEXT_ON_DARK);
        logo.setFont(Theme.H3);
        JLabel role = new JLabel(roleLabel);
        role.setForeground(Theme.ACCENT_TEAL);
        role.setFont(Theme.TINY);
        role.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        brand.add(logo);
        brand.add(role);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        for (NavItem item : items) {
            JPanel itemPanel = buildItem(item);
            itemPanels.put(item.key, itemPanel);
            nav.add(itemPanel);
            nav.add(Box.createVerticalStrut(2));
        }

        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.add(nav, BorderLayout.NORTH);

        add(brand, BorderLayout.NORTH);
        add(navWrap, BorderLayout.CENTER);
    }

    private JPanel buildItem(NavItem item) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(Theme.SIDEBAR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, Theme.SPACE_LG, 10, Theme.SPACE_LG));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(item.glyph + "   " + item.label);
        label.setForeground(Theme.TEXT_ON_DARK_MUTED);
        label.setFont(Theme.BODY_BOLD);
        panel.add(label, BorderLayout.WEST);

        panel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { navigate(item.key); }
            @Override public void mouseEntered(MouseEvent e) {
                if (!item.key.equals(activeKey)) panel.setBackground(Theme.SIDEBAR_ITEM_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!item.key.equals(activeKey)) panel.setBackground(Theme.SIDEBAR_BG);
            }
        });
        return panel;
    }

    public void setOnNavigate(Consumer<String> listener) { this.onNavigate = listener; }

    public void navigate(String key) {
        if (activeKey != null && itemPanels.containsKey(activeKey)) {
            JPanel prev = itemPanels.get(activeKey);
            prev.setBackground(Theme.SIDEBAR_BG);
            for (Component c : prev.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(Theme.TEXT_ON_DARK_MUTED);
            }
        }
        activeKey = key;
        JPanel cur = itemPanels.get(key);
        if (cur != null) {
            cur.setBackground(Theme.SIDEBAR_ITEM_ACTIVE);
            for (Component c : cur.getComponents()) {
                if (c instanceof JLabel) ((JLabel) c).setForeground(Theme.TEXT_ON_DARK);
            }
        }
        if (onNavigate != null) onNavigate.accept(key);
    }
}
