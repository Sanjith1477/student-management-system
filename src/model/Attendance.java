package model;

public class Attendance {
    private String date;
    private String courseId;
    private String studentId;
    private String status; // Present / Absent

    public Attendance(String date, String courseId, String studentId, String status) {
        this.date = date;
        this.courseId = courseId;
        this.studentId = studentId;
        this.status = status;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toLine() {
        return date + "," + courseId + "," + studentId + "," + status;
    }

    public static Attendance fromLine(String line) {
        String[] p = line.split(",", -1);
        return new Attendance(p[0], p[1], p[2], p[3]);
    }

    @Override
    public String toString() {
        return date + " | Course " + courseId + " | Student " + studentId + " | " + status;
    }
}
