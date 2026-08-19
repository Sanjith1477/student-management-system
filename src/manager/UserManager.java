package manager;

import file.FileManager;
import model.User;

import java.util.List;

public class UserManager {

    private final FileManager fileManager;

    public UserManager() {
        this.fileManager = new FileManager();
    }

    public User login(String username, String password) {
        User user = findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User findUser(String username) {
        List<User> users = fileManager.readUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        List<User> users = fileManager.readUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                if (!u.getPassword().equals(oldPassword)) {
                    return false;
                }
                u.setPassword(newPassword);
                fileManager.writeUsers(users);
                return true;
            }
        }
        return false;
    }

    /** Admin-only: reset a user's password without knowing the old one. */
    public boolean resetPassword(String username, String newPassword) {
        List<User> users = fileManager.readUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                u.setPassword(newPassword);
                fileManager.writeUsers(users);
                return true;
            }
        }
        return false;
    }

    public boolean addUser(User user) {
        List<User> users = fileManager.readUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                return false; // already exists
            }
        }
        users.add(user);
        fileManager.writeUsers(users);
        return true;
    }

    /** Returns every login account with the given role (e.g. "ADMIN"). */
    public List<User> getUsersByRole(String role) {
        List<User> result = new java.util.ArrayList<>();
        for (User u : fileManager.readUsers()) {
            if (u.getRole().equalsIgnoreCase(role)) result.add(u);
        }
        return result;
    }

    /**
     * Renames a user's login username (e.g. when a student/teacher's profile
     * username is edited by an admin), preserving their password and role.
     * Fails safely if the new username is already taken.
     */
    public boolean renameUser(String oldUsername, String newUsername) {
        if (oldUsername.equalsIgnoreCase(newUsername)) return true;
        List<User> users = fileManager.readUsers();
        User toRename = null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(oldUsername)) {
                toRename = u;
                break;
            }
        }
        if (toRename == null) return false;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(newUsername)) {
                return false; // new username already taken
            }
        }
        toRename.setUsername(newUsername);
        fileManager.writeUsers(users);
        return true;
    }

    public boolean deleteUser(String username) {
        List<User> users = fileManager.readUsers();
        boolean removed = users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) {
            fileManager.writeUsers(users);
        }
        return removed;
    }
}
