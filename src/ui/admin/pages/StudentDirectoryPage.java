package ui.admin.pages;

import manager.AttendanceManager;
import manager.EnrollmentManager;
import manager.StudentManager;
import model.Course;
import model.Student;
import manager.CourseManager;
import ui.components.*;
import ui.dialogs.EnrollStudentDialog;
import ui.dialogs.StudentFormDialog;
import ui.shell.Page;
import ui.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Admin's Student Directory: searchable/filterable/sortable table with
 * ID, name, department, year, enrolled courses and attendance %, plus
 * Add/Edit/Delete/Enroll actions on the selected row.
 */
public class StudentDirectoryPage extends JPanel implements Page {

    private final StudentManager studentManager = new StudentManager();
    private final EnrollmentManager enrollmentManager = new EnrollmentManager();
    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final CourseManager courseManager = new CourseManager();

    private final TablePanel tablePanel;
    private final JComboBox<String> deptFilter = new JComboBox<>();
    private final JComboBox<String> yearFilter = new JComboBox<>();

    public StudentDirectoryPage() {
        setLayout(new BorderLayout());
        setOpaque(false);

        tablePanel = new TablePanel("Student Directory",
                new String[]{"ID", "Name", "Department", "Year", "Enrolled Courses", "Attendance %", "Status"},
                new int[]{0, 1, 2});

        deptFilter.addActionListener(e -> applyFilters());
        yearFilter.addActionListener(e -> applyFilters());
        tablePanel.addFilterComponent(labeled("Dept:", deptFilter));
        tablePanel.addFilterComponent(labeled("Year:", yearFilter));

        AppButton addBtn = new AppButton("+ Add Student", AppButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> new StudentFormDialog(SwingUtilities.getWindowAncestor(this), null, this::refresh));

        AppButton editBtn = new AppButton("Edit", AppButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editSelected());

        AppButton enrollBtn = new AppButton("Enroll", AppButton.Variant.SECONDARY);
        enrollBtn.addActionListener(e -> new EnrollStudentDialog(SwingUtilities.getWindowAncestor(this), this::refresh));

        AppButton deleteBtn = new AppButton("Delete", AppButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());

        tablePanel.addToolbarButton(enrollBtn);
        tablePanel.addToolbarButton(editBtn);
        tablePanel.addToolbarButton(deleteBtn);
        tablePanel.addToolbarButton(addBtn);

        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Theme.SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        field.setPreferredSize(new Dimension(110, 30));
        p.add(l);
        p.add(field);
        return p;
    }

    private Student selectedStudent() {
        int row = tablePanel.selectedModelRow();
        if (row < 0) return null;
        String id = (String) tablePanel.getModel().getValueAt(row, 0);
        return studentManager.getStudent(id);
    }

    private void editSelected() {
        Student s = selectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        new StudentFormDialog(SwingUtilities.getWindowAncestor(this), s, this::refresh);
    }

    private void deleteSelected() {
        Student s = selectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Select a student first.", "No selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ConfirmDialog.confirmDelete(this, s.getName() + " (" + s.getStudentId() + ")")) {
            studentManager.deleteStudent(s.getStudentId());
            refresh();
        }
    }

    @Override
    public void refresh() {
        List<Student> students = studentManager.getAllStudents();

        String prevDept = (String) deptFilter.getSelectedItem();
        String prevYear = (String) yearFilter.getSelectedItem();
        deptFilter.removeAllItems();
        yearFilter.removeAllItems();
        deptFilter.addItem("All");
        yearFilter.addItem("All");
        java.util.LinkedHashSet<String> depts = new java.util.LinkedHashSet<>();
        java.util.LinkedHashSet<String> years = new java.util.LinkedHashSet<>();
        for (Student s : students) {
            if (s.getDepartment() != null && !s.getDepartment().isEmpty()) depts.add(s.getDepartment());
            if (s.getYear() != null && !s.getYear().isEmpty()) years.add(s.getYear());
        }
        for (String d : depts) deptFilter.addItem(d);
        for (String y : years) yearFilter.addItem(y);
        if (prevDept != null) deptFilter.setSelectedItem(prevDept);
        if (prevYear != null) yearFilter.setSelectedItem(prevYear);

        applyFilters();
    }

    private void applyFilters() {
        List<Student> students = studentManager.getAllStudents();
        String dept = (String) deptFilter.getSelectedItem();
        String year = (String) yearFilter.getSelectedItem();

        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (Student s : students) {
            if (dept != null && !dept.equals("All") && !dept.equals(s.getDepartment())) continue;
            if (year != null && !year.equals("All") && !year.equals(s.getYear())) continue;

            List<String> courseIds = enrollmentManager.getEnrolledCourseIds(s.getStudentId());
            StringBuilder courseNames = new StringBuilder();
            for (String cid : courseIds) {
                Course c = courseManager.getCourse(cid);
                if (courseNames.length() > 0) courseNames.append(", ");
                courseNames.append(c != null ? c.getCourseName() : cid);
            }
            double pct = attendanceManager.calculatePercentage(s.getStudentId());

            rows.add(new Object[]{
                    s.getStudentId(), s.getName(), s.getDepartment(), s.getYear(),
                    courseNames.length() == 0 ? "\u2014" : courseNames.toString(),
                    pct, AttendanceManager.isAtRisk(pct) && !courseIds.isEmpty() ? "At Risk" : "OK"
            });
        }
        tablePanel.setRows(rows.toArray(new Object[0][]));
    }
}
