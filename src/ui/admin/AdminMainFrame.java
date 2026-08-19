package ui.admin;

import ui.Login;
import ui.admin.pages.*;
import ui.components.Sidebar;
import ui.shell.MainFrame;

import javax.swing.*;

/** Assembles the admin's MainFrame: sidebar nav items wired to each admin page. */
public class AdminMainFrame {

    public static MainFrame create(String adminDisplayName) {
        Sidebar.NavItem[] items = new Sidebar.NavItem[]{
                new Sidebar.NavItem("home", "\uD83C\uDFE0", "Dashboard"),
                new Sidebar.NavItem("students", "\uD83C\uDF93", "Students"),
                new Sidebar.NavItem("teachers", "\uD83D\uDC69\u200D\uD83C\uDFEB", "Teachers"),
                new Sidebar.NavItem("courses", "\uD83D\uDCDA", "Courses"),
                new Sidebar.NavItem("enrollments", "\uD83D\uDCDD", "Enrollments"),
                new Sidebar.NavItem("reports", "\uD83D\uDCCA", "Reports"),
                new Sidebar.NavItem("admins", "\uD83D\uDD11", "Administrators"),
        };

        // Holder lets the logout callback reference the frame even though the
        // callback must be supplied before the MainFrame instance exists.
        final MainFrame[] holder = new MainFrame[1];

        MainFrame frame = new MainFrame("EduTrack \u2013 Admin", "Administrator", adminDisplayName, items,
                () -> {
                    if (holder[0] != null) holder[0].dispose();
                    SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                });
        holder[0] = frame;

        AdminHomePage home = new AdminHomePage(frame);
        StudentDirectoryPage students = new StudentDirectoryPage();
        TeacherDirectoryPage teachers = new TeacherDirectoryPage();
        CourseManagementPage courses = new CourseManagementPage();
        EnrollmentPage enrollments = new EnrollmentPage();
        ReportsPage reports = new ReportsPage();
        AdminsPage admins = new AdminsPage(adminDisplayName);

        frame.addPage("home", "Admin Dashboard", home, home);
        frame.addPage("students", "Student Directory", students, students);
        frame.addPage("teachers", "Teacher Directory", teachers, teachers);
        frame.addPage("courses", "Course Management", courses, courses);
        frame.addPage("enrollments", "Enrollments", enrollments, enrollments);
        frame.addPage("reports", "Reports", reports, reports);
        frame.addPage("admins", "Administrators", admins, admins);

        frame.openDefault("home");
        return frame;
    }
}
