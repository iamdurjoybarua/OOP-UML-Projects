import java.time.LocalDate; // For dates like Issuedate, returndate
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // For Object.hash and Objects.equals for identity
import java.util.concurrent.atomic.AtomicInteger; // For generating unique IDs

// =============================================================================
// Enums (Implicitly defined by types in diagram)
// =============================================================================

// No explicit enums in this diagram, but might be useful for BookStatus, MemberStatus etc.
// Sticking strictly to the diagram, I won't create any unless directly implied.

// =============================================================================
// 1. Catalog Class
// Represents the central catalog for all books in the library.
// =============================================================================
class Catalog {
    // Attributes from diagram:
    private String authorName; // 'Authorname'
    private int numberOfCopies; // 'noofcopies'

    // Composition: Catalog contains Books.
    // When the Catalog object is removed, the associated Books are also logically removed from this catalog.
    private List<Books> booksInCatalog; // Using 'Books' as the type from the diagram

    /**
     * Constructor for the Catalog class.
     * @param authorName The primary author name this catalog section focuses on (or a general catalog).
     * @param numberOfCopies The total number of copies managed in this catalog section.
     */
    public Catalog(String authorName, int numberOfCopies) {
        this.authorName = authorName;
        this.numberOfCopies = numberOfCopies;
        this.booksInCatalog = new ArrayList<>();
        System.out.println("Catalog created for author: " + authorName + " with " + numberOfCopies + " copies.");
    }

    // Getters
    public String getAuthorName() { return authorName; }
    public int getNumberOfCopies() { return numberOfCopies; }
    public List<Books> getBooksInCatalog() { return new ArrayList<>(booksInCatalog); } // Return copy

    /**
     * Method from diagram: UpdateInfo()
     * Updates information about the catalog.
     * @param newAuthorName New author name.
     * @param newNumberOfCopies New number of copies.
     */
    public void updateInfo(String newAuthorName, int newNumberOfCopies) {
        this.authorName = newAuthorName;
        this.numberOfCopies = newNumberOfCopies;
        System.out.println("Catalog info updated to Author: " + newAuthorName + ", Copies: " + newNumberOfCopies);
    }

    /**
     * Method from diagram: Searching()
     * Simulates searching within the catalog.
     * @param query The search query (e.g., book title, author).
     * @return A list of matching books.
     */
    public List<Books> searching(String query) {
        List<Books> results = new ArrayList<>();
        System.out.println("Searching catalog for: " + query);
        for (Books book : booksInCatalog) {
            // Simplified search logic
            if (book.getAuthorName().toLowerCase().contains(query.toLowerCase()) ||
                (book instanceof ReferenceBook && ((ReferenceBook) book).getTitle().toLowerCase().contains(query.toLowerCase())) ||
                (book instanceof GeneralBook && ((GeneralBook) book).getTitle().toLowerCase().contains(query.toLowerCase()))) {
                results.add(book);
            }
        }
        System.out.println("Found " + results.size() + " results.");
        return results;
    }

    /**
     * Adds a book to this catalog. This corresponds to the composition relationship.
     * @param book The book to add.
     */
    public void addBookToCatalog(Books book) {
        if (!this.booksInCatalog.contains(book)) {
            this.booksInCatalog.add(book);
            this.numberOfCopies = calculateTotalCopies(); // Update total copies in catalog
            System.out.println("Book '" + book.getAuthorName() + "' added to catalog.");
        }
    }

    /**
     * Removes a book from this catalog.
     * @param book The book to remove.
     */
    public void removeBookFromCatalog(Books book) {
        if (this.booksInCatalog.remove(book)) {
            this.numberOfCopies = calculateTotalCopies(); // Update total copies
            System.out.println("Book '" + book.getAuthorName() + "' removed from catalog.");
        }
    }

    private int calculateTotalCopies() {
        return booksInCatalog.stream().mapToInt(Books::getNumberOfBooks).sum();
    }
}

