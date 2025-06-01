import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// RegistrationManager.java (Manages students and courses)
class RegistrationManager {
    private List<Student> registeredStudents;
    private List<Course> availableCourses;
    private List<Account> userAccounts; // To manage all system accounts

    public RegistrationManager() {
        this.registeredStudents = new ArrayList<>();
        this.availableCourses = new ArrayList<>();
        this.userAccounts = new ArrayList<>();
    }

    // Operations to manage students
    public void registerStudent(Student student) {
        if (!registeredStudents.contains(student)) {
            registeredStudents.add(student);
            System.out.println("Student " + student.getName() + " (" + student.getStudentId() + ") registered successfully.");
        } else {
            System.out.println("Student " + student.getName() + " is already registered.");
        }
    }

    public void unregisterStudent(Student student) {
        if (registeredStudents.remove(student)) {
            // Also unenroll from all courses
            for (Course course : student.getEnrolledCourses()) {
                course.removeStudent(student);
            }
            student.getEnrolledCourses().clear(); // Clear student's course list
            System.out.println("Student " + student.getName() + " (" + student.getStudentId() + ") unregistered successfully and unenrolled from all courses.");
        } else {
            System.out.println("Student " + student.getName() + " is not registered.");
        }
    }

    // Operations to manage courses
    public void addCourse(Course course) {
        if (!availableCourses.contains(course)) {
            availableCourses.add(course);
            System.out.println("Course " + course.getCourseName() + " (" + course.getCourseId() + ") added to available courses.");
        } else {
            System.out.println("Course " + course.getCourseName() + " is already available.");
        }
    }

    public void removeCourse(Course course) {
        if (availableCourses.remove(course)) {
            // Also remove this course from all students who enrolled in it
            for (Student student : course.getEnrolledStudents()) {
                student.dropCourse(course);
            }
            course.getEnrolledStudents().clear(); // Clear course's student list
            System.out.println("Course " + course.getCourseName() + " (" + course.getCourseId() + ") removed from available courses and students unenrolled.");
        } else {
            System.out.println("Course " + course.getCourseName() + " is not found in available courses.");
        }
    }

    // Method to link an account to the manager (for central management)
    public void addAccount(Account account) {
        if (!userAccounts.contains(account)) {
            userAccounts.add(account);
            System.out.println("Account for " + account.getUsername() + " added to manager.");
        } else {
            System.out.println("Account for " + account.getUsername() + " already exists.");
        }
    }

    // Getters for lists (for demonstration/system overview)
    public List<Student> getRegisteredStudents() {
        return registeredStudents;
    }

    public List<Course> getAvailableCourses() {
        return availableCourses;
    }

    public List<Account> getUserAccounts() {
        return userAccounts;
    }
}

// Account.java
class Account {
    private String username;
    private String password; // In a real system, this would be hashed
    private Student owner; // A Student 'manages' an Account (or an Account 'belongs to' a Student)

