package manager;

import file.FileManager;
import model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseManager {

    private final FileManager fileManager;

    public CourseManager() {
        this.fileManager = new FileManager();
    }

    public boolean addCourse(Course course) {
        List<Course> courses = fileManager.readCourses();
        for (Course c : courses) {
            if (c.getCourseId().equals(course.getCourseId())) {
                return false;
            }
        }
        courses.add(course);
        fileManager.writeCourses(courses);
        return true;
    }

    public boolean editCourse(Course updated) {
        List<Course> courses = fileManager.readCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equals(updated.getCourseId())) {
                courses.set(i, updated);
                fileManager.writeCourses(courses);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a course and cascades the removal to related data: every
     * enrollment and every attendance record referencing this course is
     * removed too, so no orphaned rows are left in other data files.
     */
    public boolean deleteCourse(String courseId) {
        List<Course> courses = fileManager.readCourses();
        boolean removed = courses.removeIf(c -> c.getCourseId().equals(courseId));
        if (removed) {
            fileManager.writeCourses(courses);
            new EnrollmentManager().removeAllForCourse(courseId);
            new AttendanceManager().removeByCourse(courseId);
        }
        return removed;
    }

    /** Clears the teacher assignment on every course taught by a teacher (used when that teacher is deleted). */
    public void unassignTeacherFromAllCourses(String teacherId) {
        List<Course> courses = fileManager.readCourses();
        boolean changed = false;
        for (Course c : courses) {
            if (c.getTeacherId() != null && c.getTeacherId().equals(teacherId)) {
                c.setTeacherId("");
                changed = true;
            }
        }
        if (changed) fileManager.writeCourses(courses);
    }

    /** True if the given teacher is assigned to the given course -- used to gate attendance access. */
    public boolean isTeacherAssigned(String courseId, String teacherId) {
        Course c = getCourse(courseId);
        return c != null && teacherId != null && teacherId.equals(c.getTeacherId());
    }

    public boolean assignTeacher(String courseId, String teacherId) {
        List<Course> courses = fileManager.readCourses();
        for (Course c : courses) {
            if (c.getCourseId().equals(courseId)) {
                c.setTeacherId(teacherId);
                fileManager.writeCourses(courses);
                return true;
            }
        }
        return false;
    }

    public List<Course> getCourses() {
        return fileManager.readCourses();
    }

    public List<Course> getCoursesByTeacher(String teacherId) {
        List<Course> result = new ArrayList<>();
        for (Course c : fileManager.readCourses()) {
            if (c.getTeacherId().equals(teacherId)) result.add(c);
        }
        return result;
    }

    public Course getCourse(String courseId) {
        for (Course c : fileManager.readCourses()) {
            if (c.getCourseId().equals(courseId)) return c;
        }
        return null;
    }
}
