package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A rounded, colored JButton with hover feedback. Three ready-made
 * variants (primary / secondary / danger) cover almost every button in
 * the app so screens don't hand-roll button styling repeatedly.
 */
public class AppButton extends JButton {

    public enum Variant { PRIMARY, SECONDARY, DANGER, GHOST }

    private Color base;
    private Color hover;
    private Color fg;
    private final Variant variant;
    private boolean hovering = false;

    public AppButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        applyVariant(variant);
        setFont(Theme.BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
        });
    }

    private void applyVariant(Variant v) {
        switch (v) {
            case PRIMARY:
                base = Theme.PRIMARY; hover = Theme.PRIMARY_HOVER; fg = Color.WHITE;
                break;
            case SECONDARY:
                base = Theme.NEUTRAL_BG; hover = Theme.BORDER_STRONG; fg = Theme.TEXT_PRIMARY;
                break;
            case DANGER:
                base = Theme.DANGER_BG; hover = new Color(0xF8, 0xCE, 0xCE); fg = Theme.DANGER;
                break;
            case GHOST:
            default:
                base = new Color(0, 0, 0, 0); hover = Theme.NEUTRAL_BG; fg = Theme.TEXT_SECONDARY;
                break;
        }
        setForeground(fg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color paint = !isEnabled() ? Theme.BORDER : (hovering ? hover : base);
        g2.setColor(paint);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SM, Theme.RADIUS_SM);
        g2.dispose();
        setForeground(isEnabled() ? fg : Theme.TEXT_MUTED);
        super.paintComponent(g);
    }
}
