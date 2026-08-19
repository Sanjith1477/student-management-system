package ui.student.pages;

import manager.AttendanceManager;
import manager.CourseManager;
import model.Attendance;
import model.Course;
import model.Student;
import ui.components.TablePanel;
import ui.shell.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Full attendance record table for the logged-in student, scoped to their own records only. */
public class StudentAttendancePage extends JPanel implements Page {

    private final AttendanceManager attendanceManager = new AttendanceManager();
    private final CourseManager courseManager = new CourseManager();
    private final Student student;
    private final TablePanel tablePanel;

    public StudentAttendancePage(Student student) {
        this.student = student;
        setLayout(new BorderLayout());
        setOpaque(false);
        tablePanel = new TablePanel("My Attendance Records", new String[]{"Date", "Course", "Status"}, new int[]{0, 1});
        add(tablePanel, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        if (student == null) return;
        List<Attendance> records = attendanceManager.attendanceByStudent(student.getStudentId());
        Object[][] rows = new Object[records.size()][];
        for (int i = 0; i < records.size(); i++) {
            Attendance a = records.get(records.size() - 1 - i); // newest first
            Course c = courseManager.getCourse(a.getCourseId());
            rows[i] = new Object[]{a.getDate(), c != null ? c.getCourseName() : a.getCourseId(), a.getStatus()};
        }
        tablePanel.setRows(rows);
    }
}
