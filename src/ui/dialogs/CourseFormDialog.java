package ui.dialogs;

import manager.CourseManager;
import manager.TeacherManager;
import model.Course;
import model.Teacher;
import ui.components.FormDialog;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Single dialog used for both creating and editing a Course. Teachers
 * are chosen from a dropdown built from TeacherManager instead of being
 * typed in as a raw ID.
 */
public class CourseFormDialog extends FormDialog {

    private final CourseManager courseManager = new CourseManager();
    private final Course editing;
    private final Runnable onSuccess;

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JComboBox<TeacherOption> teacherCombo = new JComboBox<>();

    public CourseFormDialog(Window owner, Course editing, Runnable onSuccess) {
        super(owner, editing == null ? "Add Course" : "Edit Course",
                editing == null ? "Create a new course and assign a teacher." : "Update this course's details.");
        this.editing = editing;
        this.onSuccess = onSuccess;

        List<Teacher> teachers = new TeacherManager().getAllTeachers();
        teacherCombo.addItem(new TeacherOption("", "Unassigned"));
        for (Teacher t : teachers) {
            teacherCombo.addItem(new TeacherOption(t.getTeacherId(), t.getName() + " (" + t.getTeacherId() + ")"));
        }

        addField("Course ID", idField);
        addField("Course Name", nameField);
        addField("Teacher", teacherCombo);

        if (editing != null) {
            idField.setText(editing.getCourseId());
            idField.setEditable(false);
            idField.setBackground(Theme.NEUTRAL_BG);
            nameField.setText(editing.getCourseName());
            selectTeacher(editing.getTeacherId());
        }

        finish(owner);
    }

    private void selectTeacher(String teacherId) {
        for (int i = 0; i < teacherCombo.getItemCount(); i++) {
            TeacherOption opt = teacherCombo.getItemAt(i);
            if (opt.id.equals(teacherId)) {
                teacherCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    @Override
    protected void onSave() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        TeacherOption teacherOpt = (TeacherOption) teacherCombo.getSelectedItem();
        String teacherId = teacherOpt == null ? "" : teacherOpt.id;

        if (id.isEmpty() || name.isEmpty()) {
            showError("Course ID and Course Name are required.");
            return;
        }

        Course course = new Course(id, name, teacherId);

        if (editing == null) {
            boolean ok = courseManager.addCourse(course);
            if (!ok) {
                showError("A course with this ID already exists.");
                return;
            }
        } else {
            boolean ok = courseManager.editCourse(course);
            if (!ok) {
                showError("Could not update this course.");
                return;
            }
        }
        onSuccess.run();
        dispose();
    }

    private static class TeacherOption {
        final String id;
        final String label;
        TeacherOption(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
