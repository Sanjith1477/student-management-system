package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A dashboard "stat card": a big number, a label, an optional subtitle,
 * and an accent-colored glyph badge. Used for the metric rows on every
 * role's dashboard (total students, attendance %, etc.).
 */
public class MetricCard extends RoundedPanel {

    public MetricCard(String label, String value, String subtitle, Color accent, String glyph) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));

        JLabel glyphLabel = new JLabel(glyph, SwingConstants.CENTER);
        glyphLabel.setForeground(accent);
        glyphLabel.setFont(Theme.H3);
        glyphLabel.setOpaque(false);
        RoundedIconWrap wrap = new RoundedIconWrap(glyphLabel, mix(accent, Color.WHITE, 0.85f));

        JLabel labelText = new JLabel(label);
        labelText.setFont(Theme.SMALL_BOLD);
        labelText.setForeground(Theme.TEXT_SECONDARY);
        labelText.setBorder(BorderFactory.createEmptyBorder(0, Theme.SPACE_SM, 0, 0));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(wrap, BorderLayout.WEST);
        headerRow.add(labelText, BorderLayout.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_SM, 0, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.METRIC);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(valueLabel);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel sub = new JLabel(subtitle);
            sub.setFont(Theme.TINY);
            sub.setForeground(Theme.TEXT_MUTED);
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            center.add(sub);
        }

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(headerRow);
        content.add(center);

        add(content, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 108));
    }

    private static Color mix(Color a, Color b, float ratio) {
        int r = (int) (a.getRed() * (1 - ratio) + b.getRed() * ratio);
        int g = (int) (a.getGreen() * (1 - ratio) + b.getGreen() * ratio);
        int bl = (int) (a.getBlue() * (1 - ratio) + b.getBlue() * ratio);
        return new Color(r, g, bl);
    }

    /** Small rounded-square wrapper used behind the glyph icon. */
    private static class RoundedIconWrap extends JPanel {
        private final Color bg;
        RoundedIconWrap(JComponent inner, Color bg) {
            super(new BorderLayout());
            this.bg = bg;
            setOpaque(false);
            setPreferredSize(new Dimension(36, 36));
            inner.setOpaque(false);
            add(inner, BorderLayout.CENTER);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
