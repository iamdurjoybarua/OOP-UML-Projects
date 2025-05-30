import java.math.BigDecimal; // For bookingPrice
import java.time.LocalDate;   // For startDate, createdOn
import java.time.LocalDateTime; // For endTime
import java.util.ArrayList;   // Added for List implementations
import java.util.List;        // Added for List interface

// --- Enums as per Class Diagram ---
// Enums are used for fixed sets of states or types.

enum AccountStatus {
    ACTIVE, BLOCKED, CLOSED, PENDING
}

enum AccountType {
    ADMIN, RECEPTIONIST, GUEST
}

enum RoomStyle {
    STANDARD, DELUXE, SUITE, EXECUTIVE
}

enum RoomStatus {
    AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED
}

enum BookingStatus {
    CONFIRMED, PENDING, CANCELLED, CHECKED_IN, CHECKED_OUT
}

// =============================================================================
// 1. Hotel Class
// Represents the main Hotel entity.
// =============================================================================
class Hotel {
    // Attributes from diagram:
    private String name;
    private String location;

    // Composition relationship: Hotel has Rooms.
    // When the Hotel is gone, its Rooms are typically also gone.
    private List<Room> rooms;

    // Association: Hotel has RoomBookings.
    private List<RoomBooking> bookings;

    /**
     * Constructor for the Hotel class.
     * @param name The name of the hotel.
     * @param location The physical location of the hotel.
     */
    public Hotel(String name, String location) {
        this.name = name;
        this.location = location;
        this.rooms = new ArrayList<>();
        this.bookings = new ArrayList<>();
        System.out.println("Hotel '" + name + "' at " + location + " created.");
    }

    // Getters for attributes
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Method from diagram: getRooms()
     * Returns a list of all rooms in the hotel.
     * @return A List of Room objects.
     */
    public List<Room> getRooms() {
        return new ArrayList<>(rooms); // Return a copy to prevent external modification
    }

    /**
     * Adds a room to the hotel.
     * @param room The Room object to add.
     */
    public void addRoom(Room room) {
        this.rooms.add(room);
        System.out.println("Room " + room.getRoomNumber() + " added to " + this.name);
    }

    /**
     * Adds a booking to the hotel's list of bookings.
     * @param booking The RoomBooking object to add.
     */
    public void addBooking(RoomBooking booking) {
        this.bookings.add(booking);
        System.out.println("Booking " + booking.getReservationNumber() + " added to " + this.name);
    }

    // You might also want methods like checkIn(), checkOut(), etc. at the Hotel level
}

// =============================================================================
// 2. Person Class (Abstract)
// Represents a generic person within the system. It's a superclass for Receptionist and Guest.
// =============================================================================
abstract class Person { // Marked as abstract as a generic "Person" isn't typically instantiated directly
    // Attributes from diagram:
    private String name;
    private String address;
    private String email;
    private String phone;
    // Association: Person has an Account. (Aggregation in diagram, empty diamond on Person)
    private Account account; // A Person can exist without an Account, but an Account must belong to a Person.

    // Attribute from diagram: accountType
    public AccountType accountType; // Using public as shown in diagram, though private with getter is more common OOP

    /**
     * Constructor for the Person class.
     * @param name Person's full name.
     * @param address Person's address.
     * @param email Person's email address.
     * @param phone Person's phone number.
     * @param accountType The type of account (e.g., GUEST, RECEPTIONIST).
     */
    public Person(String name, String address, String email, String phone, AccountType accountType) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
        System.out.println("Person '" + name + "' created.");
    }

    // Getters for attributes (not shown in diagram, but good practice for private fields)
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Account getAccount() { return account; }

    /**
     * Sets the Account for this Person.
     * @param account The Account object to associate.
     */
    public void setAccount(Account account) {
        this.account = account;
        System.out.println(this.name + " linked to Account ID: " + account.getId());
    }

    // Abstract methods if any common behavior varies among subclasses
    // For now, no abstract methods as none are explicitly shown.
}

