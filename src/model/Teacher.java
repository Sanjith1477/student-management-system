package model;

public class Teacher {
    private String teacherId;
    private String name;
    private String department;
    private String email;
    private String phone;
    private String username;

    public Teacher(String teacherId, String name, String department, String email, String phone, String username) {
        this.teacherId = teacherId;
        this.name = name;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.username = username;
    }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String toLine() {
        return teacherId + "," + name + "," + department + "," + email + "," + phone + "," + username;
    }

    public static Teacher fromLine(String line) {
        String[] p = line.split(",", -1);
        return new Teacher(p[0], p[1], p[2], p[3], p[4], p[5]);
    }

    @Override
    public String toString() {
        return teacherId + " | " + name + " | " + department;
    }
}
