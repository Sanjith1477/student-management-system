package ui.teacher.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import manager.StudentManager;
import model.Attendance;
import model.Course;
import model.Student;
import model.Teacher;
import ui.components.AppButton;
import ui.components.TablePanel;
import ui.shell.Page;
import ui.theme.Theme;
import utils.CsvExporter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance History for teachers. Fixes the original data-integrity gap:
 * the course filter is a dropdown limited to courses this teacher is
 * actually assigned to (via CourseManager.isTeacherAssigned), so a
 * teacher can never browse another teacher's attendance records.
 */
public class AttendanceHistoryPage extends JPanel implements Page {

    private final CourseManager courseManager = new CourseManager();
    private final StudentManager studentManager = new StudentManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final Teacher teacher;

    private final JComboBox<CourseOption> courseFilter = new JComboBox<>();
    private final JTextField dateFilter = new JTextField();
    private final TablePanel tablePanel;

    public AttendanceHistoryPage(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Attendance History",
                new String[]{"Date", "Course", "Student ID", "Student Name", "Status"},
                new int[]{0, 1, 2, 3});

        courseFilter.setPreferredSize(new Dimension(220, 30));
        courseFilter.addActionListener(e -> applyFilters());
        dateFilter.setPreferredSize(new Dimension(120, 30));
        dateFilter.putClientProperty("JTextField.placeholderText", "yyyy-MM-dd");
        dateFilter.getDocument().addDocumentListener((SimpleDocListener) this::applyFilters);

        tablePanel.addFilterComponent(labeled("Course:", courseFilter));
        tablePanel.addFilterComponent(labeled("Date:", dateFilter));

        AppButton exportBtn = new AppButton("Export CSV", AppButton.Variant.SECONDARY);
        exportBtn.addActionListener(e -> exportCsv());
        tablePanel.addToolbarButton(exportBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Theme.SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        p.add(l);
        p.add(field);
        return p;
    }

    private void applyFilters() {
        if (teacher == null) return;
        CourseOption sel = (CourseOption) courseFilter.getSelectedItem();
        String date = dateFilter.getText().trim();

        List<Course> teacherCourses = courseManager.getCoursesByTeacher(teacher.getTeacherId());
        List<Object[]> rows = new ArrayList<>();
        for (Course c : teacherCourses) {
            if (sel != null && !sel.id.equals("ALL") && !sel.id.equals(c.getCourseId())) continue;
            // Defense in depth: re-verify assignment even though the dropdown already only lists own courses.
            if (!courseManager.isTeacherAssigned(c.getCourseId(), teacher.getTeacherId())) continue;
            List<Attendance> records = attendanceManager.attendanceByDate(date, c.getCourseId());
            for (Attendance a : records) {
                Student s = studentManager.getStudent(a.getStudentId());
                rows.add(new Object[]{a.getDate(), c.getCourseName(), a.getStudentId(),
                        s != null ? s.getName() : "(unknown)", a.getStatus()});
            }
        }
        tablePanel.setRows(rows.toArray(new Object[0][]));
    }

    private void exportCsv() {
        var model = tablePanel.getModel();
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String[] row = new String[model.getColumnCount()];
            for (int c = 0; c < model.getColumnCount(); c++) row[c] = String.valueOf(model.getValueAt(i, c));
            rows.add(row);
        }
        CsvExporter.export(this, "attendance_history.csv",
                new String[]{"Date", "Course", "Student ID", "Student Name", "Status"}, rows);
    }

    @Override
    public void refresh() {
        if (teacher == null) return;
        courseFilter.removeAllItems();
        courseFilter.addItem(new CourseOption("ALL", "All my courses"));
        for (Course c : courseManager.getCoursesByTeacher(teacher.getTeacherId())) {
            courseFilter.addItem(new CourseOption(c.getCourseId(), c.getCourseName() + " (" + c.getCourseId() + ")"));
        }
        applyFilters();
    }

    private static class CourseOption {
        final String id;
        final String label;
        CourseOption(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    /** Small functional adapter so a lambda can be used as a DocumentListener for all three events. */
    private interface SimpleDocListener extends javax.swing.event.DocumentListener {
        void update();
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
}
