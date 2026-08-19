package ui.teacher;

import manager.TeacherManager;
import model.Teacher;
import ui.Login;
import ui.components.Sidebar;
import ui.shell.MainFrame;
import ui.teacher.pages.*;

import javax.swing.*;

/** Assembles the teacher's MainFrame: sidebar nav items wired to each teacher page. */
public class TeacherMainFrame {

    public static MainFrame create(String username) {
        Teacher teacher = new TeacherManager().getTeacherByUsername(username);
        String displayName = teacher != null ? teacher.getName() : username;

        Sidebar.NavItem[] items = new Sidebar.NavItem[]{
                new Sidebar.NavItem("home", "\uD83C\uDFE0", "Dashboard"),
                new Sidebar.NavItem("students", "\uD83C\uDF93", "My Students"),
                new Sidebar.NavItem("mark", "\u2705", "Mark Attendance"),
                new Sidebar.NavItem("history", "\uD83D\uDCC4", "Attendance History"),
        };

        final MainFrame[] holder = new MainFrame[1];
        MainFrame frame = new MainFrame("EduTrack \u2013 Teacher", "Teacher", displayName, items,
                () -> {
                    if (holder[0] != null) holder[0].dispose();
                    SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                });
        holder[0] = frame;

        TeacherHomePage home = new TeacherHomePage(frame, teacher);
        ViewStudentsPage students = new ViewStudentsPage(teacher);
        MarkAttendancePage mark = new MarkAttendancePage(teacher);
        AttendanceHistoryPage history = new AttendanceHistoryPage(teacher);

        frame.addPage("home", "Teacher Dashboard", home, home);
        frame.addPage("students", "My Students", students, students);
        frame.addPage("mark", "Mark Attendance", mark, mark);
        frame.addPage("history", "Attendance History", history, history);

        if (teacher == null) {
            JOptionPane.showMessageDialog(frame, "No teacher profile is linked to this account.",
                    "Missing Profile", JOptionPane.WARNING_MESSAGE);
        }

        frame.openDefault("home");
        return frame;
    }
}