// =============================================================================
// 3. Receptionist Class
// Subclass of Person, representing hotel staff who manage bookings.
// =============================================================================
class Receptionist extends Person {
    /**
     * Constructor for the Receptionist class.
     * @param name Receptionist's full name.
     * @param address Receptionist's address.
     * @param email Receptionist's email address.
     * @param phone Receptionist's phone number.
     */
    public Receptionist(String name, String address, String email, String phone) {
        // Call the constructor of the parent class (Person)
        super(name, address, email, phone, AccountType.RECEPTIONIST);
        System.out.println("Receptionist '" + name + "' initialized.");
    }

    /**
     * Method from diagram: creatBooking() (Corrected to createBooking)
     * Allows the receptionist to create a booking.
     * @param hotel The Hotel object where the booking is made.
     * @param guest The Guest for whom the booking is made.
     * @param rooms A list of rooms to be booked.
     * @param startDate The start date of the booking.
     * @param durationDays The duration of the booking in days.
     * @return The created RoomBooking object.
     */
    public RoomBooking createBooking(Hotel hotel, Guest guest, List<Room> rooms, LocalDate startDate, int durationDays) {
        // Basic validation: Check if rooms are available and belongs to the hotel
        for (Room room : rooms) {
            if (!room.isRoomAvailable() || !hotel.getRooms().contains(room)) {
                System.out.println("Error: Room " + room.getRoomNumber() + " is not available or not part of this hotel.");
                return null;
            }
        }

        // Generate a unique reservation number (simplified)
        String reservationNum = "RES-" + System.currentTimeMillis();
        LocalDateTime endTime = startDate.plusDays(durationDays).atStartOfDay(); // Assuming check-out at start of day

        RoomBooking booking = new RoomBooking(reservationNum, startDate, durationDays, BookingStatus.PENDING, endTime);
        booking.setGuest(guest); // Link booking to guest
        booking.setRooms(rooms); // Link rooms to booking

        // Update room statuses and add to hotel's bookings
        for (Room room : rooms) {
            room.setStatus(RoomStatus.RESERVED); // Mark rooms as reserved
        }
        hotel.addBooking(booking);

        System.out.println(this.getName() + " created booking " + reservationNum + " for " + guest.getName());
        return booking;
    }
}

// =============================================================================
// 4. Guest Class
// Subclass of Person, representing a hotel guest.
// =============================================================================
class Guest extends Person {
    // Attribute from diagram:
    public int totalRoomsBooked;

    /**
     * Constructor for the Guest class.
     * @param name Guest's full name.
     * @param address Guest's address.
     * @param email Guest's email address.
     * @param phone Guest's phone number.
     */
    public Guest(String name, String address, String email, String phone) {
        // Call the constructor of the parent class (Person)
        super(name, address, email, phone, AccountType.GUEST);
        this.totalRoomsBooked = 0;
        System.out.println("Guest '" + name + "' initialized.");
    }

    /**
     * Method from diagram: creatBooking() (Corrected to createBooking)
     * Allows the guest to create a booking directly.
     * @param hotel The Hotel object where the booking is made.
     * @param rooms A list of rooms to be booked.
     * @param startDate The start date of the booking.
     * @param durationDays The duration of the booking in days.
     * @return The created RoomBooking object.
     */
    public RoomBooking createBooking(Hotel hotel, List<Room> rooms, LocalDate startDate, int durationDays) {
        // Similar logic to Receptionist's createBooking, but specific to Guest context.
        // It's often better to have a central booking service that both can use.
        // For demonstration, we'll replicate some logic.

        for (Room room : rooms) {
            if (!room.isRoomAvailable() || !hotel.getRooms().contains(room)) {
                System.out.println("Error: Room " + room.getRoomNumber() + " is not available or not part of this hotel.");
                return null;
            }
        }

        String reservationNum = "GUEST-RES-" + System.currentTimeMillis();
        LocalDateTime endTime = startDate.plusDays(durationDays).atStartOfDay();

        RoomBooking booking = new RoomBooking(reservationNum, startDate, durationDays, BookingStatus.PENDING, endTime);
        booking.setGuest(this); // Link booking to THIS guest
        booking.setRooms(rooms);

        for (Room room : rooms) {
            room.setStatus(RoomStatus.RESERVED);
        }
        hotel.addBooking(booking);
        this.totalRoomsBooked += rooms.size(); // Update guest's booked count

        System.out.println(this.getName() + " created booking " + reservationNum + " for " + rooms.size() + " rooms.");
        return booking;
    }
}

