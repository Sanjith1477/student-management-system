package manager;

import file.FileManager;
import model.Enrollment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the Enrollment relationship between students and courses.
 * This is the only place that decides who is enrolled in what -- teacher
 * and student UI must go through this class rather than assuming every
 * student belongs to every course.
 */
public class EnrollmentManager {

    private final FileManager fileManager;

    public EnrollmentManager() {
        this.fileManager = new FileManager();
    }

    /** Enrolls a student in a course for a semester. Returns false if already actively enrolled. */
    public boolean enroll(String studentId, String courseId, String semester) {
        List<Enrollment> all = fileManager.readEnrollments();
        for (Enrollment e : all) {
            if (e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId)) {
                if (e.isActive()) {
                    return false; // already enrolled
                }
                // Re-activate a previously dropped enrollment
                e.setStatus(Enrollment.STATUS_ACTIVE);
                e.setSemester(semester);
                fileManager.writeEnrollments(all);
                return true;
            }
        }
        fileManager.appendEnrollment(new Enrollment(studentId, courseId, semester, Enrollment.STATUS_ACTIVE));
        return true;
    }

    /** Marks the enrollment as dropped (soft remove, keeps historical attendance meaningful). */
    public boolean unenroll(String studentId, String courseId) {
        List<Enrollment> all = fileManager.readEnrollments();
        for (Enrollment e : all) {
            if (e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId) && e.isActive()) {
                e.setStatus(Enrollment.STATUS_DROPPED);
                fileManager.writeEnrollments(all);
                return true;
            }
        }
        return false;
    }

    /** Hard delete of an enrollment record entirely (used for cascading deletes). */
    public boolean removeCompletely(String studentId, String courseId) {
        List<Enrollment> all = fileManager.readEnrollments();
        boolean removed = all.removeIf(e -> e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId));
        if (removed) fileManager.writeEnrollments(all);
        return removed;
    }

    /** Cascading delete: removes every enrollment referencing a course (course deletion). */
    public void removeAllForCourse(String courseId) {
        List<Enrollment> all = fileManager.readEnrollments();
        boolean removed = all.removeIf(e -> e.getCourseId().equals(courseId));
        if (removed) fileManager.writeEnrollments(all);
    }

    /** Cascading delete: removes every enrollment referencing a student (student deletion). */
    public void removeAllForStudent(String studentId) {
        List<Enrollment> all = fileManager.readEnrollments();
        boolean removed = all.removeIf(e -> e.getStudentId().equals(studentId));
        if (removed) fileManager.writeEnrollments(all);
    }

    public boolean isEnrolled(String studentId, String courseId) {
        for (Enrollment e : fileManager.readEnrollments()) {
            if (e.getStudentId().equals(studentId) && e.getCourseId().equals(courseId) && e.isActive()) {
                return true;
            }
        }
        return false;
    }

    public List<Enrollment> getAllEnrollments() {
        return fileManager.readEnrollments();
    }

    public List<Enrollment> getActiveEnrollments() {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : fileManager.readEnrollments()) {
            if (e.isActive()) result.add(e);
        }
        return result;
    }

    /** All course IDs a student is actively enrolled in. */
    public List<String> getEnrolledCourseIds(String studentId) {
        List<String> ids = new ArrayList<>();
        for (Enrollment e : fileManager.readEnrollments()) {
            if (e.getStudentId().equals(studentId) && e.isActive()) ids.add(e.getCourseId());
        }
        return ids;
    }

    /** All student IDs actively enrolled in a course. */
    public List<String> getEnrolledStudentIds(String courseId) {
        List<String> ids = new ArrayList<>();
        for (Enrollment e : fileManager.readEnrollments()) {
            if (e.getCourseId().equals(courseId) && e.isActive()) ids.add(e.getStudentId());
        }
        return ids;
    }

    /** Distinct set of course IDs referenced by any student (useful for reports). */
    public Set<String> getAllEnrolledCourseIds() {
        Set<String> ids = new LinkedHashSet<>();
        for (Enrollment e : getActiveEnrollments()) ids.add(e.getCourseId());
        return ids;
    }

    public int countEnrolledStudents(String courseId) {
        return getEnrolledStudentIds(courseId).size();
    }

    public int countEnrolledCourses(String studentId) {
        return getEnrolledCourseIds(studentId).size();
    }
}
