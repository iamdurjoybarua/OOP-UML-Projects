import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;

// =============================================================================
// Enums (for Gender)
// =============================================================================
enum Gender {
    MALE, FEMALE, OTHER
}

// =============================================================================
// Base Classes
// =============================================================================

class Person {
    private String title;
    private String givenName;
    private String middleName;
    private String familyName;
    private LocalDate birthDate;
    private Gender gender;
    private String homeAddress;
    private String phone;

    public Person(String title, String givenName, String middleName, String familyName,
                  LocalDate birthDate, Gender gender, String homeAddress, String phone) {
        this.title = title;
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.homeAddress = homeAddress;
        this.phone = phone;
        System.out.println("Person " + getFullName() + " created.");
    }

    // Getters
    public String getTitle() { return title; }
    public String getGivenName() { return givenName; }
    public String getMiddleName() { return middleName; }
    public String getFamilyName() { return familyName; }
    public LocalDate getBirthDate() { return birthDate; } // Public getter for birthDate
    public Gender getGender() { return gender; }
    public String getHomeAddress() { return homeAddress; }
    public String getPhone() { return phone; }

    // Derived attribute: /name: FullName
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (givenName != null && !givenName.isEmpty()) fullName.append(givenName);
        if (middleName != null && !middleName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName);
        }
        if (familyName != null && !familyName.isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(familyName);
        }
        return fullName.toString();
    }

    // Other potential methods for a Person (e.g., updateContactInfo)
}

class Hospital {
    private String name; // {id}
    private String address; // /address - derived from name or separate
    private String phone; // Phone

    // Composition: Hospital has Department (1-to-1 as per diagram, though unusual for real hospitals)
    private Department department;

    // Association: Hospital employs Persons (a broader list, including staff and possibly patients if they are considered "members" of the hospital system)
    private List<Person> employedPersons;

    public Hospital(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.employedPersons = new ArrayList<>();
        System.out.println("Hospital '" + name + "' created.");
    }

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public Department getDepartment() { return department; }
    public List<Person> getEmployedPersons() { return new ArrayList<>(employedPersons); }

    // Setters for composition
    public void setDepartment(Department department) {
        if (this.department != null) {
            System.out.println("Warning: Hospital already has a department. Replacing.");
        }
        this.department = department;
        System.out.println("Department linked to Hospital " + name);
    }

    // Business Methods
    public void addEmployee(Person person) {
        if (!employedPersons.contains(person)) {
            employedPersons.add(person);
            System.out.println(person.getFullName() + " added as an employee/member to " + name);
        }
    }

    public void removeEmployee(Person person) {
        if (employedPersons.remove(person)) {
            System.out.println(person.getFullName() + " removed from " + name + " employees/members.");
        }
    }

    public void displayHospitalInfo() {
        System.out.println("\n--- Hospital Info: " + name + " ---");
        System.out.println("Address: " + address);
        System.out.println("Phone: " + phone);
        System.out.println("Number of employees/members: " + employedPersons.size());
        if (department != null) {
            System.out.println("Main Department: " + department.getName());
        }
        System.out.println("------------------------------");
    }
}

class Department {
    private String name; // Assuming a name for the department

    public Department(String name) {
        this.name = name;
        System.out.println("Department '" + name + "' created.");
    }

    public String getName() { return name; }
}

// =============================================================================
// Subclasses of Person
// =============================================================================

class Patient extends Person {
    private String id; // {id}
    private LocalDate acceptedDate; // accepted
    private String sicknessHistory; // sickness: History
    private List<String> prescriptions; // prescription[*]
    private List<String> allergies;     // allergies[*]
    private List<String> specialRequests; // specialRwqts[*] - corrected in Java code

