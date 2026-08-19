package model;

public class Enrollment {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DROPPED = "DROPPED";

    private String studentId;
    private String courseId;
    private String semester;
    private String status; // ACTIVE / DROPPED

    public Enrollment(String studentId, String courseId, String semester, String status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.semester = semester;
        this.status = status;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return STATUS_ACTIVE.equalsIgnoreCase(status); }

    public String toLine() {
        return studentId + "," + courseId + "," + semester + "," + status;
    }

    public static Enrollment fromLine(String line) {
        String[] p = line.split(",", -1);
        String sem = p.length > 2 ? p[2] : "";
        String status = p.length > 3 ? p[3] : STATUS_ACTIVE;
        return new Enrollment(p[0], p[1], sem, status);
    }

    @Override
    public String toString() {
        return studentId + " | " + courseId + " | " + semester + " | " + status;
    }
}
