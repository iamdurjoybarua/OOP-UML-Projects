import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // For generating unique IDs

public class AirlineReservationSystemDemo {

    public static void main(String[] args) {
        System.out.println("--- Airline Reservation System Demonstration ---");

        // 1. Create Airports
        Airport lax = new Airport("LAX", "Los Angeles International Airport", "Los Angeles", "USA");
        Airport jfk = new Airport("JFK", "John F. Kennedy International Airport", "New York", "USA");
        Airport lhr = new Airport("LHR", "London Heathrow Airport", "London", "UK");
        System.out.println("\n");

        // 2. Create Aircraft
        Aircraft boeing747 = new Aircraft("AC001", "Boeing 747", 400);
        Aircraft airbusA320 = new Aircraft("AC002", "Airbus A320", 150);
        System.out.println("\n");

        // Add seats to aircraft (simplified)
        for (int i = 1; i <= 50; i++) {
            boeing747.addSeat(new Seat("A" + i, SeatClass.FIRST));
        }
        for (int i = 51; i <= 150; i++) {
            boeing747.addSeat(new Seat("B" + i, SeatClass.BUSINESS));
        }
        for (int i = 151; i <= 400; i++) {
            boeing747.addSeat(new Seat("C" + i, SeatClass.ECONOMY));
        }
        System.out.println("Boeing 747 has " + boeing747.getSeats().size() + " seats.");
        System.out.println("\n");

        // 3. Create Flights
        Flight flightLAXJFK = new Flight("FL001", lax, jfk, LocalDateTime.of(2025, 6, 1, 10, 0), LocalDateTime.of(2025, 6, 1, 18, 0), boeing747, 500.0);
        flightLAXJFK.setStatus(FlightStatus.SCHEDULED);
        Flight flightJFKLAX = new Flight("FL002", jfk, lax, LocalDateTime.of(2025, 6, 2, 9, 0), LocalDateTime.of(2025, 6, 2, 17, 0), airbusA320, 450.0);
        flightJFKLAX.setStatus(FlightStatus.SCHEDULED);
        System.out.println("\n");

        // 4. Create Passengers
        Passenger p1 = new Passenger("P001", "Alice", "Smith", LocalDate.of(1990, 5, 15), "AB1234567", "alice@example.com", "123-456-7890");
        Passenger p2 = new Passenger("P002", "Bob", "Johnson", LocalDate.of(1985, 10, 20), "CD9876543", "bob@example.com", "098-765-4321");
        System.out.println("\n");

        // 5. Create Reservations
        System.out.println("--- Creating Reservations ---");
        Reservation res1 = new Reservation("RES001", LocalDate.now());
        res1.addPassenger(p1);
        res1.addFlight(flightLAXJFK);
        res1.confirmReservation(); // This sets status to CONFIRMED and calculates price
        System.out.println("Reservation " + res1.getReservationId() + " created for " + p1.getFirstName() + " on " + flightLAXJFK.getFlightNumber());

        // 6. Create Tickets and link to Reservation
        Ticket t1 = new Ticket(UUID.randomUUID().toString(), LocalDate.now(), "C155", SeatClass.ECONOMY, flightLAXJFK.calculateFare(SeatClass.ECONOMY));
        res1.addTicket(t1);
        Seat bookedSeat = flightLAXJFK.getAircraft().getSeat("C155");
        if (bookedSeat != null) {
            bookedSeat.bookSeat();
            t1.setSeatNumber(bookedSeat.getSeatNumber()); // Ensure ticket has correct booked seat
            System.out.println("Ticket " + t1.getTicketId() + " issued for seat " + t1.getSeatNumber() + " on " + flightLAXJFK.getFlightNumber());
        } else {
             System.out.println("Could not find seat C155 for flight FL001.");
        }
        System.out.println("\n");


        // 7. Process Payment
        System.out.println("--- Processing Payment ---");
        // Ensure totalPrice is correctly set before payment, it's summed up from flights for simplicity.
        // In a real system, totalPrice would be derived from the sum of ticket prices.
        res1.setTotalPrice(res1.getFlights().stream().mapToDouble(Flight::getBasePrice).sum()); // Ensure total price is correct before payment
        Payment payment1 = new Payment(UUID.randomUUID().toString(), res1.getTotalPrice(), PaymentMethod.CREDIT_CARD);
        res1.setPayment(payment1);
        payment1.processPayment();
        System.out.println("Payment for reservation " + res1.getReservationId() + " processed. Status: " + payment1.getStatus());
        System.out.println("\n");

        // 8. Employee (Admin) actions
        Employee admin1 = new Employee("E001", "Admin John", "Manager", 70000.0);
        System.out.println("--- Employee Actions ---");
        admin1.manageFlights(flightLAXJFK, FlightStatus.DELAYED);
        admin1.handleReservations(res1, ReservationStatus.COMPLETED); // This call now works!
        System.out.println("\n");

        // Display reservation details
        System.out.println("--- Reservation Details ---");
        System.out.println(res1.getReservationId() + " Status: " + res1.getStatus() + ", Total Price: " + res1.getTotalPrice());
        System.out.println("Passengers:");
        res1.getPassengers().forEach(p -> System.out.println(" - " + p.getFirstName() + " " + p.getLastName()));
        System.out.println("Flights:");
        res1.getFlights().forEach(f -> System.out.println(" - " + f.getFlightNumber() + " (" + f.getStatus() + ")"));
        System.out.println("Tickets:");
        res1.getTickets().forEach(t -> System.out.println(" - " + t.getTicketId() + " (Seat: " + t.getSeatNumber() + ")"));
        System.out.println("Payment: " + res1.getPayment().getStatus() + " - " + res1.getPayment().getAmount());

        System.out.println("\n--- Demonstration Complete ---");
    }
}

