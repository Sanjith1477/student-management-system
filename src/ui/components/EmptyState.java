package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/** A friendly placeholder shown instead of a blank table/list when there is no data yet. */
public class EmptyState extends JPanel {

    public EmptyState(String title, String subtitle) {
        super();
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel glyph = new JLabel("\uD83D\uDCC2");
        glyph.setFont(Theme.font(Font.PLAIN, 34));
        glyph.setAlignmentX(Component.CENTER_ALIGNMENT);
        glyph.setForeground(Theme.TEXT_MUTED);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.BODY_BOLD);
        titleLabel.setForeground(Theme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_SM, 0, 4, 0));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(Theme.SMALL);
        subLabel.setForeground(Theme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(glyph);
        inner.add(titleLabel);
        inner.add(subLabel);
        add(inner);
    }
}
