import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // For generating unique IDs

// AirlineReservationSystem.java (The main system class, also contains main method)
public class AirlineReservationSystem { // This class is now public and contains main
    private List<Reservation> allReservations;
    private List<Employee> allEmployees;

    public AirlineReservationSystem() {
        this.allReservations = new ArrayList<>();
        this.allEmployees = new ArrayList<>();
    }

    // Operations (as per diagram)
    public Reservation createReservation(List<Passenger> passengers, Date reservationDate) {
        String reservationID = "RES-" + UUID.randomUUID().toString().substring(0, 8);
        Reservation newReservation = new Reservation(reservationID, reservationDate, "Pending");
        for (Passenger p : passengers) {
            newReservation.addPassenger(p);
        }
        this.allReservations.add(newReservation);
        System.out.println("Reservation " + reservationID + " created for " + passengers.size() + " passenger(s) on " + reservationDate + ".");
        return newReservation;
    }

    public void cancelReservation(Reservation reservation) {
        if (this.allReservations.remove(reservation)) {
            reservation.setStatus("Cancelled");
            // Also cancel associated tickets
            for (Ticket ticket : reservation.getTickets()) {
                ticket.refundTicket(); // Calls refund logic for each ticket
            }
            System.out.println("Reservation " + reservation.getReservationID() + " cancelled successfully.");
        } else {
            System.out.println("Reservation " + reservation.getReservationID() + " not found.");
        }
    }

    public void modifyReservation(Reservation reservation, Date newDate) {
        if (this.allReservations.contains(reservation)) {
            reservation.setDate(newDate);
            reservation.setStatus("Modified");
            System.out.println("Reservation " + reservation.getReservationID() + " modified to new date: " + newDate + ".");
            // In a real system, this would involve re-checking availability, possibly new seat assignments, etc.
        } else {
            System.out.println("Reservation " + reservation.getReservationID() + " not found for modification.");
        }
    }

    public Reservation viewReservation(String reservationID) {
        for (Reservation res : allReservations) {
            if (res.getReservationID().equals(reservationID)) {
                System.out.println("Viewing details for Reservation ID: " + reservationID);
                res.getReservationDetails(); // Prints details
                return res;
            }
        }
        System.out.println("Reservation " + reservationID + " not found.");
        return null;
    }

    // Method to add employees to the system
    public void addEmployee(Employee employee) {
        if (!allEmployees.contains(employee)) {
            allEmployees.add(employee);
            System.out.println("Employee " + employee.getName() + " (" + employee.getEmployeeID() + ") added to system.");
        } else {
            System.out.println("Employee " + employee.getName() + " already exists.");
        }
    }

    // Getters (for system overview/access)
    public List<Reservation> getAllReservations() {
        return allReservations;
    }

    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

    // Main method for demonstration
    public static void main(String[] args) {
        // Create the main system
        AirlineReservationSystem system = new AirlineReservationSystem();

        // --- 1. Create Employees ---
        Employee agentJohn = new Employee("E001", "John Doe", "Reservation Agent");
        Employee managerJane = new Employee("E002", "Jane Smith", "Operations Manager");
        system.addEmployee(agentJohn);
        system.addEmployee(managerJane);

        // --- 2. Create Passengers ---
        // Using java.util.Date for DOB for simplicity. In real apps, consider java.time.LocalDate.
        Date dobAlice = new Date(90, 5, 10); // Year 1990, Month 6 (June), Day 10
        Date dobBob = new Date(85, 10, 20); // Year 1985, Month 11 (Nov), Day 20

        Passenger alice = new Passenger("P001", "Alice Green", dobAlice, "alice@email.com");
        Passenger bob = new Passenger("P002", "Bob White", dobBob, "bob@email.com");
        Passenger charlie = new Passenger("P003", "Charlie Brown", new Date(95, 0, 1), "charlie@email.com");

        System.out.println("\n--- Reservation Creation ---");
        // Alice and Bob make a reservation together
        List<Passenger> passengersForRes1 = new ArrayList<>();
        passengersForRes1.add(alice);
        passengersForRes1.add(bob);
        Reservation res1 = system.createReservation(passengersForRes1, new Date()); // Today's date

        // Charlie makes a separate reservation
        List<Passenger> passengersForRes2 = new ArrayList<>();
        passengersForRes2.add(charlie);
        Reservation res2 = system.createReservation(passengersForRes2, new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 7))); // One week from now

        System.out.println("\n--- Ticket Creation ---");
        // Alice books a ticket for res1
        Ticket aliceTicket = alice.bookTicket(res1, "A12", 250.00f);

        // Bob books a ticket for res1
        Ticket bobTicket = bob.bookTicket(res1, "A13", 250.00f);

        // Charlie books a ticket for res2
        Ticket charlieTicket = charlie.bookTicket(res2, "B05", 180.00f);

        // Check if tickets are properly linked
        System.out.println("Tickets associated with res1: " + res1.getTickets().size());
        System.out.println("Tickets associated with alice: " + alice.getBookedTickets().size());

        System.out.println("\n--- Employee Management ---");
        agentJohn.manageReservation(res1, "Confirmed");
        agentJohn.assistPassenger(alice, "Checked baggage allowance.");

        managerJane.manageReservation(res2, "Pending Payment");
        managerJane.assistPassenger(bob, "Updated contact number.");

        System.out.println("\n--- Viewing Reservation Details ---");
        system.viewReservation(res1.getReservationID());
        system.viewReservation(res2.getReservationID());

        System.out.println("\n--- Modifying Reservation ---");
        Date newDateForRes1 = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30)); // One month from now
        system.modifyReservation(res1, newDateForRes1);
        system.viewReservation(res1.getReservationID());

        System.out.println("\n--- Cancelling Ticket ---");
        res1.cancelTicket(bobTicket); // Bob's ticket is cancelled
        system.viewReservation(res1.getReservationID()); // Verify cancellation reflected in reservation details

        System.out.println("\n--- Cancelling Reservation ---");
        system.cancelReservation(res2); // Charlie's entire reservation is cancelled
        system.viewReservation(res2.getReservationID()); // Should show cancelled status for reservation and ticket
    }
}

