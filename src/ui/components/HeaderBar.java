package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Top bar shown above the main content area: current page title on the
 * left, a "logged in as" chip and logout button on the right.
 */
public class HeaderBar extends JPanel {

    private final JLabel titleLabel;

    public HeaderBar(String displayName, String roleLabel, Runnable onLogout) {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(Theme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_LG, Theme.SPACE_MD, Theme.SPACE_LG)));

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(Theme.H2);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACE_SM, 0));
        right.setOpaque(false);

        JPanel userChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        userChip.setOpaque(false);
        JLabel avatar = new JLabel(initials(displayName));
        avatar.setOpaque(true);
        avatar.setBackground(Theme.PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(Theme.SMALL_BOLD);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30, 30));

        JPanel nameCol = new JPanel();
        nameCol.setOpaque(false);
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(Theme.SMALL_BOLD);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        JLabel roleLbl = new JLabel(roleLabel);
        roleLbl.setFont(Theme.TINY);
        roleLbl.setForeground(Theme.TEXT_MUTED);
        nameCol.add(nameLabel);
        nameCol.add(roleLbl);

        userChip.add(new RoundAvatar(avatar));
        userChip.add(nameCol);

        AppButton logoutBtn = new AppButton("Logout", AppButton.Variant.SECONDARY);
        logoutBtn.addActionListener(e -> onLogout.run());

        right.add(userChip);
        right.add(logoutBtn);

        add(titleLabel, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setTitle(String title) { titleLabel.setText(title); }

    private static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.length() > 0 ? sb.toString() : "?";
    }

    private static class RoundAvatar extends JPanel {
        RoundAvatar(JLabel inner) {
            super(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(30, 30));
            inner.setOpaque(false);
            add(inner, BorderLayout.CENTER);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.PRIMARY);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