// Enum: FlightStatus
enum FlightStatus {
    SCHEDULED,
    DEPARTED,
    ARRIVED,
    DELAYED,
    CANCELLED
}

// Enum: SeatClass
enum SeatClass {
    ECONOMY,
    BUSINESS,
    FIRST
}

// Enum: ReservationStatus
enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

// Enum: PaymentMethod
enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER
}

// Enum: PaymentStatus
enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}

// Class: Airport
class Airport {
    private String airportCode;
    private String name;
    private String city;
    private String country;

    public Airport(String airportCode, String name, String city, String country) {
        this.airportCode = airportCode;
        this.name = name;
        this.city = city;
        this.country = country;
        System.out.println("Airport " + airportCode + " (" + city + ") created.");
    }

    public String getAirportCode() { return airportCode; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCountry() { return country; }

    public String getAirportInfo() {
        return name + " (" + airportCode + "), " + city + ", " + country;
    }
}

// Class: Aircraft
class Aircraft {
    private String aircraftId;
    private String model;
    private int capacity;
    private List<Seat> seats; // Aggregation with Seat

    public Aircraft(String aircraftId, String model, int capacity) {
        this.aircraftId = aircraftId;
        this.model = model;
        this.capacity = capacity;
        this.seats = new ArrayList<>();
        System.out.println("Aircraft " + model + " (ID: " + aircraftId + ", Capacity: " + capacity + ") created.");
    }

    public String getAircraftId() { return aircraftId; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }
    public List<Seat> getSeats() { return new ArrayList<>(seats); } // Return a copy

    public void addSeat(Seat seat) {
        if (seats.size() < capacity) {
            this.seats.add(seat);
        } else {
            System.out.println("Cannot add more seats, aircraft is at full capacity.");
        }
    }

    public Seat getSeat(String seatNumber) {
        return seats.stream().filter(s -> s.getSeatNumber().equals(seatNumber)).findFirst().orElse(null);
    }

    public String getAircraftInfo() {
        return model + " (ID: " + aircraftId + ", Seats: " + seats.size() + "/" + capacity + ")";
    }
}

// Class: Seat
class Seat {
    private String seatNumber;
    private SeatClass seatClass;
    private boolean isBooked;

    public Seat(String seatNumber, SeatClass seatClass) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.isBooked = false;
    }

