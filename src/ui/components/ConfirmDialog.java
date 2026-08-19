package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/** A consistent "are you sure?" confirmation used before any deletion. */
public final class ConfirmDialog {

    private ConfirmDialog() { }

    public static boolean confirmDelete(Component parent, String itemDescription) {
        return confirm(parent, "Delete " + itemDescription + "?",
                "This action cannot be undone.", "Delete", AppButton.Variant.DANGER);
    }

    public static boolean confirm(Component parent, String title, String message,
                                   String confirmLabel, AppButton.Variant confirmVariant) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(false);
        dialog.getContentPane().setBackground(Theme.BG);
        dialog.setLayout(new BorderLayout());

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.noBorder();
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.H3);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel msgLabel = new JLabel("<html><div style='width:280px'>" + message + "</div></html>");
        msgLabel.setFont(Theme.BODY);
        msgLabel.setForeground(Theme.TEXT_SECONDARY);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, Theme.SPACE_MD, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(msgLabel, BorderLayout.SOUTH);

        boolean[] result = {false};
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        AppButton cancel = new AppButton("Cancel", AppButton.Variant.SECONDARY);
        cancel.addActionListener(e -> dialog.dispose());
        AppButton confirm = new AppButton(confirmLabel, confirmVariant);
        confirm.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        footer.add(cancel);
        footer.add(confirm);

        card.add(top, BorderLayout.NORTH);
        card.add(footer, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        outer.add(card, BorderLayout.CENTER);
        dialog.add(outer, BorderLayout.CENTER);

        dialog.setMinimumSize(new Dimension(360, 160));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }
}
