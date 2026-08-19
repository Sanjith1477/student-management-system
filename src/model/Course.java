package model;

public class Course {
    private String courseId;
    private String courseName;
    private String teacherId;

    public Course(String courseId, String courseName, String teacherId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String toLine() {
        return courseId + "," + courseName + "," + teacherId;
    }

    public static Course fromLine(String line) {
        String[] p = line.split(",", -1);
        return new Course(p[0], p[1], p[2]);
    }

    @Override
    public String toString() {
        return courseId + " | " + courseName + " | TeacherId: " + teacherId;
    }
}