    public Patient(String title, String givenName, String middleName, String familyName,
                   LocalDate birthDate, Gender gender, String homeAddress, String phone,
                   String id, LocalDate acceptedDate, String sicknessHistory,
                   List<String> prescriptions, List<String> allergies, List<String> specialRequests) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone);
        this.id = id;
        this.acceptedDate = acceptedDate;
        this.sicknessHistory = sicknessHistory;
        this.prescriptions = prescriptions != null ? new ArrayList<>(prescriptions) : new ArrayList<>();
        this.allergies = allergies != null ? new ArrayList<>(allergies) : new ArrayList<>();
        this.specialRequests = specialRequests != null ? new ArrayList<>(specialRequests) : new ArrayList<>();
        System.out.println("Patient " + getFullName() + " (ID: " + id + ") registered.");
    }

    // Getters
    public String getId() { return id; }
    // CORRECTED LINE: Use getBirthDate() from superclass
    public int getAge() {
        return LocalDate.now().getYear() - getBirthDate().getYear();
    }
    public LocalDate getAcceptedDate() { return acceptedDate; }
    public String getSicknessHistory() { return sicknessHistory; }
    public List<String> getPrescriptions() { return new ArrayList<>(prescriptions); }
    public List<String> getAllergies() { return new ArrayList<>(allergies); }
    public List<String> getSpecialRequests() { return new ArrayList<>(specialRequests); }

    public void displayPatientInfo() {
        System.out.println("\n--- Patient Info (ID: " + id + ") ---");
        System.out.println("Name: " + getFullName());
        System.out.println("Gender: " + getGender());
        System.out.println("Age: " + getAge());
        System.out.println("Accepted Date: " + acceptedDate);
        System.out.println("Sickness: " + sicknessHistory);
        System.out.println("Allergies: " + (allergies.isEmpty() ? "None" : String.join(", ", allergies)));
        System.out.println("Special Requests: " + (specialRequests.isEmpty() ? "None" : String.join(", ", specialRequests)));
        System.out.println("Prescriptions: " + (prescriptions.isEmpty() ? "None" : String.join(", ", prescriptions)));
        System.out.println("------------------------------");
    }
}

class Staff extends Person {
    private LocalDate joinedDate;
    private List<String> education;
    private List<String> certifications; // Corrected from 'certifiction'
    private List<String> languages;

    public Staff(String title, String givenName, String middleName, String familyName,
                 LocalDate birthDate, Gender gender, String homeAddress, String phone,
                 LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone);
        this.joinedDate = joinedDate;
        this.education = education != null ? new ArrayList<>(education) : new ArrayList<>();
        this.certifications = certifications != null ? new ArrayList<>(certifications) : new ArrayList<>();
        this.languages = languages != null ? new ArrayList<>(languages) : new ArrayList<>();
        System.out.println("Staff member " + getFullName() + " joined.");
    }

    // Getters
    public LocalDate getJoinedDate() { return joinedDate; }
    public List<String> getEducation() { return new ArrayList<>(education); }
    public List<String> getCertifications() { return new ArrayList<>(certifications); }
    public List<String> getLanguages() { return new ArrayList<>(languages); }

    public void displayStaffInfo() {
        System.out.println("\n--- Staff Info: " + getFullName() + " ---");
        System.out.println("Role: " + this.getClass().getSimpleName());
        System.out.println("Joined: " + joinedDate);
        System.out.println("Education: " + (education.isEmpty() ? "None" : String.join(", ", education)));
        System.out.println("Certifications: " + (certifications.isEmpty() ? "None" : String.join(", ", certifications)));
        System.out.println("Languages: " + (languages.isEmpty() ? "None" : String.join(", ", languages)));
        System.out.println("------------------------------");
    }
}

// =============================================================================
// Staff Sub-hierarchies
// =============================================================================

// --- Operations Staff Hierarchy ---
class OperationsStaff extends Staff {
    public OperationsStaff(String title, String givenName, String middleName, String familyName,
                           LocalDate birthDate, Gender gender, String homeAddress, String phone,
                           LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println(getFullName() + " is Operations Staff.");
    }

    // Example interaction: Operations staff can manage patient's status
    public void managePatientStatus(Patient patient, String newStatus) {
        System.out.println(this.getFullName() + " is managing " + patient.getFullName() + "'s status to: " + newStatus);
        // In a real system, patient.setStatus(newStatus) would be called if Patient had a status.
    }
}

class Doctor extends OperationsStaff {
    private List<String> specialties;
    private List<String> locations;