// =============================================================================
// 2. Books Class (Abstract Base Class for specific book types)
// Represents a generic book in the library.
// =============================================================================
abstract class Books { // Made abstract as "Books" is generalized into specific types
    // Attributes from diagram:
    // "Authorname: String" appears twice in diagram, consolidating to one.
    private String authorName;
    private String title; // Added for practical book identification, implied by 'Bookname' in Alert
    private int numberOfBooks; // 'Noofbooks' - represents available copies for this specific book item

    /**
     * Constructor for the Books class.
     * @param authorName The author of the book.
     * @param title The title of the book.
     * @param numberOfBooks The number of physical copies of this particular book.
     */
    public Books(String authorName, String title, int numberOfBooks) {
        this.authorName = authorName;
        this.title = title;
        this.numberOfBooks = numberOfBooks;
        System.out.println("Book '" + title + "' by " + authorName + " created with " + numberOfBooks + " copies.");
    }

    // Getters for attributes
    public String getAuthorName() { return authorName; }
    public String getTitle() { return title; }
    public int getNumberOfBooks() { return numberOfBooks; }
    public void setNumberOfBooks(int numberOfBooks) { this.numberOfBooks = numberOfBooks; } // For issue/return

    /**
     * Method from diagram: removefirmcatalog() (corrected to removeFromCatalog)
     * This method would typically interact with the Catalog to remove the book.
     * For this class, it might decrement its own count or signal removal.
     * @param catalog The catalog to remove from.
     */
    public void removeFromCatalog(Catalog catalog) {
        catalog.removeBookFromCatalog(this);
        System.out.println("Book '" + title + "' signaled removal from catalog.");
    }

    /**
     * Method from diagram: addtocatalog() (corrected to addToCatalog)
     * This method would typically interact with the Catalog to add the book.
     * @param catalog The catalog to add to.
     */
    public void addToCatalog(Catalog catalog) {
        catalog.addBookToCatalog(this);
        System.out.println("Book '" + title + "' signaled addition to catalog.");
    }

    // Abstract methods to be implemented by subclasses if behavior differs
    public abstract String getBookType(); // e.g., "Reference" or "General"
}

// =============================================================================
// 3. ReferenceBook Class (Subclass of Books)
// Represents a reference book that cannot be issued out.
// =============================================================================
class ReferenceBook extends Books {
    /**
     * Constructor for the ReferenceBook class.
     * @param authorName The author of the reference book.
     * @param title The title of the reference book.
     * @param numberOfBooks The number of copies of this reference book.
     */
    public ReferenceBook(String authorName, String title, int numberOfBooks) {
        super(authorName, title, numberOfBooks);
        System.out.println("Reference Book: " + title);
    }

    /**
     * Method from diagram: Searchrefb() (corrected to SearchRefBook)
     * Searches for this specific reference book.
     * @param query The search query.
     * @return true if the book matches the query, false otherwise.
     */
    public boolean searchRefBook(String query) {
        boolean found = getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        getAuthorName().toLowerCase().contains(query.toLowerCase());
        if (found) {
            System.out.println("Reference Book found: " + getTitle());
        } else {
            System.out.println("Reference Book not found for query: " + query);
        }
        return found;
    }

    @Override
    public String getBookType() {
        return "Reference";
    }
}

// =============================================================================
// 4. GeneralBook Class (Subclass of Books)
// Represents a general book that can be issued out.
// =============================================================================
class GeneralBook extends Books {
    /**
     * Constructor for the GeneralBook class.
     * @param authorName The author of the general book.
     * @param title The title of the general book.
     * @param numberOfBooks The number of copies of this general book.
     */
    public GeneralBook(String authorName, String title, int numberOfBooks) {
        super(authorName, title, numberOfBooks);
        System.out.println("General Book: " + title);
    }

    @Override
    public String getBookType() {
        return "General";
    }

    // General books might have specific methods for being issued/returned beyond what's in Books
}

// =============================================================================
// 5. Librarian Class
// Represents a librarian who manages the library operations.
// =============================================================================
class Librarian {
    // Attributes from diagram:
    private String name;
    private String address;
    private int mobileNo; // 'mobileno'

    // For generating alert IDs uniquely for librarians
    private static final AtomicInteger alertIdCounter = new AtomicInteger(1000);

