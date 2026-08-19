package ui.teacher.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Attendance;
import model.Course;
import model.Student;
import model.Teacher;
import ui.components.AppButton;
import ui.components.RoundedPanel;
import ui.shell.Page;
import ui.theme.Theme;
import utils.DateUtil;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mark Attendance page. Fixes the critical bug in the original app: only
 * students actively enrolled in the selected course (via EnrollmentManager)
 * are listed -- attendance is never marked for the whole student body.
 */
public class MarkAttendancePage extends JPanel implements Page {

    private final CourseManager courseManager = new CourseManager();
    private final StudentManager studentManager = new StudentManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final Teacher teacher;

    private final JComboBox<CourseOption> courseCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField(DateUtil.today());
    private final JPanel studentListPanel = new JPanel();
    private final Map<String, JCheckBox> presentBoxes = new HashMap<>();
    private final JLabel emptyLabel = new JLabel("This course has no enrolled students yet. Enroll students from the admin Enrollments page first.");

    public MarkAttendancePage(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout(0, Theme.SPACE_MD));
        setOpaque(false);

        RoundedPanel top = new RoundedPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACE_MD, 0));
        controls.setOpaque(false);

        courseCombo.setPreferredSize(new Dimension(260, 32));
        dateField.setPreferredSize(new Dimension(120, 32));
        dateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STRONG, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        AppButton loadBtn = new AppButton("Load Students", AppButton.Variant.SECONDARY);
        loadBtn.addActionListener(e -> loadStudents());

        controls.add(labeled("Course", courseCombo));
        controls.add(labeled("Date (yyyy-MM-dd)", dateField));
        controls.add(loadBtn);
        top.add(controls, BorderLayout.CENTER);

        studentListPanel.setLayout(new BoxLayout(studentListPanel, BoxLayout.Y_AXIS));
        studentListPanel.setOpaque(false);
        RoundedPanel listCard = new RoundedPanel(new BorderLayout());
        listCard.setBorder(BorderFactory.createEmptyBorder(Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD, Theme.SPACE_MD));
        JScrollPane scroll = new JScrollPane(studentListPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        listCard.add(scroll, BorderLayout.CENTER);

        emptyLabel.setFont(Theme.SMALL);
        emptyLabel.setForeground(Theme.TEXT_MUTED);

        AppButton saveBtn = new AppButton("Save Attendance", AppButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> saveAttendance());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(saveBtn);

        add(top, BorderLayout.NORTH);
        add(listCard, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(Theme.TINY);
        l.setForeground(Theme.TEXT_MUTED);
        p.add(l);
        p.add(field);
        return p;
    }

    private String selectedCourseId() {
        CourseOption sel = (CourseOption) courseCombo.getSelectedItem();
        return sel == null ? null : sel.id;
    }

    private void loadStudents() {
        studentListPanel.removeAll();
        presentBoxes.clear();

        String courseId = selectedCourseId();
        if (courseId == null) {
            JOptionPane.showMessageDialog(this, "No courses assigned to you.");
            return;
        }

        List<String> enrolledIds = enrollmentManager.getEnrolledStudentIds(courseId);
        if (enrolledIds.isEmpty()) {
            studentListPanel.add(emptyLabel);
        } else {
            for (String studentId : enrolledIds) {
                Student s = studentManager.getStudent(studentId);
                String label = s != null ? (s.getStudentId() + " \u2013 " + s.getName()) : studentId;
                JCheckBox cb = new JCheckBox(label, true);
                cb.setOpaque(false);
                cb.setFont(Theme.BODY);
                presentBoxes.put(studentId, cb);
                studentListPanel.add(cb);
            }
        }
        studentListPanel.revalidate();
        studentListPanel.repaint();
    }

    private void saveAttendance() {
        String courseId = selectedCourseId();
        String date = dateField.getText().trim();

        if (courseId == null || Validator.isEmpty(date)) {
            JOptionPane.showMessageDialog(this, "Please select a course and enter a date.");
            return;
        }
        if (!DateUtil.isValidDate(date)) {
            JOptionPane.showMessageDialog(this, "Date must be in yyyy-MM-dd format.",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (presentBoxes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enrolled students to mark for this course.");
            return;
        }
        for (Map.Entry<String, JCheckBox> entry : presentBoxes.entrySet()) {
            String status = entry.getValue().isSelected() ? "Present" : "Absent";
            attendanceManager.markAttendance(new Attendance(date, courseId, entry.getKey(), status));
        }
        JOptionPane.showMessageDialog(this, "Attendance saved for " + date + ".");
    }

    @Override
    public void refresh() {
        if (teacher == null) return;
        CourseOption previouslySelected = (CourseOption) courseCombo.getSelectedItem();
        courseCombo.removeAllItems();
        List<Course> courses = courseManager.getCoursesByTeacher(teacher.getTeacherId());
        for (Course c : courses) {
            courseCombo.addItem(new CourseOption(c.getCourseId(), c.getCourseName() + " (" + c.getCourseId() + ")"));
        }
        if (courses.isEmpty()) {
            studentListPanel.removeAll();
            studentListPanel.add(emptyLabel);
        } else {
            dateField.setText(DateUtil.today());
            loadStudents();
        }
    }

    private static class CourseOption {
        final String id;
        final String label;
        CourseOption(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