    public Doctor(String title, String givenName, String middleName, String familyName,
                  LocalDate birthDate, Gender gender, String homeAddress, String phone,
                  LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages,
                  List<String> specialties, List<String> locations) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        this.specialties = specialties != null ? new ArrayList<>(specialties) : new ArrayList<>();
        this.locations = locations != null ? new ArrayList<>(locations) : new ArrayList<>();
        System.out.println("Doctor " + getFullName() + " created.");
    }

    // Getters
    public List<String> getSpecialties() { return new ArrayList<>(specialties); }
    public List<String> getLocations() { return new ArrayList<>(locations); }

    // Example: Doctor can prescribe medication
    public void prescribeMedication(Patient patient, String medication) {
        System.out.println("Dr. " + getFullName() + " prescribed " + medication + " for " + patient.getFullName());
        // patient.addPrescription(medication); // If patient had addPrescription method
    }
}

class Nurse extends OperationsStaff {
    public Nurse(String title, String givenName, String middleName, String familyName,
                 LocalDate birthDate, Gender gender, String homeAddress, String phone,
                 LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Nurse " + getFullName() + " created.");
    }

    // Example: Nurse can administer medication
    public void administerMedication(Patient patient, String medication) {
        System.out.println("Nurse " + getFullName() + " administered " + medication + " to " + patient.getFullName());
    }
}

// --- Administrative Staff Hierarchy ---
class AdministrativeStaff extends Staff {
    public AdministrativeStaff(String title, String givenName, String middleName, String familyName,
                               LocalDate birthDate, Gender gender, String homeAddress, String phone,
                               LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println(getFullName() + " is Administrative Staff.");
    }
    // Example: Administrative staff can handle billing
    public void handleBilling(Patient patient) {
        System.out.println(this.getFullName() + " is handling billing for " + patient.getFullName());
    }
}

class FrontDeskStaff extends AdministrativeStaff {
    public FrontDeskStaff(String title, String givenName, String middleName, String familyName,
                          LocalDate birthDate, Gender gender, String homeAddress, String phone,
                          LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Front Desk Staff " + getFullName() + " created.");
    }

    // Example: Front Desk Staff can check-in patients
    public void checkInPatient(Patient patient) {
        System.out.println(getFullName() + " checked in patient " + patient.getFullName());
    }
}

class Receptionist extends FrontDeskStaff {
    public Receptionist(String title, String givenName, String middleName, String familyName,
                        LocalDate birthDate, Gender gender, String homeAddress, String phone,
                        LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Receptionist " + getFullName() + " created.");
    }

    // Example: Receptionist can schedule appointments
    public void scheduleAppointment(Patient patient, Doctor doctor, LocalDate date) {
        System.out.println(getFullName() + " scheduled appointment for " + patient.getFullName() + " with Dr. " + doctor.getFullName() + " on " + date);
    }
}

// --- Technical Staff Hierarchy ---
class TechnicalStaff extends Staff {
    public TechnicalStaff(String title, String givenName, String middleName, String familyName,
                          LocalDate birthDate, Gender gender, String homeAddress, String phone,
                          LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println(getFullName() + " is Technical Staff.");
    }

    // Example: Technical staff can perform equipment maintenance
    public void performMaintenance(String equipmentName) {
        System.out.println(this.getFullName() + " performed maintenance on " + equipmentName);
    }
}

class Technician extends TechnicalStaff {
    public Technician(String title, String givenName, String middleName, String familyName,
                      LocalDate birthDate, Gender gender, String homeAddress, String phone,
                      LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Technician " + getFullName() + " created.");
    }

    // Example: Technician can fix medical devices
    public void fixDevice(String deviceName) {
        System.out.println(getFullName() + " fixed medical device: " + deviceName);
    }
}

class Technologist extends TechnicalStaff {
    public Technologist(String title, String givenName, String middleName, String familyName,
                        LocalDate birthDate, Gender gender, String homeAddress, String phone,
                        LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Technologist " + getFullName() + " created.");
    }

    // Example: Technologist can analyze lab results
    public void analyzeLabResults(String testName) {
        System.out.println(getFullName() + " analyzed lab results for: " + testName);
    }
}

