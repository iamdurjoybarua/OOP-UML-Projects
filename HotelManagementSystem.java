import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // For generating unique IDs

// Staff.java (Base class for Doctor and Nurse)
class Staff {
    protected String id;
    protected String name;
    protected String position;
    protected String contactNumber;
    protected String department;
    protected String workShift;

    public Staff(String id, String name, String position, String contactNumber, String department, String workShift) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.contactNumber = contactNumber;
        this.department = department;
        this.workShift = workShift;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getContactNumber() { return contactNumber; }
    public String getDepartment() { return department; }
    public String getWorkShift() { return workShift; }

    // Operations
    public void scheduleAppointment(Appointment appointment) {
        System.out.println(this.name + " (" + this.position + ") is scheduling appointment " + appointment.getId() + " for " + appointment.getPatient().getName());
        // In a real system, this would involve checking availability and updating schedules.
    }

    public void updatePatientRecord(Patient patient, MedicalRecord record, String updateDetails) {
        System.out.println(this.name + " (" + this.position + ") updating medical record for " + patient.getName() + ": " + updateDetails);
        record.addRecord(updateDetails); // Adding a simple record entry
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(id, staff.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Doctor.java (inherits from Staff)
class Doctor extends Staff {
    private String specialty;
    private String licenseNumber;

    public Doctor(String id, String name, String contactNumber, String department, String workShift, String specialty, String licenseNumber) {
        super(id, name, "Doctor", contactNumber, department, workShift);
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
    }

    // Getters
    public String getSpecialty() { return specialty; }
    public String getLicenseNumber() { return licenseNumber; }

    // Operations
    public void performSurgery(Patient patient, String surgeryDetails) {
        System.out.println("Dr. " + this.name + " is performing surgery on " + patient.getName() + ": " + surgeryDetails);
        // This would involve complex logic, updating patient status, etc.
    }

    public void diagnosePatient(Patient patient, String diagnosis) {
        System.out.println("Dr. " + this.name + " diagnosed " + patient.getName() + " with: " + diagnosis);
        // This would typically add an entry to the patient's medical record.
        if (patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().addRecord("Diagnosis by Dr. " + this.name + ": " + diagnosis);
        }
    }
}

// Nurse.java (inherits from Staff)
class Nurse extends Staff {
    private String shift; // e.g., "Day", "Night"

    public Nurse(String id, String name, String contactNumber, String department, String workShift, String shift) {
        super(id, name, "Nurse", contactNumber, department, workShift);
        this.shift = shift;
    }

    // Getter
    public String getShift() { return shift; }

    // Operations
    public void administerMedication(Patient patient, String medication) {
        System.out.println("Nurse " + this.name + " administered " + medication + " to " + patient.getName());
        if (patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().addRecord("Medication administered by Nurse " + this.name + ": " + medication);
        }
    }

    public void assistDoctor(Doctor doctor, String task) {
        System.out.println("Nurse " + this.name + " is assisting Dr. " + doctor.getName() + " with: " + task);
    }
}

// Patient.java
class Patient {
    private String id;
    private String name;
    private String dob; // Date of Birth
    private String gender;
    private String contactNumber;
    private String address;
    private String medicalHistory;
    private String allergies;
    private MedicalRecord medicalRecord; // 1-to-1 relationship with MedicalRecord
    private List<Appointment> appointments; // 1-to-many relationship with Appointment

    public Patient(String id, String name, String dob, String gender, String contactNumber, String address, String medicalHistory, String allergies) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.appointments = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public String getMedicalHistory() { return medicalHistory; }
    public String getAllergies() { return allergies; }
    public MedicalRecord getMedicalRecord() { return medicalRecord; }
    public List<Appointment> getAppointments() { return appointments; }

    // Setter for MedicalRecord (to link it after creation)
    public void setMedicalRecord(MedicalRecord medicalRecord) { this.medicalRecord = medicalRecord; }

    // Operations
    public Appointment bookAppointment(Date date, String time, Staff staff) {
        String appointmentId = "APP-" + UUID.randomUUID().toString().substring(0, 8);
        Appointment newAppointment = new Appointment(appointmentId, date, time, this, staff);
        this.appointments.add(newAppointment);
        System.out.println(this.name + " booked an appointment with " + staff.getName() + " on " + date + " at " + time);
        return newAppointment;
    }

    public void viewTreatmentHistory() {
        System.out.println("--- Treatment History for " + this.name + " ---");
        if (medicalRecord != null) {
            medicalRecord.viewRecord();
        } else {
            System.out.println("No medical record available.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// MedicalRecord.java
class MedicalRecord {
    private String id;
    private Patient patient; // 1-to-1 relationship with Patient (has)
    private List<Treatment> treatments; // 0-to-many relationship with Treatment (includes)
    private List<String> recordEntries; // Simple list to store textual record entries

    public MedicalRecord(String id, Patient patient) {
        this.id = id;
        this.patient = patient;
        this.treatments = new ArrayList<>();
        this.recordEntries = new ArrayList<>();
        this.recordEntries.add("Medical Record created for " + patient.getName() + " on " + new Date());
    }

    // Getters
    public String getId() { return id; }
    public Patient getPatient() { return patient; }
    public List<Treatment> getTreatments() { return treatments; }
    public List<String> getRecordEntries() { return recordEntries; }

    // Operations
    public void addRecord(String entry) {
        this.recordEntries.add(new Date() + ": " + entry);
        System.out.println("Added record entry for " + patient.getName() + ": " + entry);
    }

    public void viewRecord() {
        System.out.println("Medical Record ID: " + id + " for Patient: " + patient.getName());
        System.out.println("--- Entries ---");
        if (recordEntries.isEmpty()) {
            System.out.println("No entries yet.");
        } else {
            for (String entry : recordEntries) {
                System.out.println(entry);
            }
        }
        System.out.println("--- Treatments ---");
        if (treatments.isEmpty()) {
            System.out.println("No treatments recorded yet.");
        } else {
            for (Treatment treatment : treatments) {
                System.out.println("  - " + treatment.getDescription() + " (Cost: $" + String.format("%.2f", treatment.getCost()) + ", Date: " + treatment.getTreatmentDate() + ")");
            }
        }
    }
}

// Treatment.java
class Treatment {
    private String id;
    private String description;
    private double cost;
    private Date treatmentDate;
    private Doctor providedBy; // 1-to-1 relationship with Doctor (provided by)

    public Treatment(String id, String description, double cost, Date treatmentDate, Doctor providedBy) {
        this.id = id;
        this.description = description;
        this.cost = cost;
        this.treatmentDate = treatmentDate;
        this.providedBy = providedBy;
    }

    // Getters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public double getCost() { return cost; }
    public Date getTreatmentDate() { return treatmentDate; }
    public Doctor getProvidedBy() { return providedBy; }

    // Operations
    public void addTreatment(MedicalRecord medicalRecord) {
        medicalRecord.getTreatments().add(this);
        medicalRecord.addRecord("Treatment added: " + description + " by Dr. " + providedBy.getName());
        System.out.println("Treatment " + description + " added to medical record.");
    }

    public void viewTreatmentDetails() {
        System.out.println("--- Treatment Details ---");
        System.out.println("ID: " + id);
        System.out.println("Description: " + description);
        System.out.println("Cost: $" + String.format("%.2f", cost));
        System.out.println("Date: " + treatmentDate);
        System.out.println("Provided by: Dr. " + providedBy.getName() + " (" + providedBy.getSpecialty() + ")");
    }
}

// Appointment.java
class Appointment {
    private String id;
    private Date date;
    private String time;
    private Patient patient; // 1-to-1 relationship with Patient (participates)
    private Staff staff;     // 1-to-1 relationship with Staff (conducted by)

    public Appointment(String id, Date date, String time, Patient patient, Staff staff) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.patient = patient;
        this.staff = staff;
    }

    // Getters
    public String getId() { return id; }
    public Date getDate() { return date; }
    public String getTime() { return time; }
    public Patient getPatient() { return patient; }
    public Staff getStaff() { return staff; }

    // Operations
    public void createAppointment() {
        System.out.println("Appointment " + id + " created for " + patient.getName() + " with " + staff.getName() + " on " + date + " at " + time);
        // This would involve adding to staff's schedule and patient's appointment list.
        // (Already done in Patient.bookAppointment)
    }

    public void cancelAppointment() {
        System.out.println("Appointment " + id + " for " + patient.getName() + " cancelled.");
        // In a real system, this would remove the appointment from schedules and lists.
    }
}

// Main class to demonstrate the Hospital Management System
public class HospitalManagementSystem { // This is the public class, so the file must be named HospitalManagementSystem.java
    public static void main(String[] args) {
        // --- 1. Create Staff ---
        Doctor drSmith = new Doctor("D001", "Dr. Smith", "987-654-3210", "Cardiology", "Day", "Cardiologist", "LIC-12345");
        Nurse nurseAlice = new Nurse("N001", "Nurse Alice", "111-222-3333", "Emergency", "Night", "Night");
        Doctor drJones = new Doctor("D002", "Dr. Jones", "555-123-4567", "General Medicine", "Day", "General Practitioner", "LIC-67890");

        // --- 2. Create Patients ---
        Patient patientA = new Patient("P001", "John Doe", "1985-03-15", "Male", "999-888-7777", "123 Elm St", "None", "Penicillin");
        Patient patientB = new Patient("P002", "Jane Doe", "1990-07-22", "Female", "777-666-5555", "456 Oak Ave", "Asthma", "None");

        // --- 3. Create Medical Records and link to Patients ---
        MedicalRecord recordA = new MedicalRecord("MR001", patientA);
        patientA.setMedicalRecord(recordA);

        MedicalRecord recordB = new MedicalRecord("MR002", patientB);
        patientB.setMedicalRecord(recordB);

        System.out.println("\n--- Patient Operations ---");
        // John Doe books an appointment
        Date today = new Date();
        Appointment appt1 = patientA.bookAppointment(today, "10:00 AM", drSmith);
        drSmith.scheduleAppointment(appt1); // Doctor also acknowledges the appointment

        // Jane Doe books an appointment with a nurse
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24)); // Add one day
        Appointment appt2 = patientB.bookAppointment(tomorrow, "02:00 PM", nurseAlice);
        nurseAlice.scheduleAppointment(appt2);

        System.out.println("\n--- Doctor Operations ---");
        drSmith.diagnosePatient(patientA, "Common cold, mild");
        drSmith.updatePatientRecord(patientA, recordA, "Prescribed rest and fluids.");

        // Dr. Jones performs a surgery (example)
        drJones.performSurgery(patientB, "Appendectomy");
        drJones.diagnosePatient(patientB, "Post-surgery recovery");
        drJones.updatePatientRecord(patientB, recordB, "Post-op instructions given.");

        System.out.println("\n--- Nurse Operations ---");
        nurseAlice.administerMedication(patientA, "Cough Syrup");
        nurseAlice.assistDoctor(drSmith, "Preparing examination room.");

        System.out.println("\n--- Treatment and Medical Record ---");
        // Dr. Smith adds a treatment for John Doe
        Treatment treatment1 = new Treatment("T001", "Antibiotics Course", 50.00, new Date(), drSmith);
        treatment1.addTreatment(recordA);
        treatment1.viewTreatmentDetails();

        // View John Doe's full treatment history
        patientA.viewTreatmentHistory();

        // View Jane Doe's medical record
        patientB.viewTreatmentHistory();

        System.out.println("\n--- Appointment Management ---");
        appt1.createAppointment(); // This just prints a message, already "created" when booked
        appt2.cancelAppointment(); // Jane Doe cancels her appointment with Nurse Alice
        patientB.getAppointments().remove(appt2); // Remove from patient's list as well
    }
}