    /**
     * Constructor for the Librarian class.
     * @param name Librarian's name.
     * @param address Librarian's address.
     * @param mobileNo Librarian's mobile number.
     */
    public Librarian(String name, String address, int mobileNo) {
        this.name = name;
        this.address = address;
        this.mobileNo = mobileNo;
        System.out.println("Librarian '" + name + "' created.");
    }

    // Getters
    public String getName() { return name; }
    public int getMobileNo() { return mobileNo; }

    /**
     * Method from diagram: Updateinfo()
     * Updates librarian's information.
     * @param newAddress New address.
     * @param newMobileNo New mobile number.
     */
    public void updateInfo(String newAddress, int newMobileNo) {
        this.address = newAddress;
        this.mobileNo = newMobileNo;
        System.out.println(name + "'s info updated.");
    }

    /**
     * Method from diagram: Issuebooks()
     * Issues a book to a member.
     * @param book The book to issue.
     * @param member The member to whom the book is issued.
     * @return true if successful, false otherwise.
     */
    public boolean issueBooks(Books book, Member member) {
        if (book instanceof ReferenceBook) {
            System.out.println("Error: Reference books cannot be issued.");
            return false;
        }
        if (book.getNumberOfBooks() > 0) {
            book.setNumberOfBooks(book.getNumberOfBooks() - 1);
            member.addIssuedBook(book); // Member keeps track of issued books
            System.out.println(name + " issued '" + book.getTitle() + "' to " + member.getName() + ".");
            return true;
        } else {
            System.out.println("No copies of '" + book.getTitle() + "' available for issue.");
            return false;
        }
    }

    /**
     * Method from diagram: memberinfo()
     * Retrieves and displays member information.
     * @param member The member whose info to retrieve.
     */
    public void memberInfo(Member member) {
        System.out.println("\n--- Member Info for " + member.getName() + " ---");
        System.out.println("ID: " + member.getMemberNumber());
        System.out.println("Address: " + member.getAddress());
        System.out.println("Issued Books: " + member.getIssuedBooks().size());
        for (Books book : member.getIssuedBooks()) {
            System.out.println("  - " + book.getTitle());
        }
        System.out.println("-------------------------------------");
    }

    /**
     * Method from diagram: Searchbk() (corrected to SearchBook)
     * Searches for a book in a given catalog.
     * @param catalog The catalog to search in.
     * @param query The search query.
     * @return A list of matching books.
     */
    public List<Books> searchBook(Catalog catalog, String query) {
        System.out.println(name + " searching for book: " + query);
        return catalog.searching(query);
    }

    /**
     * Method from diagram: returnbk() (corrected to ReturnBook)
     * Processes a book return.
     * @param book The book being returned.
     * @param member The member returning the book.
     * @return true if successful, false otherwise.
     */
    public boolean returnBook(Books book, Member member) {
        if (member.removeIssuedBook(book)) {
            book.setNumberOfBooks(book.getNumberOfBooks() + 1);
            System.out.println(name + " processed return of '" + book.getTitle() + "' from " + member.getName() + ".");
            return true;
        } else {
            System.out.println("Error: '" + book.getTitle() + "' was not issued to " + member.getName() + " or not found.");
            return false;
        }
    }

    /**
     * Creates and sends an alert.
     * @param issuedDate The date the issue was noted.
     * @param bookName The name of the book involved.
     * @param returnDate The expected return date.
     * @param fine The fine amount.
     * @param targetMember The member the alert is for.
     */
    public Alert createAndSendAlert(LocalDate issuedDate, String bookName, LocalDate returnDate, int fine, Member targetMember) {
        Alert newAlert = new Alert(alertIdCounter.getAndIncrement(), issuedDate, bookName, returnDate, fine);
        newAlert.sendToLibrarian(); // This alert is generated by a librarian
        targetMember.addAlert(newAlert); // Member receives the alert
        System.out.println(name + " created and sent alert for " + bookName + " to " + targetMember.getName());
        return newAlert;
    }
}

// =============================================================================
// 6. Member Class (Abstract Base Class for Faculty and Student)
// Represents a generic library member.
// =============================================================================
abstract class Member { // Made abstract as "Member" is generalized
    // Attributes from diagram:
    private String name; // 'Mname'
    private String address; // 'Maddress'
    private int memberNumber; // 'Mno'

