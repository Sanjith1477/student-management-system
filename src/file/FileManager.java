package file;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager is the ONLY class in the project that directly reads/writes
 * the text files under the data/ folder. All Manager classes go through it.
 */
public class FileManager {

    // Data directory is resolved relative to the project root so that the
    // app works whether run from an IDE or from the command line.
    private static final String DATA_DIR = "data";

    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.txt";
    private static final String TEACHERS_FILE = DATA_DIR + File.separator + "teachers.txt";
    private static final String COURSES_FILE = DATA_DIR + File.separator + "courses.txt";
    private static final String ATTENDANCE_FILE = DATA_DIR + File.separator + "attendance.txt";
    private static final String ENROLLMENTS_FILE = DATA_DIR + File.separator + "enrollments.txt";

    static {
        ensureDataFiles();
    }

    private static void ensureDataFiles() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            createIfMissing(USERS_FILE);
            createIfMissing(STUDENTS_FILE);
            createIfMissing(TEACHERS_FILE);
            createIfMissing(COURSES_FILE);
            createIfMissing(ATTENDANCE_FILE);
            createIfMissing(ENROLLMENTS_FILE);
        } catch (IOException e) {
            System.err.println("Could not initialize data folder: " + e.getMessage());
        }
    }

    private static void createIfMissing(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            Files.createFile(p);
        }
    }

    private static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + path + ": " + e.getMessage());
        }
        return lines;
    }

    private static void writeLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, false), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing " + path + ": " + e.getMessage());
        }
    }

    private static void appendLine(String path, String line) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to " + path + ": " + e.getMessage());
        }
    }

    // ---------------- USERS ----------------
    public List<User> readUsers() {
        List<User> users = new ArrayList<>();
        for (String line : readLines(USERS_FILE)) {
            users.add(User.fromLine(line));
        }
        return users;
    }

    public void writeUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) lines.add(u.toLine());
        writeLines(USERS_FILE, lines);
    }

    // ---------------- STUDENTS ----------------
    public List<Student> readStudents() {
        List<Student> students = new ArrayList<>();
        for (String line : readLines(STUDENTS_FILE)) {
            students.add(Student.fromLine(line));
        }
        return students;
    }

    public void writeStudents(List<Student> students) {
        List<String> lines = new ArrayList<>();
        for (Student s : students) lines.add(s.toLine());
        writeLines(STUDENTS_FILE, lines);
    }

    // ---------------- TEACHERS ----------------
    public List<Teacher> readTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        for (String line : readLines(TEACHERS_FILE)) {
            teachers.add(Teacher.fromLine(line));
        }
        return teachers;
    }

    public void writeTeachers(List<Teacher> teachers) {
        List<String> lines = new ArrayList<>();
        for (Teacher t : teachers) lines.add(t.toLine());
        writeLines(TEACHERS_FILE, lines);
    }

    // ---------------- COURSES ----------------
    public List<Course> readCourses() {
        List<Course> courses = new ArrayList<>();
        for (String line : readLines(COURSES_FILE)) {
            courses.add(Course.fromLine(line));
        }
        return courses;
    }

    public void writeCourses(List<Course> courses) {
        List<String> lines = new ArrayList<>();
        for (Course c : courses) lines.add(c.toLine());
        writeLines(COURSES_FILE, lines);
    }

    // ---------------- ATTENDANCE ----------------
    public List<Attendance> readAttendance() {
        List<Attendance> records = new ArrayList<>();
        for (String line : readLines(ATTENDANCE_FILE)) {
            records.add(Attendance.fromLine(line));
        }
        return records;
    }

    public void writeAttendance(List<Attendance> records) {
        List<String> lines = new ArrayList<>();
        for (Attendance a : records) lines.add(a.toLine());
        writeLines(ATTENDANCE_FILE, lines);
    }

    public void appendAttendance(Attendance record) {
        appendLine(ATTENDANCE_FILE, record.toLine());
    }

    // ---------------- ENROLLMENTS ----------------
    public List<Enrollment> readEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        for (String line : readLines(ENROLLMENTS_FILE)) {
            enrollments.add(Enrollment.fromLine(line));
        }
        return enrollments;
    }

    public void writeEnrollments(List<Enrollment> enrollments) {
        List<String> lines = new ArrayList<>();
        for (Enrollment e : enrollments) lines.add(e.toLine());
        writeLines(ENROLLMENTS_FILE, lines);
    }

    public void appendEnrollment(Enrollment enrollment) {
        appendLine(ENROLLMENTS_FILE, enrollment.toLine());
    }
}
