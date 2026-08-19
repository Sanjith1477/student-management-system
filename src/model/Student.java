package model;

public class Student {
    private String studentId;
    private String name;
    private String department;
    private String year;
    private String section;
    private String email;
    private String phone;
    private String username;

    public Student(String studentId, String name, String department, String year,
                   String section, String email, String phone, String username) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.year = year;
        this.section = section;
        this.email = email;
        this.phone = phone;
        this.username = username;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String toLine() {
        return studentId + "," + name + "," + department + "," + year + "," +
               section + "," + email + "," + phone + "," + username;
    }

    public static Student fromLine(String line) {
        String[] p = line.split(",", -1);
        return new Student(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
    }

    @Override
    public String toString() {
        return studentId + " | " + name + " | " + department + " | Year " + year + " | Sec " + section;
    }
}