// Employee.java (Non-public class as it's in the same file as the public AirlineReservationSystem)
class Employee {
    private String employeeID;
    private String name;
    private String role; // e.g., "Agent", "Manager"

    public Employee(String employeeID, String name, String role) {
        this.employeeID = employeeID;
        this.name = name;
        this.role = role;
    }

    // Getters
    public String getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public String getRole() { return role; }

    // Operations
    public void manageReservation(Reservation reservation, String newStatus) {
        System.out.println(this.name + " (" + this.role + ") is managing reservation " + reservation.getReservationID() + " to set status to: " + newStatus);
        reservation.setStatus(newStatus); // Update status of the reservation
    }

    public void assistPassenger(Passenger passenger, String assistanceDetails) {
        System.out.println(this.name + " (" + this.role + ") assisting " + passenger.getName() + ": " + assistanceDetails);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeID, employee.employeeID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeID);
    }
}

// Reservation.java (Non-public class)
class Reservation {
    private String reservationID;
    private Date date;
    private String status; // e.g., "Pending", "Confirmed", "Cancelled", "Modified"
    private List<Passenger> passengers; // 1-to-many relationship: includes
    private List<Ticket> tickets; // 1-to-many relationship: generates

    public Reservation(String reservationID, Date date, String status) {
        this.reservationID = reservationID;
        this.date = date;
        this.status = status;
        this.passengers = new ArrayList<>();
        this.tickets = new ArrayList<>();
    }

    // Getters
    public String getReservationID() { return reservationID; }
    public Date getDate() { return date; }
    public String getStatus() { return status; }
    public List<Passenger> getPassengers() { return passengers; }
    public List<Ticket> getTickets() { return tickets; }

    // Setters (for modification)
    public void setDate(Date date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }

    // Helper to add passengers
    public void addPassenger(Passenger passenger) {
        if (!passengers.contains(passenger)) {
            passengers.add(passenger);
            System.out.println("Passenger " + passenger.getName() + " added to reservation " + reservationID);
        }
    }

    // Operations
    public Ticket createTicket(Passenger passenger, String seatNumber, float price) {
        if (!this.passengers.contains(passenger)) {
            System.out.println("Error: Passenger " + passenger.getName() + " is not part of this reservation.");
            return null;
        }
        String ticketID = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        Ticket newTicket = new Ticket(ticketID, price, seatNumber, "Issued");
        this.tickets.add(newTicket);
        // Link ticket back to reservation implicitly via creation, and to passenger
        passenger.addTicket(newTicket); // Passenger 'books' a ticket
        newTicket.setAssociatedReservation(this); // Set the reservation the ticket belongs to

        System.out.println("Ticket " + ticketID + " generated for " + passenger.getName() + " on reservation " + reservationID + " for seat " + seatNumber + ".");
        return newTicket;
    }

