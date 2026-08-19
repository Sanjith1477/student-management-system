package ui.student;

import manager.StudentManager;
import manager.UserManager;
import model.Student;
import model.User;
import ui.Login;
import ui.components.Sidebar;
import ui.shell.MainFrame;
import ui.student.pages.*;

import javax.swing.*;

/** Assembles the student's MainFrame: sidebar nav items wired to each student page. */
public class StudentMainFrame {

    public static MainFrame create(String username) {
        User user = new UserManager().findUser(username);
        Student student = new StudentManager().getStudentByUsername(username);
        String displayName = student != null ? student.getName() : username;

        Sidebar.NavItem[] items = new Sidebar.NavItem[]{
                new Sidebar.NavItem("home", "\uD83C\uDFE0", "Dashboard"),
                new Sidebar.NavItem("attendance", "\uD83D\uDCC4", "My Attendance"),
                new Sidebar.NavItem("profile", "\uD83D\uDC64", "My Profile"),
        };

        final MainFrame[] holder = new MainFrame[1];
        MainFrame frame = new MainFrame("EduTrack \u2013 Student", "Student", displayName, items,
                () -> {
                    if (holder[0] != null) holder[0].dispose();
                    SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                });
        holder[0] = frame;

        StudentHomePage home = new StudentHomePage(student);
        StudentAttendancePage attendance = new StudentAttendancePage(student);
        StudentProfilePage profile = new StudentProfilePage(student, user);

        frame.addPage("home", "Student Dashboard", home, home);
        frame.addPage("attendance", "My Attendance", attendance, attendance);
        frame.addPage("profile", "My Profile", profile, profile);

        if (student == null) {
            JOptionPane.showMessageDialog(frame, "No student profile is linked to this account.",
                    "Missing Profile", JOptionPane.WARNING_MESSAGE);
        }

        frame.openDefault("home");
        return frame;
    }
}