    public String getSeatNumber() { return seatNumber; }
    public SeatClass getSeatClass() { return seatClass; }
    public boolean isBooked() { return isBooked; }

    public void bookSeat() {
        if (!isBooked) {
            this.isBooked = true;
            System.out.println("Seat " + seatNumber + " booked.");
        } else {
            System.out.println("Seat " + seatNumber + " is already booked.");
        }
    }

    public void unbookSeat() {
        if (isBooked) {
            this.isBooked = false;
            System.out.println("Seat " + seatNumber + " unbooked.");
        } else {
            System.out.println("Seat " + seatNumber + " is not booked.");
        }
    }
}

// Class: Flight
class Flight {
    private String flightNumber;
    private Airport origin;
    private Airport destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Duration duration;
    private FlightStatus status;
    private Aircraft aircraft; // Association with Aircraft
    private double basePrice;

    public Flight(String flightNumber, Airport origin, Airport destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, Aircraft aircraft, double basePrice) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = Duration.between(departureTime, arrivalTime);
        this.aircraft = aircraft;
        this.basePrice = basePrice;
        this.status = FlightStatus.SCHEDULED; // Default status
        System.out.println("Flight " + flightNumber + " (" + origin.getAirportCode() + " to " + destination.getAirportCode() + ") created.");
    }

    public String getFlightNumber() { return flightNumber; }
    public Airport getOrigin() { return origin; }
    public Airport getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public Duration getDuration() { return duration; }
    public FlightStatus getStatus() { return status; }
    public Aircraft getAircraft() { return aircraft; }
    public double getBasePrice() { return basePrice; }

    public void setStatus(FlightStatus status) {
        this.status = status;
        System.out.println("Flight " + flightNumber + " status updated to " + status);
    }

    public double calculateFare(SeatClass seatClass) {
        double fare = basePrice;
        switch (seatClass) {
            case BUSINESS:
                fare *= 1.5;
                break;
            case FIRST:
                fare *= 2.0;
                break;
            case ECONOMY:
            default:
                // No change for economy
                break;
        }
        return fare;
    }

    public String getFlightDetails() {
        return String.format("Flight %s: %s (%s) -> %s (%s) | Departs: %s | Arrives: %s | Status: %s",
                             flightNumber, origin.getAirportCode(), origin.getCity(), destination.getAirportCode(), destination.getCity(),
                             departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                             arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), status);
    }
}

// Class: Passenger
class Passenger {
    private String passengerId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String passportNumber;
    private String contactEmail;
    private String contactPhone;

    public Passenger(String passengerId, String firstName, String lastName, LocalDate dateOfBirth,
                     String passportNumber, String contactEmail, String contactPhone) {
        this.passengerId = passengerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.passportNumber = passportNumber;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        System.out.println("Passenger " + firstName + " " + lastName + " (ID: " + passengerId + ") created.");
    }

    public String getPassengerId() { return passengerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getPassportNumber() { return passportNumber; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }

    public String getPassengerDetails() {
        return firstName + " " + lastName + " (Passport: " + passportNumber + ")";
    }
}

// Class: Reservation
class Reservation {
    private String reservationId;
    private LocalDate bookingDate;
    private ReservationStatus status;
    private double totalPrice;
    private List<Passenger> passengers; // Association with Passenger
    private List<Flight> flights;       // Association with Flight
    private List<Ticket> tickets;       // Composition with Ticket
    private Payment payment;            // Association with Payment

    public Reservation(String reservationId, LocalDate bookingDate) {
        this.reservationId = reservationId;
        this.bookingDate = bookingDate;
        this.status = ReservationStatus.PENDING; // Default status
        this.totalPrice = 0.0;
        this.passengers = new ArrayList<>();
        this.flights = new ArrayList<>();
        this.tickets = new ArrayList<>();
        System.out.println("Reservation " + reservationId + " created (Status: " + status + ").");
    }

