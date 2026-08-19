package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel painted as a rounded "card": white background, subtle border,
 * consistent corner radius. Used throughout the app as the base building
 * block for dashboard cards, forms, and table containers.
 */
public class RoundedPanel extends JPanel {

    private int radius = Theme.RADIUS;
    private Color background = Theme.CARD_BG;
    private Color borderColor = Theme.BORDER;
    private boolean drawBorder = true;

    public RoundedPanel() {
        this(new BorderLayout());
    }

    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public RoundedPanel radius(int r) { this.radius = r; return this; }
    public RoundedPanel background(Color c) { this.background = c; return this; }
    public RoundedPanel borderColor(Color c) { this.borderColor = c; return this; }
    public RoundedPanel noBorder() { this.drawBorder = false; return this; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        if (drawBorder) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
