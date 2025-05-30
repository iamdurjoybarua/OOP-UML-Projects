import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // For equals and hashCode in association classes

public class StudentRegistrationDemo {
    public static void main(String[] args) {
        System.out.println("--- Student Registration System Demonstration ---");

        // 1. Create Students
        Student mary = new Student("S001", "Mary Jones", "mary.j@example.com");
        Student john = new Student("S002", "John Doe", "john.d@example.com");
        Student jane = new Student("S003", "Jane Smith", "jane.s@example.com");
        System.out.println("\n");

        // 2. Create Courses
        Course mkt350 = new Course("MKT350", "Principles of Marketing", 3);
        Course mis385 = new Course("MIS385", "Database Management", 4);
        Course cs101 = new Course("CS101", "Introduction to Programming", 3);
        System.out.println("\n");

        // 3. Create Computer Accounts
        ComputerAccount maryAccount = new ComputerAccount("jones385", "password123", 10);
        ComputerAccount johnAccount = new ComputerAccount("doe500", "securepass", 15);
        System.out.println("\n");

        // 4. Register Students for Courses (using Registration association class)
        System.out.println("--- Course Registrations ---");
        Registration reg1 = new Registration(mary, mkt350, "Fall2010");
        reg1.setGrade("W"); // Mary withdraws from MKT350
        reg1.setComputerAccount(maryAccount); // Link registration to Mary's computer account
        mary.addRegistration(reg1);
        mkt350.addRegistration(reg1);
        System.out.println(mary.getName() + " registered for " + mkt350.getTitle() + " (Term: " + reg1.getTerm() + ", Grade: " + reg1.getGrade() + ")");
        System.out.println("  Linked to Computer Account: " + (reg1.getComputerAccount() != null ? reg1.getComputerAccount().getAcctID() : "None"));


        Registration reg2 = new Registration(mary, mis385, "Fall2010");
        reg2.setComputerAccount(maryAccount); // Link registration to Mary's computer account
        mary.addRegistration(reg2);
        mis385.addRegistration(reg2);
        System.out.println(mary.getName() + " registered for " + mis385.getTitle() + " (Term: " + reg2.getTerm() + ", Grade: " + reg2.getGrade() + ")");
        System.out.println("  Linked to Computer Account: " + (reg2.getComputerAccount() != null ? reg2.getComputerAccount().getAcctID() : "None"));

        Registration reg3 = new Registration(john, cs101, "Fall2024");
        john.addRegistration(reg3);
        cs101.addRegistration(reg3);
        System.out.println(john.getName() + " registered for " + cs101.getTitle() + " (Term: " + reg3.getTerm() + ")");
        System.out.println("\n");

        // 5. Create Tutoring Relationships (using Tutor association class)
        System.out.println("--- Tutoring Relationships ---");
        Tutor tutor1 = new Tutor(john, jane, LocalDate.of(2024, 9, 1), 5);
        john.addTutorRelationship(tutor1); // John is the tutor
        jane.addPupilRelationship(tutor1); // Jane is the pupil
        System.out.println(john.getName() + " tutors " + jane.getName() + " starting " + tutor1.getBeginDate() + " for " + tutor1.getNumberOfHrs() + " hours.");
        System.out.println("\n");

        // 6. Display Student Information and Enrollments
        System.out.println("--- Displaying Student Information ---");
        mary.displayStudentInfo();
        john.displayStudentInfo();
        jane.displayStudentInfo();

        System.out.println("--- Demonstration Complete ---");
    }
}

// Class: Student
class Student {
    private String studentId;
    private String name;
    private String email;
    private List<Registration> registrations; // All registrations this student is involved in
    private List<Tutor> tutoringAsTutor; // Relationships where this student is the tutor
    private List<Tutor> tutoringAsPupil; // Relationships where this student is the pupil

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.registrations = new ArrayList<>();
        this.tutoringAsTutor = new ArrayList<>();
        this.tutoringAsPupil = new ArrayList<>();
        System.out.println("Student " + name + " (ID: " + studentId + ") created.");
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<Registration> getRegistrations() { return new ArrayList<>(registrations); }
    public List<Tutor> getTutoringAsTutor() { return new ArrayList<>(tutoringAsTutor); }
    public List<Tutor> getTutoringAsPupil() { return new ArrayList<>(tutoringAsPupil); }


    // Methods to manage relationships
    public void addRegistration(Registration registration) {
        if (registration != null && !registrations.contains(registration)) {
            registrations.add(registration);
        }
    }

    public void addTutorRelationship(Tutor tutor) {
        if (tutor != null && !tutoringAsTutor.contains(tutor)) {
            tutoringAsTutor.add(tutor);
        }
    }

    public void addPupilRelationship(Tutor pupil) {
        if (pupil != null && !tutoringAsPupil.contains(pupil)) {
            tutoringAsPupil.add(pupil);
        }
    }

