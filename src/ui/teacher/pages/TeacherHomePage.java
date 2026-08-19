package ui.teacher.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Attendance;
import model.Course;
import model.Student;
import model.Teacher;
import ui.components.*;
import ui.shell.MainFrame;
import ui.shell.Page;
import ui.theme.Theme;
import utils.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Teacher landing page: assigned courses, today's attendance tasks
 * (which courses still need attendance marked today), at-risk students
 * across their courses, recent attendance activity, and a fast "Mark
 * Attendance" shortcut.
 */
public class TeacherHomePage extends JPanel implements Page {

    private final CourseManager courseManager = new CourseManager();
    private final StudentManager studentManager = new StudentManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();

    private final Teacher teacher;
    private final MainFrame mainFrame;

    private final JPanel metricsRow = new JPanel(new GridLayout(1, 3, Theme.SPACE_MD, 0));
    private final JPanel tasksHost = new JPanel(new BorderLayout());
    private final JPanel riskHost = new JPanel(new BorderLayout());
    private final JPanel activityHost = new JPanel(new BorderLayout());

    public TeacherHomePage(MainFrame mainFrame, Teacher teacher) {
        this.mainFrame = mainFrame;
        this.teacher = teacher;
        setLayout(new BorderLayout(0, Theme.SPACE_MD));
        setOpaque(false);
        metricsRow.setOpaque(false);
        metricsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel middle = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        middle.setOpaque(false);
        middle.add(card("Today's Attendance Tasks", tasksHost));
        middle.add(card("At-Risk Students (below 75%)", riskHost));

        JPanel bottom = card("Recent Attendance Activity", activityHost);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        metricsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        middle.setAlignmentX(Component.LEFT_ALIGNMENT);
        middle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        stack.add(metricsRow);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(middle);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(bottom);

        add(stack, BorderLayout.CENTER);
    }