    // Association: A Member can have many Alerts
    private List<Alert> alerts;
    // Keep track of books currently issued to the member
    private List<Books> issuedBooks;

    private static final AtomicInteger memberIdCounter = new AtomicInteger(100); // For unique member IDs

    /**
     * Constructor for the Member class.
     * @param name Member's name.
     * @param address Member's address.
     */
    public Member(String name, String address) {
        this.name = name;
        this.address = address;
        this.memberNumber = memberIdCounter.getAndIncrement(); // Assign unique ID
        this.alerts = new ArrayList<>();
        this.issuedBooks = new ArrayList<>();
        System.out.println("Member '" + name + "' (ID: " + memberNumber + ") created.");
    }

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getMemberNumber() { return memberNumber; }
    public List<Alert> getAlerts() { return new ArrayList<>(alerts); }
    public List<Books> getIssuedBooks() { return new ArrayList<>(issuedBooks); }

    /**
     * Adds an issued book to the member's list.
     * @param book The book that has been issued.
     */
    public void addIssuedBook(Books book) {
        if (!issuedBooks.contains(book)) { // Prevent duplicates if logic allows re-issuing
            issuedBooks.add(book);
        }
    }

    /**
     * Removes an issued book from the member's list.
     * @param book The book that is being returned.
     * @return true if the book was found and removed, false otherwise.
     */
    public boolean removeIssuedBook(Books book) {
        return issuedBooks.remove(book);
    }

    /**
     * Method from diagram: mrequestforbk() (corrected to requestForBook)
     * Simulates a member requesting a book.
     * @param catalog The catalog to search in.
     * @param bookTitle The title of the book being requested.
     * @return The found book, or null if not found.
     */
    public Books requestForBook(Catalog catalog, String bookTitle) {
        System.out.println(name + " requesting book: " + bookTitle);
        List<Books> foundBooks = catalog.searching(bookTitle);
        if (!foundBooks.isEmpty()) {
            System.out.println("Book '" + bookTitle + "' found.");
            // In a real system, would handle multiple copies, specific book selection
            return foundBooks.get(0); // Return the first match
        }
        System.out.println("Book '" + bookTitle + "' not found in catalog.");
        return null;
    }

    /**
     * Method from diagram: Mreturnbk() (corrected to returnBook)
     * Simulates a member returning a book.
     * This usually interacts with a librarian, but here the member initiates.
     * @param librarian The librarian processing the return.
     * @param book The book to return.
     * @return true if successful, false otherwise.
     */
    public boolean returnBook(Librarian librarian, Books book) {
        System.out.println(name + " returning book: " + book.getTitle());
        return librarian.returnBook(book, this);
    }

    /**
     * Adds an alert to the member's list of alerts.
     * @param alert The alert to add.
     */
    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        System.out.println(name + " received an alert: " + alert.getBookName());
    }
}

// =============================================================================
// 7. FacultyMember Class (Subclass of Member)
// Represents a faculty member.
// =============================================================================
class FacultyMember extends Member {
    // Attributes from diagram:
    private String facultyName; // 'Fname' - potentially redundant if Member.name is primary
    private String facultyCollection; // 'facultycoll'

    /**
     * Constructor for the FacultyMember class.
     * @param name Faculty member's name.
     * @param address Faculty member's address.
     * @param facultyCollection The collection/department the faculty belongs to.
     */
    public FacultyMember(String name, String address, String facultyCollection) {
        super(name, address);
        this.facultyName = name; // Assuming Fname maps to general name for consistency
        this.facultyCollection = facultyCollection;
        System.out.println("Faculty Member '" + name + "' initialized for collection: " + facultyCollection);
    }

    // Getters for specific attributes
    public String getFacultyCollection() { return facultyCollection; }

    /**
     * Method from diagram: checkoutbk() (corrected to checkoutBook)
     * Simulates checking out a book (e.g., specific rules for faculty).
     * This would typically involve a librarian.
     * @param librarian The librarian to process the checkout.
     * @param book The book to checkout.
     * @return true if successful, false otherwise.
     */
    public boolean checkoutBook(Librarian librarian, Books book) {
        System.out.println("Faculty member " + getName() + " attempting to checkout: " + book.getTitle());
        return librarian.issueBooks(book, this);
    }
}

