package ui.admin.pages;

import manager.*;
import model.Attendance;
import model.Course;
import model.Student;
import ui.components.*;
import ui.dialogs.CourseFormDialog;
import ui.dialogs.EnrollStudentDialog;
import ui.dialogs.StudentFormDialog;
import ui.dialogs.TeacherFormDialog;
import ui.shell.MainFrame;
import ui.shell.Page;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Admin landing page: metric cards, an attendance trend chart, a
 * department breakdown chart, quick actions, and recent activity --
 * gives the admin an at-a-glance view instead of a bare menu.
 */
public class AdminHomePage extends JPanel implements Page {

    private final StudentManager studentManager = new StudentManager();
    private final TeacherManager teacherManager = new TeacherManager();
    private final CourseManager courseManager = new CourseManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();

    private final JPanel metricsRow = new JPanel(new GridLayout(1, 4, Theme.SPACE_MD, 0));
    private final JPanel chartsRow = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
    private final JPanel activityCard;
    private final DefaultListModel<String> activityModel = new DefaultListModel<>();
    private final MainFrame mainFrame;

    public AdminHomePage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, Theme.SPACE_MD));
        setOpaque(false);

        metricsRow.setOpaque(false);
        chartsRow.setOpaque(false);

        JPanel quickActions = buildQuickActions();

        activityCard = buildActivityCard();

        JPanel middle = new JPanel(new BorderLayout(0, Theme.SPACE_MD));
        middle.setOpaque(false);
        middle.add(chartsRow, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        bottom.setOpaque(false);
        bottom.add(quickActions);
        bottom.add(activityCard);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        metricsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        middle.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        metricsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        middle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        stack.add(metricsRow);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(middle);
        stack.add(Box.createVerticalStrut(Theme.SPACE_MD));
        stack.add(bottom);

        add(stack, BorderLayout.CENTER);
    }

    private JPanel buildQuickActions() {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel title = new JLabel("Quick Actions");
        title.setFont(Theme.H3);
        title.setForeground(Theme.TEXT_PRIMARY);

        JPanel grid = new JPanel(new GridLayout(2, 2, Theme.SPACE_SM, Theme.SPACE_SM));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, 0, 0, 0));

        AppButton addStudent = new AppButton("+ Add Student", AppButton.Variant.PRIMARY);
        addStudent.addActionListener(e -> new StudentFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));
        AppButton addTeacher = new AppButton("+ Add Teacher", AppButton.Variant.SECONDARY);
        addTeacher.addActionListener(e -> new TeacherFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));
        AppButton addCourse = new AppButton("+ Create Course", AppButton.Variant.SECONDARY);
        addCourse.addActionListener(e -> new CourseFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));
        AppButton enroll = new AppButton("+ Enroll Students", AppButton.Variant.SECONDARY);
        enroll.addActionListener(e -> new EnrollStudentDialog(SwingUtilities.getWindowAncestor(this), this::refresh));

        grid.add(addStudent);
        grid.add(addTeacher);
        grid.add(addCourse);
        grid.add(enroll);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityCard() {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel title = new JLabel("Recent Activity");
        title.setFont(Theme.H3);
        title.setForeground(Theme.TEXT_PRIMARY);

        JList<String> list = new JList<>(activityModel);
        list.setFont(Theme.SMALL);
        list.setFixedCellHeight(24);
        list.setBackground(Theme.CARD_BG);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);

        card.add(title, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refresh() {
        List<Student> students = studentManager.getAllStudents();
        int teacherCount = teacherManager.getAllTeachers().size();
        int courseCount = courseManager.getCourses().size();
        List<Attendance> allAttendance = attendanceManager.getAttendance();

        double overallPct = overallAttendancePercentage(allAttendance);

        metricsRow.removeAll();
        metricsRow.add(new MetricCard("Total Students", String.valueOf(students.size()), null, Theme.PRIMARY, "\uD83C\uDF93"));
        metricsRow.add(new MetricCard("Total Teachers", String.valueOf(teacherCount), null, Theme.ACCENT_TEAL, "\uD83D\uDC69\u200D\uD83C\uDFEB"));
        metricsRow.add(new MetricCard("Total Courses", String.valueOf(courseCount), null, Theme.INFO, "\uD83D\uDCDA"));
        metricsRow.add(new MetricCard("Overall Attendance", String.format("%.0f%%", overallPct),
                allAttendance.size() + " records", Theme.statusColor(overallPct), "\u2705"));
        metricsRow.revalidate();
        metricsRow.repaint();

        chartsRow.removeAll();
        chartsRow.add(wrapChart("Attendance Trend (last 7 sessions)", buildTrendChart(allAttendance)));
        chartsRow.add(wrapChart("Students by Department", buildDepartmentChart(students)));
        chartsRow.revalidate();
        chartsRow.repaint();

        activityModel.clear();
        List<Student> recentStudents = lastN(students, 3);
        for (int i = recentStudents.size() - 1; i >= 0; i--) {
            Student s = recentStudents.get(i);
            activityModel.addElement("\u2022 New student registered: " + s.getName() + " (" + s.getStudentId() + ")");
        }
        List<Attendance> recentAttendance = lastN(allAttendance, 5);
        for (int i = recentAttendance.size() - 1; i >= 0; i--) {
            Attendance a = recentAttendance.get(i);
            activityModel.addElement("\u2022 " + a.getDate() + " \u2013 " + a.getStudentId() + " marked " + a.getStatus() + " (" + a.getCourseId() + ")");
        }
        if (activityModel.isEmpty()) {
            activityModel.addElement("No activity yet. Add students and mark attendance to see updates here.");
        }
    }

    private JPanel wrapChart(String title, JComponent chart) {
        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel t = new JLabel(title);
        t.setFont(Theme.H3);
        t.setForeground(Theme.TEXT_PRIMARY);
        card.add(t, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private BarChartPanel buildTrendChart(List<Attendance> all) {
        Map<String, int[]> byDate = new TreeMap<>(); // date -> [present, total]
        for (Attendance a : all) {
            int[] counts = byDate.computeIfAbsent(a.getDate(), k -> new int[2]);
            counts[1]++;
            if (a.getStatus().equalsIgnoreCase("Present")) counts[0]++;
        }
        List<String> dates = new ArrayList<>(byDate.keySet());
        List<String> lastDates = dates.size() > 7 ? dates.subList(dates.size() - 7, dates.size()) : dates;
        List<BarChartPanel.Bar> bars = new ArrayList<>();
        for (String date : lastDates) {
            int[] c = byDate.get(date);
            double pct = c[1] == 0 ? 0 : (c[0] * 100.0 / c[1]);
            bars.add(new BarChartPanel.Bar(date.length() >= 5 ? date.substring(5) : date, pct, Theme.PRIMARY));
        }
        BarChartPanel chart = new BarChartPanel(bars, 100);
        chart.setValueSuffix("%");
        return chart;
    }

    private BarChartPanel buildDepartmentChart(List<Student> students) {
        Map<String, Integer> byDept = new TreeMap<>();
        for (Student s : students) {
            String dept = (s.getDepartment() == null || s.getDepartment().isEmpty()) ? "Unspecified" : s.getDepartment();
            byDept.merge(dept, 1, Integer::sum);
        }
        List<BarChartPanel.Bar> bars = new ArrayList<>();
        double max = 1;
        Color[] palette = {Theme.PRIMARY, Theme.ACCENT_TEAL, Theme.INFO, Theme.WARNING, Theme.SUCCESS};
        int i = 0;
        for (Map.Entry<String, Integer> e : byDept.entrySet()) {
            bars.add(new BarChartPanel.Bar(e.getKey(), e.getValue(), palette[i % palette.length]));
            max = Math.max(max, e.getValue());
            i++;
        }
        return new BarChartPanel(bars, max);
    }

    private double overallAttendancePercentage(List<Attendance> all) {
        if (all.isEmpty()) return 0;
        long present = all.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
        return present * 100.0 / all.size();
    }

    private <T> List<T> lastN(List<T> list, int n) {
        if (list.size() <= n) return list;
        return list.subList(list.size() - n, list.size());
    }
}