class SurgicalTechnologist extends Technologist {
    public SurgicalTechnologist(String title, String givenName, String middleName, String familyName,
                                LocalDate birthDate, Gender gender, String homeAddress, String phone,
                                LocalDate joinedDate, List<String> education, List<String> certifications, List<String> languages) {
        super(title, givenName, middleName, familyName, birthDate, gender, homeAddress, phone,
              joinedDate, education, certifications, languages);
        System.out.println("Surgical Technologist " + getFullName() + " created.");
    }

    // Example: Surgical Technologist can assist in surgery
    public void assistInSurgery(String procedureName) {
        System.out.println(getFullName() + " is assisting in surgery: " + procedureName);
    }
}


// =============================================================================
// Main Demonstration Class
// =============================================================================
public class HospitalManagementDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting Hospital Management System Demonstration ---");

        // 1. Create Hospital and Department
        Hospital generalHospital = new Hospital("General City Hospital", "456 Oak St, Metropolis", "555-9876");
        Department emergencyDept = new Department("Emergency Department");
        generalHospital.setDepartment(emergencyDept);

        // 2. Create Staff Members
        Doctor drSmith = new Doctor("Dr.", "Alice", "B.", "Smith", LocalDate.of(1975, 5, 15), Gender.FEMALE,
                "789 Elm St", "555-1235", LocalDate.of(2000, 1, 1),
                Arrays.asList("MD", "Residency"), Arrays.asList("Board Certified"), Arrays.asList("English"),
                Arrays.asList("Cardiology", "Internal Medicine"), Arrays.asList("Main Clinic", "ER"));
        generalHospital.addEmployee(drSmith);

        Nurse nurseJones = new Nurse("Ms.", "Betty", "", "Jones", LocalDate.of(1988, 11, 20), Gender.FEMALE,
                "101 Pine St", "555-2345", LocalDate.of(2010, 3, 10),
                Arrays.asList("BSN"), Arrays.asList("RN License"), Arrays.asList("English", "Spanish"));
        generalHospital.addEmployee(nurseJones);

        Receptionist annWhite = new Receptionist("Ms.", "Ann", "", "White", LocalDate.of(1995, 7, 7), Gender.FEMALE,
                "202 Birch Ave", "555-3456", LocalDate.of(2018, 9, 1),
                Arrays.asList("High School Diploma"), new ArrayList<>(), Arrays.asList("English"));
        generalHospital.addEmployee(annWhite);

        SurgicalTechnologist techMike = new SurgicalTechnologist("Mr.", "Mike", "", "Green", LocalDate.of(1990, 2, 28), Gender.MALE,
                "303 Cedar Ln", "555-4567", LocalDate.of(2015, 6, 1),
                Arrays.asList("Associate Degree"), Arrays.asList("CST"), Arrays.asList("English"));
        generalHospital.addEmployee(techMike);

        // 3. Create a Patient
        Patient patBrown = new Patient("Mr.", "Charles", "", "Brown", LocalDate.of(1960, 1, 1), Gender.MALE,
                "404 Maple Dr", "555-5678", "P001", LocalDate.of(2025, 5, 20),
                "Hypertension", Arrays.asList("Lisinopril"), Arrays.asList("Penicillin"), Arrays.asList("Wheelchair access needed"));
        generalHospital.addEmployee(patBrown); // Adding patient to hospital's list of 'persons' (broadly)

        // Display Info
        generalHospital.displayHospitalInfo();
        drSmith.displayStaffInfo();
        patBrown.displayPatientInfo();

        // 4. Simulate Interactions
        System.out.println("\n--- Simulating Interactions ---");

        drSmith.prescribeMedication(patBrown, "New Blood Pressure Med");
        nurseJones.administerMedication(patBrown, "Pain Reliever");
        annWhite.checkInPatient(patBrown);
        annWhite.scheduleAppointment(patBrown, drSmith, LocalDate.of(2025, 6, 15));
        techMike.assistInSurgery("Appendectomy");

        System.out.println("\n--- Hospital Management System Demonstration Complete ---");
    }
}