    private RoundedPanel card(String title, JPanel host) {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel t = new JLabel(title);
        t.setFont(Theme.H3);
        t.setForeground(Theme.TEXT_PRIMARY);
        card.add(t, BorderLayout.NORTH);
        host.setOpaque(false);
        card.add(new JScrollPane(host) {{ setBorder(null); getViewport().setOpaque(false); setOpaque(false); }}, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refresh() {
        if (teacher == null) return;
        List<Course> courses = courseManager.getCoursesByTeacher(teacher.getTeacherId());
        String today = DateUtil.today();

        int totalEnrolled = 0;
        for (Course c : courses) totalEnrolled += enrollmentManager.countEnrolledStudents(c.getCourseId());

        List<Attendance> allForTeacherCourses = new ArrayList<>();
        for (Course c : courses) allForTeacherCourses.addAll(attendanceManager.attendanceByCourse(c.getCourseId()));

        double avgPct = 0;
        if (!allForTeacherCourses.isEmpty()) {
            long present = allForTeacherCourses.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
            avgPct = present * 100.0 / allForTeacherCourses.size();
        }

        metricsRow.removeAll();
        metricsRow.add(new MetricCard("Assigned Courses", String.valueOf(courses.size()), null, Theme.PRIMARY, "\uD83D\uDCDA"));
        metricsRow.add(new MetricCard("Total Enrolled", String.valueOf(totalEnrolled), "across your courses", Theme.ACCENT_TEAL, "\uD83C\uDF93"));
        metricsRow.add(new MetricCard("Avg. Attendance", String.format("%.0f%%", avgPct), null, Theme.statusColor(avgPct), "\u2705"));
        metricsRow.revalidate();
        metricsRow.repaint();

        // Today's attendance tasks: courses where not every enrolled student has a record for today.
        JPanel tasksList = new JPanel();
        tasksList.setOpaque(false);
        tasksList.setLayout(new BoxLayout(tasksList, BoxLayout.Y_AXIS));
        boolean anyTask = false;
        for (Course c : courses) {
            int enrolled = enrollmentManager.countEnrolledStudents(c.getCourseId());
            long markedToday = attendanceManager.attendanceByDate(today, c.getCourseId()).size();
            if (enrolled > 0 && markedToday < enrolled) {
                anyTask = true;
                tasksList.add(taskRow(c, enrolled, (int) markedToday));
            }
        }
        if (!anyTask) {
            tasksList.add(mutedLabel("All caught up \u2014 attendance marked for today."));
        }
        tasksHost.removeAll();
        tasksHost.add(tasksList, BorderLayout.NORTH);

        // At-risk students across teacher's courses.
        JPanel riskList = new JPanel();
        riskList.setOpaque(false);
        riskList.setLayout(new BoxLayout(riskList, BoxLayout.Y_AXIS));
        Set<String> seen = new HashSet<>();
        boolean anyRisk = false;
        for (Course c : courses) {
            for (String studentId : enrollmentManager.getEnrolledStudentIds(c.getCourseId())) {
                if (!seen.add(studentId + "|" + c.getCourseId())) continue;
                double pct = attendanceManager.calculatePercentage(studentId, c.getCourseId());
                List<Attendance> recs = attendanceManager.attendanceByStudentAndCourse(studentId, c.getCourseId());
                if (!recs.isEmpty() && AttendanceManager.isAtRisk(pct)) {
                    anyRisk = true;
                    Student s = studentManager.getStudent(studentId);
                    riskList.add(riskRow(s != null ? s.getName() : studentId, c.getCourseName(), pct));
                }
            }
        }
        if (!anyRisk) riskList.add(mutedLabel("No at-risk students right now."));
        riskHost.removeAll();
        riskHost.add(riskList, BorderLayout.NORTH);

        // Recent activity.
        JPanel activityList = new JPanel();
        activityList.setOpaque(false);
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        List<Attendance> recent = allForTeacherCourses.size() > 8
                ? allForTeacherCourses.subList(allForTeacherCourses.size() - 8, allForTeacherCourses.size())
                : allForTeacherCourses;
        if (recent.isEmpty()) {
            activityList.add(mutedLabel("No attendance recorded yet."));
        } else {
            for (int i = recent.size() - 1; i >= 0; i--) {
                Attendance a = recent.get(i);
                Student s = studentManager.getStudent(a.getStudentId());
                JLabel row = new JLabel(a.getDate() + "  \u2013  " + (s != null ? s.getName() : a.getStudentId())
                        + " marked " + a.getStatus() + " in " + a.getCourseId());
                row.setFont(Theme.SMALL);
                row.setForeground(Theme.TEXT_SECONDARY);
                row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                activityList.add(row);
            }
        }
        activityHost.removeAll();
        activityHost.add(activityList, BorderLayout.NORTH);

        revalidate();
        repaint();
    }

    private JPanel taskRow(Course c, int enrolled, int marked) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        JLabel label = new JLabel(c.getCourseName() + " (" + c.getCourseId() + ") \u2013 " + marked + "/" + enrolled + " marked");
        label.setFont(Theme.SMALL_BOLD);
        label.setForeground(Theme.TEXT_PRIMARY);
        AppButton markBtn = new AppButton("Mark", AppButton.Variant.PRIMARY);
        markBtn.addActionListener(e -> mainFrame.showPage("mark"));
        row.add(label, BorderLayout.CENTER);
        row.add(markBtn, BorderLayout.EAST);
        return row;
    }

    private JPanel riskRow(String studentName, String courseName, double pct) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel label = new JLabel(studentName + " \u2013 " + courseName);
        label.setFont(Theme.SMALL);
        label.setForeground(Theme.TEXT_SECONDARY);
        row.add(label, BorderLayout.WEST);
        row.add(Badge.forPercentage(pct), BorderLayout.EAST);
        return row;
    }

    private JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.SMALL);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }
}
