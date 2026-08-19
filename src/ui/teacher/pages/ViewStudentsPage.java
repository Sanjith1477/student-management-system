package ui.teacher.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Course;
import model.Student;
import model.Teacher;
import ui.components.TablePanel;
import ui.shell.Page;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Shows the teacher's assigned courses and, for a selected course, only
 * the students actually enrolled in it -- not the entire student body.
 */
public class ViewStudentsPage extends JPanel implements Page {

    private final CourseManager courseManager = new CourseManager();
    private final StudentManager studentManager = new StudentManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final Teacher teacher;

    private final JComboBox<CourseOption> courseCombo = new JComboBox<>();
    private final TablePanel tablePanel;

    public ViewStudentsPage(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Enrolled Students", new String[]{"ID", "Name", "Department", "Year", "Section", "Attendance %"}, new int[]{0, 1});
        courseCombo.setPreferredSize(new Dimension(260, 30));
        courseCombo.addActionListener(e -> loadStudents());
        tablePanel.addFilterComponent(labeled("Course:", courseCombo));

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

    private void loadStudents() {
        CourseOption sel = (CourseOption) courseCombo.getSelectedItem();
        if (sel == null) {
            tablePanel.setRows(new Object[0][]);
            return;
        }
        List<String> enrolledIds = enrollmentManager.getEnrolledStudentIds(sel.id);
        Object[][] rows = new Object[enrolledIds.size()][];
        for (int i = 0; i < enrolledIds.size(); i++) {
            Student s = studentManager.getStudent(enrolledIds.get(i));
            if (s == null) continue;
            double pct = attendanceManager.calculatePercentage(s.getStudentId(), sel.id);
            rows[i] = new Object[]{s.getStudentId(), s.getName(), s.getDepartment(), s.getYear(), s.getSection(),
                    String.format("%.0f%%", pct)};
        }
        tablePanel.setRows(rows);
    }

    @Override
    public void refresh() {
        if (teacher == null) return;
        courseCombo.removeAllItems();
        List<Course> courses = courseManager.getCoursesByTeacher(teacher.getTeacherId());
        for (Course c : courses) {
            courseCombo.addItem(new CourseOption(c.getCourseId(), c.getCourseName() + " (" + c.getCourseId() + ")"));
        }
        loadStudents();
    }

    private static class CourseOption {
        final String id;
        final String label;
        CourseOption(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