    public void displayStudentInfo() {
        System.out.println("\n--- Student: " + name + " (ID: " + studentId + ") ---");
        System.out.println("Email: " + email);
        System.out.println("Courses Registered:");
        if (registrations.isEmpty()) {
            System.out.println("  No courses registered.");
        } else {
            for (Registration reg : registrations) {
                System.out.println("  - " + reg.getCourse().getTitle() + " (Term: " + reg.getTerm() + ", Grade: " + (reg.getGrade() != null ? reg.getGrade() : "N/A") + ")");
                if (reg.getComputerAccount() != null) {
                    System.out.println("    (Linked Computer Account: " + reg.getComputerAccount().getAcctID() + ")");
                }
            }
        }
        System.out.println("Tutoring as Tutor:");
        if (tutoringAsTutor.isEmpty()) {
            System.out.println("  Not tutoring anyone.");
        } else {
            for (Tutor t : tutoringAsTutor) {
                System.out.println("  - Tutors " + t.getPupil().getName() + " for " + t.getNumberOfHrs() + " hours starting " + t.getBeginDate());
            }
        }
        System.out.println("Tutoring as Pupil:");
        if (tutoringAsPupil.isEmpty()) {
            System.out.println("  Not a pupil.");
        } else {
            for (Tutor t : tutoringAsPupil) {
                System.out.println("  - Tutored by " + t.getTutor().getName() + " for " + t.getNumberOfHrs() + " hours starting " + t.getBeginDate());
            }
        }
        System.out.println("------------------------------------");
    }
}

// Class: Course
class Course {
    private String courseCode;
    private String title;
    private int credits;
    private List<Registration> registrations; // All registrations for this course

    public Course(String courseCode, String title, int credits) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.registrations = new ArrayList<>();
        System.out.println("Course " + courseCode + " - '" + title + "' created.");
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public List<Registration> getRegistrations() { return new ArrayList<>(registrations); }

    // Method to manage relationship
    public void addRegistration(Registration registration) {
        if (registration != null && !registrations.contains(registration)) {
            registrations.add(registration);
        }
    }
}

// Class: Registration (Association Class between Student and Course)
class Registration {
    private Student student;
    private Course course;
    private String term;
    private String grade; // Optional, can be set later
    private ComputerAccount computerAccount; // Optional, 0..1 relationship

    public Registration(Student student, Course course, String term) {
        this.student = student;
        this.course = course;
        this.term = term;
        System.out.println("Registration created for " + student.getName() + " in " + course.getCourseCode() + " for " + term);
    }

    // Getters
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public String getTerm() { return term; }
    public String getGrade() { return grade; }
    public ComputerAccount getComputerAccount() { return computerAccount; }

    // Setters
    public void setGrade(String grade) { this.grade = grade; }
    public void setComputerAccount(ComputerAccount computerAccount) {
        this.computerAccount = computerAccount;
        System.out.println("Registration for " + student.getName() + " in " + course.getCourseCode() + " linked to Computer Account " + computerAccount.getAcctID());
    }

    // Override equals and hashCode for proper comparison in Lists
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return Objects.equals(student, that.student) &&
               Objects.equals(course, that.course) &&
               Objects.equals(term, that.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course, term);
    }
}

// Class: ComputerAccount
class ComputerAccount {
    private String acctID;
    private String password;
    private int serverSpace; // in MB

    public ComputerAccount(String acctID, String password, int serverSpace) {
        this.acctID = acctID;
        this.password = password;
        this.serverSpace = serverSpace;
        System.out.println("Computer Account " + acctID + " created.");
    }

    // Getters
    public String getAcctID() { return acctID; }
    public String getPassword() { return password; } // In a real system, avoid returning raw password
    public int getServerSpace() { return serverSpace; }

    // Setters (if attributes can change)
    public void setPassword(String password) { this.password = password; }
    public void setServerSpace(int serverSpace) { this.serverSpace = serverSpace; }
}

// Class: Tutor (Association Class for Student-Student self-association)
class Tutor {
    private Student tutorStudent; // The student who is tutoring
    private Student pupilStudent; // The student being tutored
    private LocalDate beginDate;
    private int numberOfHrs;

    public Tutor(Student tutorStudent, Student pupilStudent, LocalDate beginDate, int numberOfHrs) {
        this.tutorStudent = tutorStudent;
        this.pupilStudent = pupilStudent;
        this.beginDate = beginDate;
        this.numberOfHrs = numberOfHrs;
        System.out.println("Tutoring relationship established: " + tutorStudent.getName() + " -> " + pupilStudent.getName());
    }

    // Getters
    public Student getTutor() { return tutorStudent; }
    public Student getPupil() { return pupilStudent; }
    public LocalDate getBeginDate() { return beginDate; }
    public int getNumberOfHrs() { return numberOfHrs; }

    // Setters (if attributes can change)
    public void setNumberOfHrs(int numberOfHrs) { this.numberOfHrs = numberOfHrs; }

    // Override equals and hashCode for proper comparison in Lists
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tutor tutor = (Tutor) o;
        return numberOfHrs == tutor.numberOfHrs &&
               Objects.equals(tutorStudent, tutor.tutorStudent) &&
               Objects.equals(pupilStudent, tutor.pupilStudent) &&
               Objects.equals(beginDate, tutor.beginDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tutorStudent, pupilStudent, beginDate, numberOfHrs);
    }
}