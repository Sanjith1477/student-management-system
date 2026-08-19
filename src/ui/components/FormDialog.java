package ui.components;

import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for the app's small modal forms (Add/Edit Student, Add/Edit
 * Teacher, Add/Edit Course, Enroll Student...). Gives every form the
 * same look: a title, a card of labeled fields, an inline
 * error/success banner for validation feedback, and Cancel/Save
 * buttons -- so individual dialogs only need to describe their fields.
 */
public abstract class FormDialog extends JDialog {

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JPanel fieldsPanel;
    private final JLabel banner;
    private int row = 0;

    protected FormDialog(Window owner, String title, String subtitle) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.noBorder();
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.H2);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(titleLabel);
        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subLabel = new JLabel(subtitle);
            subLabel.setFont(Theme.SMALL);
            subLabel.setForeground(Theme.TEXT_MUTED);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            subLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
            headerPanel.add(subLabel);
        }
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_MD, 0));

        banner = new JLabel(" ");
        banner.setFont(Theme.SMALL_BOLD);
        banner.setOpaque(true);
        banner.setVisible(false);
        banner.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(headerPanel, BorderLayout.NORTH);
        top.add(banner, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        card.add(fieldsPanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, 0, 0, 0));
        AppButton cancel = new AppButton("Cancel", AppButton.Variant.SECONDARY);
        cancel.addActionListener(e -> dispose());
        AppButton save = new AppButton(saveLabel(), AppButton.Variant.PRIMARY);
        save.addActionListener(e -> onSave());
        footer.add(cancel);
        footer.add(save);
        card.add(footer, BorderLayout.SOUTH);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        outer.add(card, BorderLayout.CENTER);
        add(outer, BorderLayout.CENTER);

        getRootPane().setDefaultButton(save);
    }

    protected String saveLabel() { return "Save"; }

    /** Adds a labeled field row (e.g. "Full Name" -> JTextField). */
    protected void addField(String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.SMALL_BOLD);
        l.setForeground(Theme.TEXT_SECONDARY);
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 2, 0);
        fieldsPanel.add(l, gbc);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 2, 0);
        styleField(field);
        fieldsPanel.add(field, gbc);
    }

    protected void addRaw(JComponent component) {
        gbc.gridy = row++;
        gbc.insets = new Insets(6, 0, 6, 0);
        fieldsPanel.add(component, gbc);
    }

    private void styleField(JComponent field) {
        field.setFont(Theme.BODY);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 34));
        if (field instanceof JTextField || field instanceof JComboBox) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        }
    }

    protected void showError(String message) {
        banner.setText(message);
        banner.setBackground(Theme.DANGER_BG);
        banner.setForeground(Theme.DANGER);
        banner.setVisible(true);
        pack();
    }

    protected void showSuccess(String message) {
        banner.setText(message);
        banner.setBackground(Theme.SUCCESS_BG);
        banner.setForeground(Theme.SUCCESS);
        banner.setVisible(true);
        pack();
    }

    /** Called when the Save button is pressed; implementations validate and persist. */
    protected abstract void onSave();

    protected void finish(Window owner) {
        setMinimumSize(new Dimension(420, getPreferredSize().height));
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
