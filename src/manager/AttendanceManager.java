package manager;

import file.FileManager;
import model.Attendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    private final FileManager fileManager;

    public AttendanceManager() {
        this.fileManager = new FileManager();
    }

    /** Marks (or updates, if it already exists for that date/course/student) attendance. */
    public void markAttendance(Attendance record) {
        List<Attendance> all = fileManager.readAttendance();
        for (int i = 0; i < all.size(); i++) {
            Attendance a = all.get(i);
            if (a.getDate().equals(record.getDate()) &&
                a.getCourseId().equals(record.getCourseId()) &&
                a.getStudentId().equals(record.getStudentId())) {
                all.set(i, record);
                fileManager.writeAttendance(all);
                return;
            }
        }
        // Not found -> append as new record
        fileManager.appendAttendance(record);
    }

    public boolean editAttendance(Attendance updated) {
        List<Attendance> all = fileManager.readAttendance();
        for (int i = 0; i < all.size(); i++) {
            Attendance a = all.get(i);
            if (a.getDate().equals(updated.getDate()) &&
                a.getCourseId().equals(updated.getCourseId()) &&
                a.getStudentId().equals(updated.getStudentId())) {
                all.set(i, updated);
                fileManager.writeAttendance(all);
                return true;
            }
        }
        return false;
    }

    public List<Attendance> getAttendance() {
        return fileManager.readAttendance();
    }

    /** Cascading delete: removes every attendance record for a course (course deletion). */
    public void removeByCourse(String courseId) {
        List<Attendance> all = fileManager.readAttendance();
        boolean removed = all.removeIf(a -> a.getCourseId().equals(courseId));
        if (removed) fileManager.writeAttendance(all);
    }

    /** Cascading delete: removes every attendance record for a student (student deletion). */
    public void removeByStudent(String studentId) {
        List<Attendance> all = fileManager.readAttendance();
        boolean removed = all.removeIf(a -> a.getStudentId().equals(studentId));
        if (removed) fileManager.writeAttendance(all);
    }

    public List<Attendance> attendanceByCourse(String courseId) {
        return attendanceByDate(null, courseId);
    }

    /** Threshold below which a student is flagged "at risk" for a course/overall. */
    public static final double AT_RISK_THRESHOLD = 75.0;

    public static boolean isAtRisk(double percentage) {
        return percentage < AT_RISK_THRESHOLD;
    }

    public List<Attendance> attendanceByDate(String date, String courseId) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : fileManager.readAttendance()) {
            boolean dateMatches = date == null || date.isEmpty() || a.getDate().equals(date);
            boolean courseMatches = courseId == null || courseId.isEmpty() || a.getCourseId().equals(courseId);
            if (dateMatches && courseMatches) result.add(a);
        }
        return result;
    }

    public List<Attendance> attendanceByStudent(String studentId) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : fileManager.readAttendance()) {
            if (a.getStudentId().equals(studentId)) result.add(a);
        }
        return result;
    }

    public List<Attendance> attendanceByStudentAndCourse(String studentId, String courseId) {
        List<Attendance> result = new ArrayList<>();
        for (Attendance a : fileManager.readAttendance()) {
            if (a.getStudentId().equals(studentId) && a.getCourseId().equals(courseId)) result.add(a);
        }
        return result;
    }

    /** Returns overall attendance percentage (0-100) for a student across all courses. */
    public double calculatePercentage(String studentId) {
        List<Attendance> records = attendanceByStudent(studentId);
        if (records.isEmpty()) return 0.0;
        long present = records.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
        return (present * 100.0) / records.size();
    }

    /** Returns attendance percentage for a student in a specific course. */
    public double calculatePercentage(String studentId, String courseId) {
        List<Attendance> records = attendanceByStudentAndCourse(studentId, courseId);
        if (records.isEmpty()) return 0.0;
        long present = records.stream().filter(a -> a.getStatus().equalsIgnoreCase("Present")).count();
        return (present * 100.0) / records.size();
    }
}