// =============================================================================
// 8. Student Class (Subclass of Member)
// Represents a student member.
// =============================================================================
class Student extends Member {
    // Attributes from diagram:
    private String studentName; // 'sName' - potentially redundant if Member.name is primary
    private String studentCollection; // 'Studentcoll' - assuming this is like department/major

    /**
     * Constructor for the Student class.
     * @param name Student's name.
     * @param address Student's address.
     * @param studentCollection The collection/department the student belongs to.
     */
    public Student(String name, String address, String studentCollection) {
        super(name, address);
        this.studentName = name; // Assuming sName maps to general name for consistency
        this.studentCollection = studentCollection;
        System.out.println("Student '" + name + "' initialized for collection: " + studentCollection);
    }

    // Getters for specific attributes
    public String getStudentCollection() { return studentCollection; }

    /**
     * Method from diagram: checkoutbk() (corrected to checkoutBook)
     * Simulates checking out a book (e.g., specific rules for students).
     * This would typically involve a librarian.
     * @param librarian The librarian to process the checkout.
     * @param book The book to checkout.
     * @return true if successful, false otherwise.
     */
    public boolean checkoutBook(Librarian librarian, Books book) {
        System.out.println("Student " + getName() + " attempting to checkout: " + book.getTitle());
        return librarian.issueBooks(book, this);
    }
}

// =============================================================================
// 9. Alert Class
// Represents an alert or notification regarding book status (e.g., overdue, fine).
// =============================================================================
class Alert {
    // Attributes from diagram:
    private int alertId; // Added for unique identification
    private LocalDate issueDate; // 'Issuedate'
    private String bookName; // 'Bookname'
    private LocalDate returnDate; // 'returndate'
    private int fineAmount; // 'Fine'

    /**
     * Constructor for the Alert class.
     * @param alertId Unique ID for the alert.
     * @param issueDate The date the book was issued.
     * @param bookName The name of the book related to the alert.
     * @param returnDate The expected/actual return date.
     * @param fineAmount The fine incurred (if any).
     */
    public Alert(int alertId, LocalDate issueDate, String bookName, LocalDate returnDate, int fineAmount) {
        this.alertId = alertId;
        this.issueDate = issueDate;
        this.bookName = bookName;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        System.out.println("Alert " + alertId + " created for '" + bookName + "'.");
    }

    // Getters
    public int getAlertId() { return alertId; }
    public LocalDate getIssueDate() { return issueDate; }
    public String getBookName() { return bookName; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getFineAmount() { return fineAmount; }

    /**
     * Method from diagram: Finecall()
     * Simulates calling for fine payment.
     */
    public void fineCall() {
        System.out.println("Alert " + alertId + ": Please pay fine of " + fineAmount + " for '" + bookName + "'.");
        // In a real system, this would trigger payment processing.
    }

    /**
     * Method from diagram: ViewAlert()
     * Displays the details of the alert.
     */
    public void viewAlert() {
        System.out.println("\n--- Alert Details (ID: " + alertId + ") ---");
        System.out.println("Book: " + bookName);
        System.out.println("Issued On: " + issueDate);
        System.out.println("Due/Return Date: " + returnDate);
        System.out.println("Fine: " + fineAmount);
        System.out.println("-------------------------------------");
    }

    /**
     * Method from diagram: sendtolibraian() (corrected to sendToLibrarian)
     * Marks this alert as sent to a librarian (for action/record).
     * This might be a flag or log entry in a real system.
     */
    public void sendToLibrarian() {
        System.out.println("Alert " + alertId + " for '" + bookName + "' sent to librarian for review.");
    }

    /**
     * Method from diagram: deletealertbyno() (corrected to deleteAlertByNo)
     * Deletes the alert by its number.
     * In a real system, this would remove it from a collection/database.
     */
    public void deleteAlertByNo() {
        System.out.println("Alert " + alertId + " for '" + bookName + "' marked as deleted.");
        // Logic to remove from relevant lists/database.
    }
}

// =============================================================================
// Main Class for Demonstration
// This class will set up objects and simulate interactions based on the diagram.
// =============================================================================
public class LibrarySystemDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting Library System Demonstration ---");