// =============================================================================
// 5. Room Class
// Represents an individual room in the hotel.
// =============================================================================
class Room {
    // Attributes from diagram:
    private String roomNumber;
    private RoomStyle style;
    private RoomStatus status;
    private BigDecimal bookingPrice; // Using BigDecimal for currency

    /**
     * Constructor for the Room class.
     * @param roomNumber Unique identifier for the room.
     * @param style The style of the room (e.g., STANDARD, DELUXE).
     * @param bookingPrice The price of booking the room per night.
     */
    public Room(String roomNumber, RoomStyle style, BigDecimal bookingPrice) {
        this.roomNumber = roomNumber;
        this.style = style;
        this.status = RoomStatus.AVAILABLE; // Rooms are available by default
        this.bookingPrice = bookingPrice;
        System.out.println("Room " + roomNumber + " (" + style + ") created with price $" + bookingPrice + ".");
    }

    // Getters and Setters for attributes
    public String getRoomNumber() { return roomNumber; }
    public RoomStyle getStyle() { return style; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) {
        this.status = status;
        System.out.println("Room " + roomNumber + " status updated to " + status);
    }
    public BigDecimal getBookingPrice() { return bookingPrice; }

    /**
     * Method from diagram: isRoomAvailable()
     * Checks if the room is currently available for booking.
     * @return true if the room status is AVAILABLE, false otherwise.
     */
    public boolean isRoomAvailable() {
        return this.status == RoomStatus.AVAILABLE;
    }
}

// =============================================================================
// 6. RoomBooking Class
// Represents a specific booking made for one or more rooms.
// =============================================================================
class RoomBooking {
    // Attributes from diagram:
    private String reservationNumber; // 'rservationNumber' in diagram (corrected)
    private LocalDate startDate;
    private int durationDays;
    private BookingStatus status;
    private LocalDateTime endTime; // 'datetime' in diagram

    // Associations:
    private Guest guest; // A booking is made by one Guest
    private List<Room> rooms; // A booking can be for one or more Rooms (dependency in diagram, usually association)
    private List<Notification> notifications; // A booking can trigger notifications

    /**
     * Constructor for the RoomBooking class.
     * @param reservationNumber Unique identifier for the booking.
     * @param startDate The start date of the booking.
     * @param durationDays The number of days the booking is for.
     * @param status The current status of the booking.
     * @param endTime The end date/time of the booking.
     */
    public RoomBooking(String reservationNumber, LocalDate startDate, int durationDays, BookingStatus status, LocalDateTime endTime) {
        this.reservationNumber = reservationNumber;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.status = status;
        this.endTime = endTime;
        this.rooms = new ArrayList<>();
        this.notifications = new ArrayList<>();
        System.out.println("RoomBooking " + reservationNumber + " created for " + startDate + " for " + durationDays + " days.");
    }

    // Getters and Setters
    public String getReservationNumber() { return reservationNumber; }
    public LocalDate getStartDate() { return startDate; }
    public int getDurationDays() { return durationDays; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) {
        this.status = status;
        System.out.println("Booking " + reservationNumber + " status updated to " + status);
    }
    public LocalDateTime getEndTime() { return endTime; }
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }
    public List<Room> getRooms() { return new ArrayList<>(rooms); }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    /**
     * Method from diagram: fetchDetail()
     * Fetches and displays details of the booking.
     */
    public void fetchDetail() {
        System.out.println("\n--- Booking Details for " + reservationNumber + " ---");
        System.out.println("Guest: " + (guest != null ? guest.getName() : "N/A"));
        System.out.println("Start Date: " + startDate);
        System.out.println("Duration: " + durationDays + " days");
        System.out.println("End Time: " + endTime);
        System.out.println("Status: " + status);
        System.out.print("Rooms Booked: ");
        if (rooms.isEmpty()) {
            System.out.println("None");
        } else {
            for (Room room : rooms) {
                System.out.print(room.getRoomNumber() + " (" + room.getStyle() + "), ");
            }
            System.out.println();
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Adds a notification related to this booking.
     * @param notification The Notification object to add.
     */
    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        System.out.println("Notification " + notification.getNotificationId() + " added for booking " + reservationNumber);
    }
}

