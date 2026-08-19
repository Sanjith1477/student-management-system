package ui.admin.pages;

import manager.*;
import model.Attendance;
import model.Course;
import model.Student;
import ui.components.*;
import ui.shell.Page;
import ui.theme.Theme;
import utils.CsvExporter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/** Admin Reports: summary metric cards, simple charts, and a per-course attendance table with CSV export. */
public class ReportsPage extends JPanel implements Page {

    private final StudentManager studentManager = new StudentManager();
    private final CourseManager courseManager = new CourseManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();

    private final JPanel metricsRow = new JPanel(new GridLayout(1, 3, Theme.SPACE_MD, 0));
    private final JPanel chartHost = new JPanel(new BorderLayout());
    private final TablePanel tablePanel;

    public ReportsPage() {
        setLayout(new BorderLayout(0, Theme.SPACE_MD));
        setOpaque(false);
        metricsRow.setOpaque(false);
        metricsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        RoundedPanel chartCard = new RoundedPanel(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JLabel chartTitle = new JLabel("Attendance by Course");
        chartTitle.setFont(Theme.H3);
        chartTitle.setForeground(Theme.TEXT_PRIMARY);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chartHost, BorderLayout.CENTER);
        chartHost.setOpaque(false);
        chartCard.setPreferredSize(new Dimension(100, 220));

        tablePanel = new TablePanel("Per-Course Attendance Summary",
                new String[]{"Course ID", "Course Name", "Enrolled", "Sessions Recorded", "Attendance %"},
                new int[]{0, 1});
        AppButton exportBtn = new AppButton("Export CSV", AppButton.Variant.SECONDARY);
        exportBtn.addActionListener(e -> exportCsv());
        tablePanel.addToolbarButton(exportBtn);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        metricsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(metricsRow);
        top.add(Box.createVerticalStrut(Theme.SPACE_MD));
        top.add(chartCard);

        add(top, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void exportCsv() {
        DefaultListModel<Object> unused = null;
        List<String[]> rows = new java.util.ArrayList<>();
        var model = tablePanel.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String[] row = new String[model.getColumnCount()];
            for (int c = 0; c < model.getColumnCount(); c++) {
                row[c] = String.valueOf(model.getValueAt(i, c));
            }
            rows.add(row);
        }
        CsvExporter.export(this, "attendance_report.csv",
                new String[]{"Course ID", "Course Name", "Enrolled", "Sessions Recorded", "Attendance %"}, rows);
    }

    @Override
    public void refresh() {
        List<Student> students = studentManager.getAllStudents();
        List<Course> courses = courseManager.getCourses();
        List<Attendance> allAttendance = attendanceManager.getAttendance();

        double overallPct = 0;
        if (!allAttendance.isEmpty()) {
            long present = allAttendance.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
            overallPct = present * 100.0 / allAttendance.size();
        }
        long atRiskCount = students.stream()
                .filter(s -> AttendanceManager.isAtRisk(attendanceManager.calculatePercentage(s.getStudentId())))
                .filter(s -> !enrollmentManager.getEnrolledCourseIds(s.getStudentId()).isEmpty())
                .count();

        metricsRow.removeAll();
        metricsRow.add(new MetricCard("Overall Attendance", String.format("%.0f%%", overallPct),
                allAttendance.size() + " records", Theme.statusColor(overallPct), "\u2705"));
        metricsRow.add(new MetricCard("Total Courses", String.valueOf(courses.size()), null, Theme.INFO, "\uD83D\uDCDA"));
        metricsRow.add(new MetricCard("Students At Risk", String.valueOf(atRiskCount), "below 75% attendance", Theme.DANGER, "\u26A0"));
        metricsRow.revalidate();
        metricsRow.repaint();

        List<BarChartPanel.Bar> bars = new java.util.ArrayList<>();
        Color[] palette = {Theme.PRIMARY, Theme.ACCENT_TEAL, Theme.INFO, Theme.WARNING, Theme.SUCCESS, Theme.DANGER};
        int i = 0;
        Object[][] tableRows = new Object[courses.size()][];
        for (Course c : courses) {
            List<Attendance> courseAttendance = attendanceManager.attendanceByCourse(c.getCourseId());
            double pct = 0;
            if (!courseAttendance.isEmpty()) {
                long present = courseAttendance.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
                pct = present * 100.0 / courseAttendance.size();
            }
            bars.add(new BarChartPanel.Bar(c.getCourseId(), pct, palette[i % palette.length]));
            int enrolled = enrollmentManager.countEnrolledStudents(c.getCourseId());
            tableRows[i] = new Object[]{c.getCourseId(), c.getCourseName(), enrolled, courseAttendance.size(),
                    String.format("%.0f%%", pct)};
            i++;
        }
        chartHost.removeAll();
        BarChartPanel chart = new BarChartPanel(bars, 100);
        chart.setValueSuffix("%");
        chartHost.add(chart, BorderLayout.CENTER);
        chartHost.revalidate();
        chartHost.repaint();

        tablePanel.setRows(tableRows);
    }
}
