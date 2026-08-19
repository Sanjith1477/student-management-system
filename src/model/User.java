package model;

public class User {
    private String username;
    private String password;
    private String role; // ADMIN, TEACHER, STUDENT

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String toLine() {
        return username + "," + password + "," + role;
    }

    public static User fromLine(String line) {
        String[] p = line.split(",", -1);
        return new User(p[0], p[1], p[2]);
    }
}