// =============================================================================
// 7. Notification Class
// Represents a system notification, potentially sent to a guest or staff.
// =============================================================================
class Notification {
    // Attributes from diagram:
    private int notificationId;
    private LocalDate createdOn; // 'date' in diagram
    private String content;

    /**
     * Constructor for the Notification class.
     * @param notificationId Unique ID for the notification.
     * @param content The message content of the notification.
     */
    public Notification(int notificationId, String content) {
        this.notificationId = notificationId;
        this.createdOn = LocalDate.now(); // Set creation date to current date
        this.content = content;
        System.out.println("Notification " + notificationId + " created.");
    }

    // Getters for attributes
    public int getNotificationId() { return notificationId; }
    public LocalDate getCreatedOn() { return createdOn; }
    public String getContent() { return content; }

    /**
     * Method from diagram: send()
     * Simulates sending the notification (e.g., email, SMS, in-app).
     */
    public void send() {
        System.out.println("--- Sending Notification " + notificationId + " ---");
        System.out.println("Created On: " + createdOn);
        System.out.println("Content: " + content);
        System.out.println("-------------------------------------");
        // In a real system, this would integrate with actual messaging services.
    }
}

// =============================================================================
// 8. Account Class
// Represents a user account in the system, linked to a Person.
// =============================================================================
class Account {
    // Attributes from diagram:
    private int id;
    private String password;
    private AccountStatus status;

    /**
     * Constructor for the Account class.
     * @param id Unique identifier for the account.
     * @param password The account password (in a real system, this would be hashed).
     * @param status The initial status of the account.
     */
    public Account(int id, String password, AccountStatus status) {
        this.id = id;
        this.password = password; // In production, store password hashes, not plain text!
        this.status = status;
        System.out.println("Account ID " + id + " created with status: " + status);
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getPassword() { return password; } // Be cautious with returning plain password
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) {
        this.status = status;
        System.out.println("Account ID " + id + " status updated to " + status);
    }

    /**
     * Method from diagram: resetPassword()
     * Resets the account password.
     * @param newPassword The new password.
     * @return true if password was reset, false otherwise.
     */
    public boolean resetPassword(String newPassword) {
        // Add password complexity rules here
        if (newPassword != null && newPassword.length() >= 6) {
            this.password = newPassword; // Store hashed password in a real app
            System.out.println("Account ID " + id + " password reset successfully.");
            return true;
        }
        System.out.println("Password reset failed: New password too short or invalid.");
        return false;
    }

    /**
     * Basic authentication method.
     * @param enteredPassword The password entered by the user.
     * @return true if password matches, false otherwise.
     */
    public boolean authenticate(String enteredPassword) {
        return this.password.equals(enteredPassword) && this.status == AccountStatus.ACTIVE;
    }
}

