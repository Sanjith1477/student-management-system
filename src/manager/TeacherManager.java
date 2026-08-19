package manager;

import file.FileManager;
import model.Teacher;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class TeacherManager {

    private final FileManager fileManager;
    private final UserManager userManager;

    public TeacherManager() {
        this.fileManager = new FileManager();
        this.userManager = new UserManager();
    }

    public boolean addTeacher(Teacher teacher, String password) {
        List<Teacher> teachers = fileManager.readTeachers();
        for (Teacher t : teachers) {
            if (t.getTeacherId().equals(teacher.getTeacherId())) {
                return false;
            }
        }
        teachers.add(teacher);
        fileManager.writeTeachers(teachers);
        userManager.addUser(new User(teacher.getUsername(), password, "TEACHER"));
        return true;
    }

    /** Updates a teacher record, keeping the linked login username in sync if changed. */
    public boolean editTeacher(Teacher updated) {
        List<Teacher> teachers = fileManager.readTeachers();
        for (int i = 0; i < teachers.size(); i++) {
            Teacher existing = teachers.get(i);
            if (existing.getTeacherId().equals(updated.getTeacherId())) {
                String oldUsername = existing.getUsername();
                teachers.set(i, updated);
                fileManager.writeTeachers(teachers);
                if (!oldUsername.equalsIgnoreCase(updated.getUsername())) {
                    userManager.renameUser(oldUsername, updated.getUsername());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a teacher, removes their login, and safely un-assigns (rather
     * than deletes) any courses they were teaching so those courses and
     * their enrollment/attendance history survive as "Unassigned".
     */
    public boolean deleteTeacher(String teacherId) {
        List<Teacher> teachers = fileManager.readTeachers();
        Teacher toRemove = null;
        for (Teacher t : teachers) {
            if (t.getTeacherId().equals(teacherId)) {
                toRemove = t;
                break;
            }
        }
        if (toRemove == null) return false;
        teachers.remove(toRemove);
        fileManager.writeTeachers(teachers);
        userManager.deleteUser(toRemove.getUsername());
        new CourseManager().unassignTeacherFromAllCourses(teacherId);
        return true;
    }

    public Teacher getTeacher(String teacherId) {
        for (Teacher t : fileManager.readTeachers()) {
            if (t.getTeacherId().equals(teacherId)) return t;
        }
        return null;
    }

    public Teacher getTeacherByUsername(String username) {
        for (Teacher t : fileManager.readTeachers()) {
            if (t.getUsername().equalsIgnoreCase(username)) return t;
        }
        return null;
    }

    public List<Teacher> searchTeacher(String keyword) {
        List<Teacher> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Teacher t : fileManager.readTeachers()) {
            if (t.getTeacherId().toLowerCase().contains(lower) ||
                t.getName().toLowerCase().contains(lower) ||
                t.getDepartment().toLowerCase().contains(lower)) {
                results.add(t);
            }
        }
        return results;
    }

    public List<Teacher> getAllTeachers() {
        return fileManager.readTeachers();
    }
}