        // 1. Create Catalog
        Catalog mainCatalog = new Catalog("General Collection", 0); // Copies will be updated as books are added

        // 2. Create Books (Reference and General)
        ReferenceBook refJava = new ReferenceBook("Herbert Schildt", "Java: The Complete Reference", 1);
        GeneralBook genOOP = new GeneralBook("Erich Gamma", "Design Patterns", 3);
        GeneralBook genFiction = new GeneralBook("Jane Austen", "Pride and Prejudice", 5);

        // Add books to catalog
        refJava.addToCatalog(mainCatalog);
        genOOP.addToCatalog(mainCatalog);
        genFiction.addToCatalog(mainCatalog);

        System.out.println("Total copies in main catalog: " + mainCatalog.getNumberOfCopies());


        // 3. Create Librarian
        Librarian john = new Librarian("John Doe", "123 Library Ln", 1234567890);

        // 4. Create Members (Faculty and Student)
        FacultyMember profSmith = new FacultyMember("Prof. Smith", "456 University Rd", "Computer Science");
        Student alice = new Student("Alice Johnson", "789 Dormitory St", "Engineering");
        Student bob = new Student("Bob Williams", "101 Main St", "Arts");


        // 5. Simulate Library Operations

        // Librarian searches for books
        System.out.println("\n--- Librarian Searching ---");
        List<Books> searchResults = john.searchBook(mainCatalog, "design");
        searchResults.forEach(book -> System.out.println("  Found: " + book.getTitle() + " (" + book.getBookType() + ")"));

        // Alice (Student) requests a book
        System.out.println("\n--- Student Requesting Book ---");
        Books requestedBook = alice.requestForBook(mainCatalog, "Pride and Prejudice");

        // Alice (Student) checks out a book
        System.out.println("\n--- Student Checkout ---");
        if (requestedBook != null) {
            alice.checkoutBook(john, requestedBook);
            System.out.println("Copies of '" + requestedBook.getTitle() + "' remaining: " + requestedBook.getNumberOfBooks());
        }

        // Prof. Smith (Faculty) checks out a book
        System.out.println("\n--- Faculty Checkout ---");
        Books facultyBook = mainCatalog.searching("Java").get(0); // Assuming Java book is found
        if (facultyBook != null) {
            profSmith.checkoutBook(john, facultyBook);
            System.out.println("Copies of '" + facultyBook.getTitle() + "' remaining: " + facultyBook.getNumberOfBooks());
        }

        // Try to issue a reference book (should fail)
        System.out.println("\n--- Attempt to Issue Reference Book ---");
        john.issueBooks(refJava, bob); // Should report error

        // Librarian views member info
        System.out.println("\n--- Librarian Views Member Info ---");
        john.memberInfo(alice);
        john.memberInfo(profSmith);

        // Alice returns a book
        System.out.println("\n--- Student Returns Book ---");
        boolean returned = alice.returnBook(john, genFiction);
        if (returned) {
            System.out.println("Copies of '" + genFiction.getTitle() + "' remaining: " + genFiction.getNumberOfBooks());
        }

        // Create and send an alert for an overdue book (simulated)
        System.out.println("\n--- Creating and Sending Alert ---");
        LocalDate issued = LocalDate.of(2024, 4, 1);
        LocalDate dueDate = LocalDate.of(2024, 4, 15);
        Alert overdueAlert = john.createAndSendAlert(issued, "Design Patterns", dueDate, 5, bob); // Bob gets an alert
        overdueAlert.fineCall();
        overdueAlert.viewAlert();

        // Bob views his alerts
        System.out.println("\n--- Bob Views His Alerts ---");
        bob.getAlerts().forEach(Alert::viewAlert);

        // Delete an alert
        System.out.println("\n--- Deleting Alert ---");
        overdueAlert.deleteAlertByNo();


        System.out.println("\n--- Library System Demonstration Complete ---");
    }
}