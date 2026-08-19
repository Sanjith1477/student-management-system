package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A circular progress indicator used for the student's overall
 * attendance percentage. Colored via Theme.statusColor so it reads
 * red/amber/green the same way the rest of the app does.
 */
public class ProgressRing extends JComponent {

    private double percentage;
    private String centerText;
    private String captionText;

    public ProgressRing(double percentage, int diameter) {
        this.percentage = percentage;
        this.centerText = String.format("%.0f%%", percentage);
        setPreferredSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    public void setCaption(String caption) { this.captionText = caption; repaint(); }

    public void setPercentage(double pct) {
        this.percentage = pct;
        this.centerText = String.format("%.0f%%", pct);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int stroke = Math.max(8, getWidth() / 12);
        int pad = stroke / 2 + 2;
        int size = Math.min(getWidth(), getHeight()) - pad * 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        Color color = Theme.statusColor(percentage);

        g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Theme.NEUTRAL_BG);
        g2.drawArc(x, y, size, size, 0, 360);

        g2.setColor(color);
        int angle = (int) Math.round(360 * Math.max(0, Math.min(100, percentage)) / 100.0);
        g2.drawArc(x, y, size, size, 90, -angle);

        g2.setFont(Theme.font(Font.BOLD, Math.max(16, size / 4)));
        g2.setColor(Theme.TEXT_PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        int tx = getWidth() / 2 - fm.stringWidth(centerText) / 2;
        int ty = getHeight() / 2 + (captionText != null ? -2 : 0) + fm.getAscent() / 2 - 4;
        g2.drawString(centerText, tx, ty);

        if (captionText != null) {
            g2.setFont(Theme.TINY);
            g2.setColor(Theme.TEXT_MUTED);
            FontMetrics fm2 = g2.getFontMetrics();
            int cx = getWidth() / 2 - fm2.stringWidth(captionText) / 2;
            g2.drawString(captionText, cx, ty + 16);
        }

        g2.dispose();
    }
}
