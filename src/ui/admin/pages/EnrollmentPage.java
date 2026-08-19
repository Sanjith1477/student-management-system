package ui.admin.pages;

import manager.CourseManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Course;
import model.Enrollment;
import model.Student;
import ui.components.*;
import ui.dialogs.EnrollStudentDialog;
import ui.shell.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Admin page for enrolling/removing students from courses and viewing all enrollments. */
public class EnrollmentPage extends JPanel implements Page {

    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final StudentManager studentManager = new StudentManager();
    private final CourseManager courseManager = new CourseManager();
    private final TablePanel tablePanel;

    public EnrollmentPage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Enrollments",
                new String[]{"Student ID", "Student Name", "Course ID", "Course Name", "Semester", "Status"},
                new int[]{0, 1, 2, 3});

        AppButton enrollBtn = new AppButton("+ Enroll Student", AppButton.Variant.PRIMARY);
        enrollBtn.addActionListener(e -> new EnrollStudentDialog(SwingUtilities.getWindowAncestor(this), this::refresh));

        AppButton removeBtn = new AppButton("Remove", AppButton.Variant.DANGER);
        removeBtn.addActionListener(e -> removeSelected());

        tablePanel.addToolbarButton(removeBtn);
        tablePanel.addToolbarButton(enrollBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private void removeSelected() {
        int row = tablePanel.selectedModelRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an enrollment first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String studentId = (String) tablePanel.getModel().getValueAt(row, 0);
        String courseId = (String) tablePanel.getModel().getValueAt(row, 2);
        String studentName = (String) tablePanel.getModel().getValueAt(row, 1);
        String courseName = (String) tablePanel.getModel().getValueAt(row, 3);

        if (ConfirmDialog.confirmDelete(this, studentName + "'s enrollment in " + courseName)) {
            enrollmentManager.unenroll(studentId, courseId);
            refresh();
        }
    }

    @Override
    public void refresh() {
        List<Enrollment> enrollments = enrollmentManager.getAllEnrollments();
        Object[][] rows = new Object[enrollments.size()][];
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment en = enrollments.get(i);
            Student s = studentManager.getStudent(en.getStudentId());
            Course c = courseManager.getCourse(en.getCourseId());
            rows[i] = new Object[]{
                    en.getStudentId(),
                    s != null ? s.getName() : "(deleted)",
                    en.getCourseId(),
                    c != null ? c.getCourseName() : "(deleted)",
                    en.getSemester(),
                    en.getStatus()
            };
        }
        tablePanel.setRows(rows);
    }
}