    public Account(String username, String password, Student owner) {
        this.username = username;
        this.password = password;
        this.owner = owner;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() { // Caution: direct password access in real apps is risky
        return password;
    }

    public Student getOwner() {
        return owner;
    }

    // Operations
    public boolean login(String enteredUsername, String enteredPassword) {
        if (this.username.equals(enteredUsername) && this.password.equals(enteredPassword)) {
            System.out.println(this.username + " logged in successfully.");
            return true;
        }
        System.out.println("Login failed for " + enteredUsername + ": Invalid credentials.");
        return false;
    }

    public void logout() {
        System.out.println(this.username + " logged out.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}

// Student.java
class Student {
    private String name;
    private String studentId;
    private String email;
    private Account account; // A Student 'has' an Account
    private List<Course> enrolledCourses; // A Student 'enrolls' in many Courses

    public Student(String name, String studentId, String email) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.enrolledCourses = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getEmail() {
        return email;
    }

    public Account getAccount() {
        return account;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    // Setter for account (link after account creation)
    public void setAccount(Account account) {
        this.account = account;
        System.out.println("Account for " + this.name + " (" + account.getUsername() + ") linked.");
    }

    // Operations
    public void registerCourse(Course course) {
        if (course != null && !enrolledCourses.contains(course)) {
            enrolledCourses.add(course);
            course.addStudent(this); // Link back to course
            System.out.println(this.name + " (" + this.studentId + ") registered for course: " + course.getCourseName());
        } else if (course == null) {
            System.out.println("Cannot register for a null course.");
        } else {
            System.out.println(this.name + " is already enrolled in " + course.getCourseName());
        }
    }

    public void dropCourse(Course course) {
        if (enrolledCourses.remove(course)) {
            course.removeStudent(this); // Unlink from course
            System.out.println(this.name + " (" + this.studentId + ") dropped course: " + course.getCourseName());
        } else {
            System.out.println(this.name + " was not enrolled in " + course.getCourseName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}

// Course.java
class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private List<Student> enrolledStudents; // A Course 'has' many enrolled Students

    public Course(String courseId, String courseName, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.enrolledStudents = new ArrayList<>();
    }

    // Getters
    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    // Operations
    public void addStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            System.out.println("Student " + student.getName() + " added to " + courseName + ".");
        } else {
            System.out.println("Student " + student.getName() + " is already in " + courseName + ".");
        }
    }

    public void removeStudent(Student student) {
        if (enrolledStudents.remove(student)) {
            System.out.println("Student " + student.getName() + " removed from " + courseName + ".");
        } else {
            System.out.println("Student " + student.getName() + " not found in " + courseName + ".");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}


// Main class to demonstrate the Student Registration System
public class StudentRegistrationSystem {
    public static void main(String[] args) {
        // --- 1. Initialize the Registration Manager ---
        RegistrationManager manager = new RegistrationManager();
        System.out.println("Student Registration System initialized.");

        // --- 2. Create Students ---
        Student alice = new Student("Alice Wonderland", "S001", "alice@university.com");
        Student bob = new Student("Bob The Builder", "S002", "bob@university.com");
        Student charlie = new Student("Charlie Chaplin", "S003", "charlie@university.com");

        // --- 3. Create Accounts and link to Students ---
        Account aliceAccount = new Account("alice_w", "pass123", alice);
        alice.setAccount(aliceAccount);
        manager.addAccount(aliceAccount); // Add account to manager's watch list

        Account bobAccount = new Account("bob_b", "securepwd", bob);
        bob.setAccount(bobAccount);
        manager.addAccount(bobAccount);

        Account charlieAccount = new Account("charlie_c", "mypass", charlie);
        charlie.setAccount(charlieAccount);
        manager.addAccount(charlieAccount);

        System.out.println("\n--- Student Registration ---");
        manager.registerStudent(alice);
        manager.registerStudent(bob);
        manager.registerStudent(charlie);
        manager.registerStudent(alice); // Try to register again

        // --- 4. Create Courses ---
        Course math101 = new Course("MATH101", "Calculus I", 3);
        Course cs101 = new Course("CS101", "Introduction to Programming", 4);
        Course hist201 = new Course("HIST201", "World History", 3);

        System.out.println("\n--- Course Management ---");
        manager.addCourse(math101);
        manager.addCourse(cs101);
        manager.addCourse(hist201);
        manager.addCourse(math101); // Try to add again

        System.out.println("\n--- Student Course Enrollment ---");
        // Alice enrolls in courses
        alice.registerCourse(math101);
        alice.registerCourse(cs101);
        alice.registerCourse(math101); // Alice tries to enroll in Math101 again

        // Bob enrolls in courses
        bob.registerCourse(cs101);
        bob.registerCourse(hist201);

        // Charlie enrolls in one course
        charlie.registerCourse(math101);

        System.out.println("\n--- Viewing Enrollments ---");
        System.out.println("Alice's enrolled courses: " + alice.getEnrolledCourses().stream().map(Course::getCourseName).collect(java.util.stream.Collectors.joining(", ")));
        System.out.println("Students in CS101: " + cs101.getEnrolledStudents().stream().map(Student::getName).collect(java.util.stream.Collectors.joining(", ")));
        System.out.println("Students in Math101: " + math101.getEnrolledStudents().stream().map(Student::getName).collect(java.util.stream.Collectors.joining(", ")));

        System.out.println("\n--- Student Login/Logout ---");
        aliceAccount.login("alice_w", "pass123");
        bobAccount.login("bob_b", "wrongpwd"); // Failed login
        charlieAccount.logout();

        System.out.println("\n--- Dropping Courses ---");
        bob.dropCourse(hist201);
        bob.dropCourse(math101); // Bob wasn't enrolled in Math101

        System.out.println("\n--- Manager Unregistering Student ---");
        manager.unregisterStudent(charlie); // Charlie gets unregistered

        System.out.println("\n--- Manager Removing Course ---");
        manager.removeCourse(cs101); // CS101 is removed
        System.out.println("Alice's enrolled courses after CS101 removal: " + alice.getEnrolledCourses().stream().map(Course::getCourseName).collect(java.util.stream.Collectors.joining(", ")));
        System.out.println("Bob's enrolled courses after CS101 removal: " + bob.getEnrolledCourses().stream().map(Course::getCourseName).collect(java.util.stream.Collectors.joining(", ")));


        System.out.println("\n--- System Overview ---");
        System.out.println("Total registered students: " + manager.getRegisteredStudents().size());
        System.out.println("Total available courses: " + manager.getAvailableCourses().size());
        System.out.println("Total user accounts: " + manager.getUserAccounts().size());
    }
}
