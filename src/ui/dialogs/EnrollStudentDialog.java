package ui.dialogs;

import manager.CourseManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Course;
import model.Student;
import ui.components.FormDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Dialog for enrolling a student into a course for a given semester. */
public class EnrollStudentDialog extends FormDialog {

    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final Runnable onSuccess;

    private final JComboBox<Option> studentCombo = new JComboBox<>();
    private final JComboBox<Option> courseCombo = new JComboBox<>();
    private final JTextField semesterField = new JTextField("Fall 2026");

    public EnrollStudentDialog(Window owner, Runnable onSuccess) {
        super(owner, "Enroll Student", "Add a student to a course.");
        this.onSuccess = onSuccess;

        List<Student> students = new StudentManager().getAllStudents();
        for (Student s : students) {
            studentCombo.addItem(new Option(s.getStudentId(), s.getName() + " (" + s.getStudentId() + ")"));
        }
        List<Course> courses = new CourseManager().getCourses();
        for (Course c : courses) {
            courseCombo.addItem(new Option(c.getCourseId(), c.getCourseName() + " (" + c.getCourseId() + ")"));
        }

        addField("Student", studentCombo);
        addField("Course", courseCombo);
        addField("Semester", semesterField);

        finish(owner);
    }

    public EnrollStudentDialog(Window owner, String presetCourseId, Runnable onSuccess) {
        this(owner, onSuccess);
        if (presetCourseId != null) {
            for (int i = 0; i < courseCombo.getItemCount(); i++) {
                if (courseCombo.getItemAt(i).id.equals(presetCourseId)) {
                    courseCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    protected String saveLabel() { return "Enroll"; }

    @Override
    protected void onSave() {
        Option student = (Option) studentCombo.getSelectedItem();
        Option course = (Option) courseCombo.getSelectedItem();
        String semester = semesterField.getText().trim();

        if (student == null || course == null) {
            showError("Please select both a student and a course.");
            return;
        }
        if (semester.isEmpty()) {
            showError("Semester is required.");
            return;
        }
        boolean ok = enrollmentManager.enroll(student.id, course.id, semester);
        if (!ok) {
            showError("This student is already enrolled in that course.");
            return;
        }
        onSuccess.run();
        dispose();
    }

    private static class Option {
        final String id;
        final String label;
        Option(String id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