    public String getReservationId() { return reservationId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public ReservationStatus getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public List<Passenger> getPassengers() { return new ArrayList<>(passengers); }
    public List<Flight> getFlights() { return new ArrayList<>(flights); }
    public List<Ticket> getTickets() { return new ArrayList<>(tickets); }
    public Payment getPayment() { return payment; }

    // ****** FIX: Added setStatus method here ******
    public void setStatus(ReservationStatus status) {
        this.status = status;
        System.out.println("Reservation " + reservationId + " status updated to " + status);
    }

    // Also added a setter for totalPrice as it's modified outside the constructor
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    // ********************************************

    public void addPassenger(Passenger passenger) {
        if (!passengers.contains(passenger)) {
            passengers.add(passenger);
        }
    }

    public void addFlight(Flight flight) {
        if (!flights.contains(flight)) {
            flights.add(flight);
            // This assumes basePrice of flight is added to reservation total price
            // In a real system, the fare from the ticket would be used.
            // this.totalPrice += flight.getBasePrice();
        }
    }

    public void addTicket(Ticket ticket) {
        if (!tickets.contains(ticket)) {
            tickets.add(ticket);
            // totalPrice is usually calculated from flights and seat class, ticket has final price
            // this.totalPrice += ticket.getPrice(); // If ticket price is separate
        }
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void confirmReservation() {
        this.status = ReservationStatus.CONFIRMED;
        System.out.println("Reservation " + reservationId + " confirmed.");
    }

    public void cancelReservation() {
        this.status = ReservationStatus.CANCELLED;
        System.out.println("Reservation " + reservationId + " cancelled.");
        // Logic for unbooking seats, refunding payment etc.
    }
}

// Class: Ticket
class Ticket {
    private String ticketId;
    private LocalDate issueDate;
    private String seatNumber; // Simplified, ideally links to Seat object
    private SeatClass seatClass; // Store seat class for clarity
    private double price;

    public Ticket(String ticketId, LocalDate issueDate, String seatNumber, SeatClass seatClass, double price) {
        this.ticketId = ticketId;
        this.issueDate = issueDate;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.price = price;
        System.out.println("Ticket " + ticketId + " issued for seat " + seatNumber + " (" + seatClass + ") at price " + price);
    }

    public String getTicketId() { return ticketId; }
    public LocalDate getIssueDate() { return issueDate; }
    public String getSeatNumber() { return seatNumber; }
    public SeatClass getSeatClass() { return seatClass; }
    public double getPrice() { return price; }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTicketDetails() {
        return String.format("Ticket ID: %s, Seat: %s (%s), Price: %.2f", ticketId, seatNumber, seatClass, price);
    }
}

// Class: Payment
class Payment {
    private String paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment(String paymentId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
        this.method = method;
        this.status = PaymentStatus.PENDING; // Default status
        System.out.println("Payment " + paymentId + " initiated for " + amount + " via " + method + ".");
    }

    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }

    public void processPayment() {
        // Simulate payment processing
        this.status = PaymentStatus.COMPLETED;
        System.out.println("Payment " + paymentId + " processed successfully. Status: " + status);
    }

    public void refundPayment() {
        if (this.status == PaymentStatus.COMPLETED) {
            this.status = PaymentStatus.REFUNDED;
            System.out.println("Payment " + paymentId + " refunded.");
        } else {
            System.out.println("Cannot refund payment " + paymentId + ". Current status: " + status);
        }
    }
}

// Class: Employee
class Employee {
    private String employeeId;
    private String name;
    private String position;
    private double salary;

    public Employee(String employeeId, String name, String position, double salary) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.salary = salary;
        System.out.println("Employee " + name + " (ID: " + employeeId + ", Position: " + position + ") created.");
    }

    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }

    // Example methods for employee actions
    public void manageFlights(Flight flight, FlightStatus newStatus) {
        System.out.println(name + " (" + position + ") managing Flight " + flight.getFlightNumber());
        flight.setStatus(newStatus);
    }

    public void handleReservations(Reservation reservation, ReservationStatus newStatus) {
        System.out.println(name + " (" + position + ") handling Reservation " + reservation.getReservationId());
        reservation.setStatus(newStatus); // This line should now compile correctly
    }
}