// =============================================================================
// Main Class for Demonstration
// This class will set up objects and simulate interactions based on the diagram.
// =============================================================================
public class HotelManagementSystemDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting Hotel Management System Demonstration ---");

        // 1. Create Hotel
        Hotel grandHotel = new Hotel("Grand Legacy Hotel", "123 Grand Ave, Metropolis");

        // 2. Add Rooms to Hotel
        Room room101 = new Room("101", RoomStyle.STANDARD, new BigDecimal("100.00"));
        Room room102 = new Room("102", RoomStyle.DELUXE, new BigDecimal("150.00"));
        Room room201 = new Room("201", RoomStyle.SUITE, new BigDecimal("300.00"));
        grandHotel.addRoom(room101);
        grandHotel.addRoom(room102);
        grandHotel.addRoom(room201);

        // 3. Create Persons (Receptionist and Guest)
        Receptionist mary = new Receptionist("Mary Poppins", "12 Spoonful St", "mary@hotel.com", "555-1111");
        Guest alice = new Guest("Alice Wonderland", "Rabbit Hole Rd", "alice@example.com", "555-2222");
        Guest bob = new Guest("Bob The Builder", "Construction Site", "bob@example.com", "555-3333");

        // 4. Create Accounts and link them to Persons
        Account maryAccount = new Account(1, "marypass", AccountStatus.ACTIVE);
        mary.setAccount(maryAccount);

        Account aliceAccount = new Account(2, "alicepass", AccountStatus.ACTIVE);
        alice.setAccount(aliceAccount);

        Account bobAccount = new Account(3, "bobpass", AccountStatus.ACTIVE);
        bob.setAccount(bobAccount);

        // Test Account authentication
        System.out.println("\n--- Account Authentication Test ---");
        System.out.println("Mary authenticates: " + maryAccount.authenticate("marypass"));
        System.out.println("Alice authenticates with wrong pass: " + aliceAccount.authenticate("wrong"));

        // 5. Simulate Booking by Receptionist
        System.out.println("\n--- Booking Simulation by Receptionist ---");
        List<Room> roomsForAlice = new ArrayList<>();
        roomsForAlice.add(room101);
        RoomBooking aliceBooking = mary.createBooking(grandHotel, alice, roomsForAlice, LocalDate.of(2025, 7, 10), 3);
        if (aliceBooking != null) {
            aliceBooking.fetchDetail();
            // Create a notification for Alice's booking
            Notification bookingConfirmation = new Notification(101, "Your booking " + aliceBooking.getReservationNumber() + " is confirmed!");
            aliceBooking.addNotification(bookingConfirmation);
            bookingConfirmation.send();
            aliceBooking.setStatus(BookingStatus.CONFIRMED); // Update booking status
        }

        // Check room status after booking
        System.out.println("Room 101 status: " + room101.getStatus());

        // Simulate Booking by Guest (using room201)
        System.out.println("\n--- Booking Simulation by Guest ---");
        List<Room> roomsForBob = new ArrayList<>();
        roomsForBob.add(room201);
        RoomBooking bobBooking = bob.createBooking(grandHotel, roomsForBob, LocalDate.of(2025, 8, 1), 5);
        if (bobBooking != null) {
            bobBooking.fetchDetail();
            Notification bobConfirmation = new Notification(102, "Your booking " + bobBooking.getReservationNumber() + " is confirmed!");
            bobBooking.addNotification(bobConfirmation);
            bobConfirmation.send();
            bobBooking.setStatus(BookingStatus.CONFIRMED);
        }
        System.out.println("Room 201 status: " + room201.getStatus());
        System.out.println("Bob's total rooms booked: " + bob.totalRoomsBooked);

        // Try to book an occupied room
        System.out.println("\n>>> Trying to book already reserved Room 101 <<<");
        List<Room> roomsForFailedBooking = new ArrayList<>();
        roomsForFailedBooking.add(room101);
        mary.createBooking(grandHotel, bob, roomsForFailedBooking, LocalDate.of(2025, 9, 1), 2);
        System.out.println("Room 101 status (should still be RESERVED): " + room101.getStatus());

        // Simulate check-in and check-out (not explicitly in diagram, but common flow)
        System.out.println("\n--- Check-in/Check-out Simulation ---");
        if (aliceBooking != null && aliceBooking.getStatus() == BookingStatus.CONFIRMED) {
            aliceBooking.setStatus(BookingStatus.CHECKED_IN);
            // In a real system, room status would be OCCUPIED now.
            // For this simplified example, we manually update the room status after check-in.
            for (Room room : aliceBooking.getRooms()) {
                room.setStatus(RoomStatus.OCCUPIED);
            }
            System.out.println("Room 101 status (after check-in): " + room101.getStatus());
        }

        // Simulate check-out
        if (aliceBooking != null && aliceBooking.getStatus() == BookingStatus.CHECKED_IN) {
            aliceBooking.setStatus(BookingStatus.CHECKED_OUT);
            // After check-out, rooms become available or go into maintenance
            for (Room room : aliceBooking.getRooms()) {
                room.setStatus(RoomStatus.MAINTENANCE); // Or AVAILABLE, depends on policy
            }
            System.out.println("Room 101 status (after check-out): " + room101.getStatus());
        }

        System.out.println("\n--- Hotel Management System Demonstration Complete ---");
    }
}