    public void cancelTicket(Ticket ticket) {
        if (this.tickets.remove(ticket)) {
            ticket.refundTicket(); // Call the ticket's refund logic
            System.out.println("Ticket " + ticket.getTicketID() + " cancelled from reservation " + reservationID + ".");
        } else {
            System.out.println("Ticket " + ticket.getTicketID() + " not found in reservation " + reservationID + ".");
        }
    }

    public void getReservationDetails() {
        System.out.println("\n--- Reservation Details for " + reservationID + " ---");
        System.out.println("Date: " + date);
        System.out.println("Status: " + status);
        System.out.println("Passengers (" + passengers.size() + "):");
        for (Passenger p : passengers) {
            System.out.println("  - " + p.getName() + " (ID: " + p.getPassengerID() + ")");
        }
        System.out.println("Tickets (" + tickets.size() + "):");
        if (tickets.isEmpty()) {
            System.out.println("  No tickets issued yet.");
        } else {
            for (Ticket t : tickets) {
                System.out.println("  - Ticket ID: " + t.getTicketID() + ", Price: $" + String.format("%.2f", t.getPrice()) + ", Seat: " + t.getSeatNumber() + ", Status: " + t.getStatus());
            }
        }
        System.out.println("----------------------------------------");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationID, that.reservationID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationID);
    }
}

// Passenger.java (Non-public class)
class Passenger {
    private String passengerID;
    private String name;
    private Date dob; // Date of Birth
    private String contactInfo;
    private List<Ticket> bookedTickets; // A Passenger 'books' many Tickets

    public Passenger(String passengerID, String name, Date dob, String contactInfo) {
        this.passengerID = passengerID;
        this.name = name;
        this.dob = dob;
        this.contactInfo = contactInfo;
        this.bookedTickets = new ArrayList<>();
    }

    // Getters
    public String getPassengerID() { return passengerID; }
    public String getName() { return name; }
    public Date getDob() { return dob; }
    public String getContactInfo() { return contactInfo; }
    public List<Ticket> getBookedTickets() { return bookedTickets; }

    // Helper to add tickets
    public void addTicket(Ticket ticket) {
        if (!bookedTickets.contains(ticket)) {
            bookedTickets.add(ticket);
            System.out.println("Ticket " + ticket.getTicketID() + " added to " + this.name + "'s booked tickets.");
        }
    }

    // Operations
    // The diagram shows bookTicket() operation for Passenger
    // This implies that a Passenger can initiate the ticket booking process,
    // which in turn implies they might call a method on Reservation or the System
    public Ticket bookTicket(Reservation reservation, String seatNumber, float price) {
        // This method acts as a convenience for the passenger to book a ticket within an existing reservation.
        // It delegates the actual ticket creation to the Reservation object.
        System.out.println(this.name + " is booking a ticket for reservation " + reservation.getReservationID());
        Ticket newTicket = reservation.createTicket(this, seatNumber, price);
        if (newTicket != null) {
            // The addTicket(newTicket) is already called inside Reservation.createTicket() for this passenger.
            System.out.println(this.name + " successfully booked ticket " + newTicket.getTicketID());
        }
        return newTicket;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passengerID, passenger.passengerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerID);
    }
}

// Ticket.java (Non-public class)
class Ticket {
    private String ticketID;
    private float price;
    private String seatNumber;
    private String status; // e.g., "Issued", "Refunded"
    private Reservation associatedReservation; // Link back to the reservation it belongs to

    public Ticket(String ticketID, float price, String seatNumber, String status) {
        this.ticketID = ticketID;
        this.price = price;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    // Getters
    public String getTicketID() { return ticketID; }
    public float getPrice() { return price; }
    public String getSeatNumber() { return seatNumber; }
    public String getStatus() { return status; }
    public Reservation getAssociatedReservation() { return associatedReservation; }

    // Setter for associated reservation (set when ticket is created)
    public void setAssociatedReservation(Reservation associatedReservation) {
        this.associatedReservation = associatedReservation;
    }

    // Operations
    public void issueTicket() {
        this.status = "Issued";
        System.out.println("Ticket " + ticketID + " has been issued.");
    }

    public void refundTicket() {
        if (!this.status.equals("Refunded")) {
            this.status = "Refunded";
            System.out.println("Ticket " + ticketID + " has been refunded. Amount: $" + String.format("%.2f", price));
            // In a real system, this would trigger payment processing for refund.
        } else {
            System.out.println("Ticket " + ticketID + " is already refunded.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketID, ticket.ticketID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketID);
    }
}
