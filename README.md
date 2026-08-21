# EduTrack Management System 

A polished, modern rebuild of the original Java Swing Student Management System.
Same layered architecture, same plain-text storage, no new frameworks \u2014 just a
consistent design system, a real student-course enrollment relationship, and a
handful of data-integrity fixes.

## How to compile and run

No external dependencies are required \u2014 just a JDK (17+ recommended).

```bash
# from the project root
mkdir -p build
javac -d build -encoding UTF-8 $(find src -name "*.java")
java -cp build Main
```

Demo logins (unchanged from the original sample data):

| Role    | Username  | Password    |
|---------|-----------|-------------|
| Admin   | admin     | admin123    |
| Teacher | teacher01 | teacher123  |
| Teacher | teacher02 | teacher123  |
| Student | student01 | student123  |
| Student | student02 | student123  |

## What changed

### Architecture (unchanged, extended)
`UI \u2192 Manager \u2192 FileManager \u2192 text files` is preserved exactly. The only
structural change is on the UI side: every role now gets **one `MainFrame`**
(sidebar + header + `CardLayout` content area) instead of a `JFrame` per
dashboard and a `JDialog` per listing. Add/Edit forms are still small modal
dialogs \u2014 that's still the right UX for a short form \u2014 but every *listing*
screen (Student Directory, Teacher Directory, Course Management, Enrollments,
Attendance History) is now a sidebar-navigable page built on a shared,
reusable searchable/sortable table component.

### New: Enrollment (the critical fix)
The original app assigned **teachers** to courses but never enrolled
**students** in courses \u2014 attendance was effectively marked for every student
in every course. This is now a real relationship:

- `model/Enrollment` \u2014 `(studentId, courseId, semester, status)`
- `data/enrollments.txt` \u2014 new data file, read/written only through `FileManager`
- `manager/EnrollmentManager` \u2014 enroll / unenroll / lookups, used everywhere
  a student-course relationship needs to be checked
- Admin gets a new **Enrollments** page to enroll/remove students from courses,
  plus an "Enroll" action from the Student Directory and Course Management pages
- **Mark Attendance** (teacher) now only lists actively enrolled students for
  the selected course
- **My Students** (teacher) now only shows enrolled students per course
- The **student dashboard/attendance** views are scoped to the student's own
  enrolled courses only

### UI/UX system
- `ui/theme/Theme.java` \u2014 single source of truth for the navy/indigo +
  teal color palette, typography, spacing and corner radius
- `ui/components/*` \u2014 reusable building blocks used everywhere: `RoundedPanel`
  (cards), `AppButton` (primary/secondary/danger/ghost), `Badge` (status pills,
  incl. attendance/enrollment/at-risk helpers), `MetricCard` (dashboard stats),
  `ProgressRing` (circular attendance visual), `TablePanel` (searchable +
  sortable JTable with empty state, replaces every plain `JTextArea` listing),
  `BarChartPanel` (dependency-free Java2D bar chart for reports/trends),
  `Sidebar`, `HeaderBar`, `FormDialog` (consistent modal form shell with
  inline validation banner), `ConfirmDialog` (consistent delete confirmation)
- No FlatLaf/third-party look-and-feel jar: GitHub only hosts FlatLaf's source
  in Releases (not a compiled binary), and Maven Central wasn't reachable from
  the build environment used here. The theme layer above achieves the same
  "flat, modern" visual result in pure Swing/Java2D, so the project still
  compiles with nothing but the JDK. Swapping in a real FlatLaf jar later is a
  one-line change in `Main.java` if you'd like to add it yourself.

### Role dashboards
- **Admin**: metric cards (students/teachers/courses/overall attendance),
  attendance trend + department-breakdown charts, recent activity feed, quick
  actions (add student/teacher, create course, enroll students)
- **Teacher**: assigned courses, today's attendance tasks (courses not yet
  marked today), at-risk students (<75%) across their courses, recent
  attendance activity, fast "Mark" shortcut
- **Student**: welcome card, overall attendance progress ring, one card per
  enrolled course with its attendance %, low-attendance warning banner,
  announcements placeholder, recent attendance records

### Data integrity fixes
- `StudentManager.editStudent` / `TeacherManager.editTeacher` now rename the
  linked login (`User`) account when the username changes, instead of leaving
  an orphaned login
- `StudentManager.deleteStudent` cascades: removes the login, enrollments,
  and attendance records for that student
- `CourseManager.deleteCourse` cascades: removes enrollments and attendance
  records for that course
- `TeacherManager.deleteTeacher` **un-assigns** (doesn't delete) their courses,
  so course/enrollment/attendance history survives as "Unassigned"
- Teacher's Attendance History is now a dropdown of **their own** courses only
  (was a free-text course field before) \u2014 `CourseManager.isTeacherAssigned`
  is checked again as defense-in-depth even though the dropdown already limits
  the choices
- Course creation/editing now uses a **teacher dropdown** instead of a typed
  teacher ID

### Reports
- Metric cards (overall attendance, total courses, students at risk)
- Simple attendance-by-course bar chart
- Per-course summary table (enrolled count, sessions recorded, attendance %)
- CSV export (also available on the teacher's Attendance History page)

## Migration notes for existing data
- `data/students.txt`, `data/teachers.txt`, `data/courses.txt`,
  `data/attendance.txt`, `data/users.txt` are untouched and fully compatible
  \u2014 nothing about their format changed
- `data/enrollments.txt` is new. It starts **empty** on a fresh checkout; the
  app creates it automatically on first run (via `FileManager`). Until
  students are enrolled, teachers will see no students to mark and students
  will see no courses \u2014 this is intentional (the whole point of the fix) and
  not a bug
- For convenience, this delivered copy seeds `data/enrollments.txt` with a
  few sample enrollments matching the existing sample students/courses/
  attendance records, so the new feature is visible immediately:
  - `1001` (John Doe) \u2192 `101` (Java Programming), `102` (DBMS)
  - `1002` (Jane Smith) \u2192 `101` (Java Programming)
  - `24I353` (SAM) \u2192 `102` (DBMS)
  Delete or edit that file (or use the admin Enrollments page) as needed.

## Known limitations / follow-ups you may want
- "Upcoming classes/announcements" on the student dashboard is a placeholder
  card, as there's no schedule/announcement data model in the original app
- "Recent registrations" on the admin dashboard is inferred from file append
  order (new records are appended, so the last N lines are "most recent")
  since the original data files have no timestamp field \u2014 keeps the change
  100% backward compatible with existing files
