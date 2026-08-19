package utils;

import javax.swing.*;
import java.awt.*;

/**
 * A small, hand-drawn eye icon (open / crossed-out) used to toggle
 * password visibility. Drawn with Java2D instead of an emoji font so it
 * renders identically on every platform.
 */
public class EyeIcon implements Icon {

    private final int size;
    private final boolean open;
    private final Color color;

    public EyeIcon(int size, boolean open) {
        this(size, open, new Color(110, 110, 110));
    }

    public EyeIcon(int size, boolean open, Color color) {
        this.size = size;
        this.open = open;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int w = size;
        int h = (int) Math.round(size * 0.62);
        int top = (size - h) / 2;

        // Almond-shaped eye outline made of two arcs
        g2.drawArc(0, top, w, h, 0, 180);
        g2.drawArc(0, top - (h / 2), w, h, 180, 180);

        // Pupil
        int pupil = (int) Math.round(h * 0.55);
        g2.fillOval((w - pupil) / 2, top + (h - pupil) / 2 + 1, pupil, pupil);

        if (!open) {
            // Diagonal slash to indicate "hidden"
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(1, size - 2, size - 1, 1);
        }

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
