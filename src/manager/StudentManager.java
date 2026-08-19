package manager;

import file.FileManager;
import model.Student;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class StudentManager {

    private final FileManager fileManager;
    private final UserManager userManager;

    public StudentManager() {
        this.fileManager = new FileManager();
        this.userManager = new UserManager();
    }

    public boolean addStudent(Student student, String password) {
        List<Student> students = fileManager.readStudents();
        for (Student s : students) {
            if (s.getStudentId().equals(student.getStudentId())) {
                return false; // duplicate id
            }
        }
        students.add(student);
        fileManager.writeStudents(students);
        userManager.addUser(new User(student.getUsername(), password, "STUDENT"));
        return true;
    }

    /**
     * Updates a student record. If the username changed, the linked login
     * (User) account is renamed too so the student can still log in --
     * otherwise the account would silently become orphaned.
     */
    public boolean editStudent(Student updated) {
        List<Student> students = fileManager.readStudents();
        for (int i = 0; i < students.size(); i++) {
            Student existing = students.get(i);
            if (existing.getStudentId().equals(updated.getStudentId())) {
                String oldUsername = existing.getUsername();
                students.set(i, updated);
                fileManager.writeStudents(students);
                if (!oldUsername.equalsIgnoreCase(updated.getUsername())) {
                    userManager.renameUser(oldUsername, updated.getUsername());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a student and cascades the removal to related data: the
     * linked login account, any course enrollments, and any attendance
     * history, so no orphaned records are left behind in other files.
     */
    public boolean deleteStudent(String studentId) {
        List<Student> students = fileManager.readStudents();
        Student toRemove = null;
        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove == null) return false;
        students.remove(toRemove);
        fileManager.writeStudents(students);
        userManager.deleteUser(toRemove.getUsername());
        new EnrollmentManager().removeAllForStudent(studentId);
        new AttendanceManager().removeByStudent(studentId);
        return true;
    }

    public Student getStudent(String studentId) {
        for (Student s : fileManager.readStudents()) {
            if (s.getStudentId().equals(studentId)) return s;
        }
        return null;
    }

    public Student getStudentByUsername(String username) {
        for (Student s : fileManager.readStudents()) {
            if (s.getUsername().equalsIgnoreCase(username)) return s;
        }
        return null;
    }

    public List<Student> searchStudent(String keyword) {
        List<Student> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Student s : fileManager.readStudents()) {
            if (s.getStudentId().toLowerCase().contains(lower) ||
                s.getName().toLowerCase().contains(lower) ||
                s.getDepartment().toLowerCase().contains(lower)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> getAllStudents() {
        return fileManager.readStudents();
    }
}
