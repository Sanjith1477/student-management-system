package ui.admin.pages;

import manager.CourseManager;
import manager.EnrollmentManager;
import manager.TeacherManager;
import model.Course;
import model.Teacher;
import ui.components.*;
import ui.dialogs.CourseFormDialog;
import ui.dialogs.EnrollStudentDialog;
import ui.shell.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Admin's Course Management: table of courses with teacher (via dropdown, not typed ID) and enrolled-student counts. */
public class CourseManagementPage extends JPanel implements Page {

    private final CourseManager courseManager = new CourseManager();
    private final TeacherManager teacherManager = new TeacherManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final TablePanel tablePanel;

    public CourseManagementPage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Course Management",
                new String[]{"Course ID", "Course Name", "Teacher", "Enrolled Students"},
                new int[]{0, 1, 2});

        AppButton addBtn = new AppButton("+ Create Course", AppButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> new CourseFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));

        AppButton editBtn = new AppButton("Edit", AppButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editSelected());

        AppButton enrollBtn = new AppButton("Enroll Students", AppButton.Variant.SECONDARY);
        enrollBtn.addActionListener(e -> enrollForSelected());

        AppButton deleteBtn = new AppButton("Delete", AppButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());

        tablePanel.addToolbarButton(enrollBtn);
        tablePanel.addToolbarButton(editBtn);
        tablePanel.addToolbarButton(deleteBtn);
        tablePanel.addToolbarButton(addBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private Course selectedCourse() {
        int row = tablePanel.selectedModelRow();
        if (row < 0) return null;
        String id = (String) tablePanel.getModel().getValueAt(row, 0);
        return courseManager.getCourse(id);
    }

    private void editSelected() {
        Course c = selectedCourse();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new CourseFormDialog(SwingUtilities.getWindowAncestor(this), c, this::refresh);
    }

    private void enrollForSelected() {
        Course c = selectedCourse();
        new EnrollStudentDialog(SwingUtilities.getWindowAncestor(this), c == null ? null : c.getCourseId(), this::refresh);
    }

    private void deleteSelected() {
        Course c = selectedCourse();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ConfirmDialog.confirmDelete(this, c.getCourseName() + " (" + c.getCourseId() + ")")) {
            courseManager.deleteCourse(c.getCourseId());
            refresh();
        }
    }

    @Override
    public void refresh() {
        List<Course> courses = courseManager.getCourses();
        Object[][] rows = new Object[courses.size()][];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            Teacher t = (c.getTeacherId() == null || c.getTeacherId().isEmpty()) ? null : teacherManager.getTeacher(c.getTeacherId());
            String teacherLabel = t != null ? t.getName() : "Unassigned";
            int enrolledCount = enrollmentManager.countEnrolledStudents(c.getCourseId());
            rows[i] = new Object[]{c.getCourseId(), c.getCourseName(), teacherLabel, enrolledCount};
        }
        tablePanel.setRows(rows);
    }
}
