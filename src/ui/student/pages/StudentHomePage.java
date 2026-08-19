package ui.student.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import manager.EnrollmentManager;
import model.Attendance;
import model.Course;
import model.Student;
import ui.components.*;
import ui.shell.Page;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Student landing page: welcome card, overall attendance ring, one
 * subject-wise card per enrolled course, low-attendance warnings, a
 * placeholder announcements card, and recent attendance records.
 * Everything here is scoped to the student's own enrolled courses only.
 */
public class StudentHomePage extends JPanel implements Page {

    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final CourseManager courseManager = new CourseManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final Student student;

    private final JPanel top = new JPanel(new BorderLayout(Theme.SPACE_MD, 0));
    private final JPanel subjectGrid = new JPanel();
    private final JPanel warningsHost = new JPanel();
    private final JPanel recentHost = new JPanel(new BorderLayout());

    public StudentHomePage(Student student) {
        this.student = student;
        setLayout(new BorderLayout(0, Theme.SPACE_MD));
        setOpaque(false);
        top.setOpaque(false);

        JPanel subjectsCard = sectionCard("My Courses \u2013 Attendance");
        subjectGrid.setLayout(new GridLayout(0, 3, Theme.SPACE_MD, Theme.SPACE_MD));
        subjectGrid.setOpaque(false);
        subjectsCard.add(subjectGrid, BorderLayout.CENTER);

        warningsHost.setLayout(new BoxLayout(warningsHost, BoxLayout.Y_AXIS));
        warningsHost.setOpaque(false);

        JPanel recentCard = sectionCard("Recent Attendance");
        recentHost.setOpaque(false);
        recentCard.add(recentHost, BorderLayout.CENTER);

        JPanel announcementsCard = sectionCard("Announcements");
        JLabel placeholder = new JLabel("No announcements yet. Check back later for class updates.");
        placeholder.setFont(Theme.SMALL);
        placeholder.setForeground(Theme.TEXT_MUTED);
        announcementsCard.add(placeholder, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        bottom.setOpaque(false);
        bottom.add(recentCard);
        bottom.add(announcementsCard);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        warningsHost.setAlignmentX(Component.LEFT_ALIGNMENT);
        subjectsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        subjectsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        stack.add(top);
        stack.add(Box.createVerticalStrut(Theme.SPACE_SM));
        stack.add(warningsHost);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(subjectsCard);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(bottom);

        add(stack, BorderLayout.CENTER);
    }

    private RoundedPanel sectionCard(String title) {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel t = new JLabel(title);
        t.setFont(Theme.H3);
        t.setForeground(Theme.TEXT_PRIMARY);
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    @Override
    public void refresh() {
        if (student == null) return;

        double overall = attendanceManager.calculatePercentage(student.getStudentId());
        List<Attendance> allRecords = attendanceManager.attendanceByStudent(student.getStudentId());

        top.removeAll();
        RoundedPanel welcomeCard = new RoundedPanel(new BorderLayout());
        welcomeCard.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG, Theme.SPACE_LG));
        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        JLabel hello = new JLabel("Welcome back, " + student.getName() + "!");
        hello.setFont(Theme.H1);
        hello.setForeground(Theme.TEXT_PRIMARY);
        JLabel meta = new JLabel(student.getStudentId() + " \u2022 " + nullToDash(student.getDepartment())
                + " \u2022 Year " + nullToDash(student.getYear()) + " \u2022 Sec " + nullToDash(student.getSection()));
        meta.setFont(Theme.SMALL);
        meta.setForeground(Theme.TEXT_MUTED);
        meta.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        welcomeText.add(hello);
        welcomeText.add(meta);
        welcomeCard.add(welcomeText, BorderLayout.WEST);

        ProgressRing ring = new ProgressRing(overall, 110);
        ring.setCaption("Overall");
        RoundedPanel ringWrap = new RoundedPanel(new FlowLayout(FlowLayout.CENTER));
        ringWrap.noBorder();
        ringWrap.add(ring);
        welcomeCard.add(ringWrap, BorderLayout.EAST);
        top.add(welcomeCard, BorderLayout.CENTER);

        warningsHost.removeAll();
        List<String> courseIds = enrollmentManager.getEnrolledCourseIds(student.getStudentId());
        if (!allRecords.isEmpty() && AttendanceManager.isAtRisk(overall)) {
            warningsHost.add(warningBanner("Your overall attendance is " + String.format("%.0f%%", overall)
                    + " \u2014 below the 75% requirement. Please attend upcoming classes to improve it."));
        }

        subjectGrid.removeAll();
        if (courseIds.isEmpty()) {
            JLabel none = new JLabel("You are not enrolled in any courses yet. Contact your administrator.");
            none.setFont(Theme.SMALL);
            none.setForeground(Theme.TEXT_MUTED);
            subjectGrid.add(none);
        } else {
            for (String courseId : courseIds) {
                Course c = courseManager.getCourse(courseId);
                double pct = attendanceManager.calculatePercentage(student.getStudentId(), courseId);
                String name = c != null ? c.getCourseName() : courseId;
                subjectGrid.add(new MetricCard(name, String.format("%.0f%%", pct),
                        courseId, Theme.statusColor(pct), pct < 75 ? "\u26A0" : "\u2705"));
            }
        }

        recentHost.removeAll();
        JPanel recentList = new JPanel();
        recentList.setLayout(new BoxLayout(recentList, BoxLayout.Y_AXIS));
        recentList.setOpaque(false);
        List<Attendance> recent = allRecords.size() > 6 ? allRecords.subList(allRecords.size() - 6, allRecords.size()) : allRecords;
        if (recent.isEmpty()) {
            JLabel none = new JLabel("No attendance records yet.");
            none.setFont(Theme.SMALL);
            none.setForeground(Theme.TEXT_MUTED);
            recentList.add(none);
        } else {
            for (int i = recent.size() - 1; i >= 0; i--) {
                Attendance a = recent.get(i);
                Course c = courseManager.getCourse(a.getCourseId());
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(false);
                row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
                JLabel label = new JLabel(a.getDate() + " \u2013 " + (c != null ? c.getCourseName() : a.getCourseId()));
                label.setFont(Theme.SMALL);
                label.setForeground(Theme.TEXT_SECONDARY);
                row.add(label, BorderLayout.WEST);
                row.add(Badge.forAttendanceStatus(a.getStatus()), BorderLayout.EAST);
                recentList.add(row);
            }
        }
        recentHost.add(recentList, BorderLayout.NORTH);

        revalidate();
        repaint();
    }

    private JPanel warningBanner(String text) {
        RoundedPanel banner = new RoundedPanel(new BorderLayout());
        banner.background(Theme.WARNING_BG).borderColor(Theme.WARNING);
        banner.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel label = new JLabel("\u26A0  " + text);
        label.setFont(Theme.SMALL_BOLD);
        label.setForeground(Theme.WARNING);
        banner.add(label, BorderLayout.CENTER);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return banner;
    }

    private String nullToDash(String s) { return (s == null || s.isEmpty()) ? "\u2014" : s; }
}
