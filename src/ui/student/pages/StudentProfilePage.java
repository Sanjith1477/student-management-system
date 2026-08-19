package ui.student.pages;

import manager.UserManager;
import model.Student;
import model.User;
import ui.components.AppButton;
import ui.components.RoundedPanel;
import ui.dialogs.ChangePasswordDialog;
import ui.shell.Page;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;

/** Read-only profile view for the student, with a Change Password action. */
public class StudentProfilePage extends JPanel implements Page {

    private final Student student;
    private final User user;
    private final JPanel fieldsHost = new JPanel();

    public StudentProfilePage(Student student, User user) {
        this.student = student;
        this.user = user;
        setLayout(new BorderLayout());
        setOpaque(false);

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));
        card.setPreferredSize(new Dimension(420, 400));

        JLabel title = new JLabel("My Profile");
        title.setFont(Theme.H2);
        title.setForeground(Theme.TEXT_PRIMARY);

        fieldsHost.setLayout(new BoxLayout(fieldsHost, BoxLayout.Y_AXIS));
        fieldsHost.setOpaque(false);
        fieldsHost.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, 0, Theme.SPACE_MD, 0));

        AppButton changePwBtn = new AppButton("Change Password", AppButton.Variant.SECONDARY);
        changePwBtn.addActionListener(e -> new ChangePasswordDialog(SwingUtilities.getWindowAncestor(this), user));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        footer.add(changePwBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(fieldsHost, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrap.setOpaque(false);
        wrap.add(card);
        add(wrap, BorderLayout.NORTH);
    }

    @Override
    public void refresh() {
        fieldsHost.removeAll();
        if (student != null) {
            addRow("Student ID", student.getStudentId());
            addRow("Full Name", student.getName());
            addRow("Department", student.getDepartment());
            addRow("Year", student.getYear());
            addRow("Section", student.getSection());
            addRow("Email", student.getEmail());
            addRow("Phone", student.getPhone());
            addRow("Username", student.getUsername());
        } else {
            JLabel none = new JLabel("No student profile linked to this account.");
            none.setFont(Theme.SMALL);
            none.setForeground(Theme.TEXT_MUTED);
            fieldsHost.add(none);
        }
        revalidate();
        repaint();
    }

    private void addRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel l = new JLabel(label);
        l.setFont(Theme.SMALL_BOLD);
        l.setForeground(Theme.TEXT_SECONDARY);
        JLabel v = new JLabel(value == null || value.isEmpty() ? "\u2014" : value);
        v.setFont(Theme.BODY);
        v.setForeground(Theme.TEXT_PRIMARY);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        fieldsHost.add(row);
    }
}
