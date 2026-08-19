package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A small pill-shaped status label, e.g. "Present" / "Absent",
 * "Active" / "Dropped", "At Risk" / "Good". Reused across every table
 * and dashboard card that needs a status indicator.
 */
public class Badge extends JLabel {

    private final Color bg;
    private final Color fg;

    public Badge(String text, Color background, Color foreground) {
        super(text, SwingConstants.CENTER);
        this.bg = background;
        this.fg = foreground;
        setFont(Theme.SMALL_BOLD);
        setForeground(fg);
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setOpaque(false);
    }

    public static Badge success(String text) { return new Badge(text, Theme.SUCCESS_BG, Theme.SUCCESS); }
    public static Badge danger(String text) { return new Badge(text, Theme.DANGER_BG, Theme.DANGER); }
    public static Badge warning(String text) { return new Badge(text, Theme.WARNING_BG, Theme.WARNING); }
    public static Badge info(String text) { return new Badge(text, Theme.INFO_BG, Theme.INFO); }
    public static Badge neutral(String text) { return new Badge(text, Theme.NEUTRAL_BG, Theme.TEXT_SECONDARY); }

    /** Ready-made badge for an attendance status string ("Present"/"Absent"). */
    public static Badge forAttendanceStatus(String status) {
        if (status != null && status.equalsIgnoreCase("Present")) return success("Present");
        return danger("Absent");
    }

    /** Ready-made badge for an attendance percentage against the 75% risk threshold. */
    public static Badge forPercentage(double pct) {
        if (pct < 75) return danger(String.format("%.0f%% \u2022 At Risk", pct));
        if (pct < 85) return warning(String.format("%.0f%%", pct));
        return success(String.format("%.0f%%", pct));
    }

    public static Badge forEnrollmentStatus(String status) {
        if (status != null && status.equalsIgnoreCase("ACTIVE")) return success("Active");
        return neutral("Dropped");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_PILL, Theme.RADIUS_PILL);
        g2.dispose();
        super.paintComponent(g);
    }
}
