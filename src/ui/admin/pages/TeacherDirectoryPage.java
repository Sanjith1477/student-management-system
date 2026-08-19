package ui.admin.pages;

import manager.CourseManager;
import manager.TeacherManager;
import model.Course;
import model.Teacher;
import ui.components.*;
import ui.dialogs.TeacherFormDialog;
import ui.shell.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Admin's Teacher Directory: searchable/sortable table with course-load info and CRUD actions. */
public class TeacherDirectoryPage extends JPanel implements Page {

    private final TeacherManager teacherManager = new TeacherManager();
    private final CourseManager courseManager = new CourseManager();
    private final TablePanel tablePanel;

    public TeacherDirectoryPage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Teacher Directory",
                new String[]{"ID", "Name", "Department", "Email", "Assigned Courses"},
                new int[]{0, 1, 2});

        AppButton addBtn = new AppButton("+ Add Teacher", AppButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> new TeacherFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));

        AppButton editBtn = new AppButton("Edit", AppButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editSelected());

        AppButton deleteBtn = new AppButton("Delete", AppButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());

        tablePanel.addToolbarButton(editBtn);
        tablePanel.addToolbarButton(deleteBtn);
        tablePanel.addToolbarButton(addBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private Teacher selectedTeacher() {
        int row = tablePanel.selectedModelRow();
        if (row < 0) return null;
        String id = (String) tablePanel.getModel().getValueAt(row, 0);
        return teacherManager.getTeacher(id);
    }

    private void editSelected() {
        Teacher t = selectedTeacher();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Select a teacher first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new TeacherFormDialog(SwingUtilities.getWindowAncestor(this), t, this::refresh);
    }

    private void deleteSelected() {
        Teacher t = selectedTeacher();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Select a teacher first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ConfirmDialog.confirmDelete(this, t.getName() + " (" + t.getTeacherId() + ")")) {
            teacherManager.deleteTeacher(t.getTeacherId());
            refresh();
        }
    }

    @Override
    public void refresh() {
        List<Teacher> teachers = teacherManager.getAllTeachers();
        List<Course> courses = courseManager.getCourses();

        Object[][] rows = new Object[teachers.size()][];
        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            StringBuilder courseNames = new StringBuilder();
            for (Course c : courses) {
                if (c.getTeacherId() != null && c.getTeacherId().equals(t.getTeacherId())) {
                    if (courseNames.length() > 0) courseNames.append(", ");
                    courseNames.append(c.getCourseName());
                }
            }
            rows[i] = new Object[]{
                    t.getTeacherId(), t.getName(), t.getDepartment(), t.getEmail(),
                    courseNames.length() == 0 ? "\u2014" : courseNames.toString()
            };
        }
        tablePanel.setRows(rows);
    }
}
