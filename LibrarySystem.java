import java.util.ArrayList;
import java.util.List;
import java.util.Date; // For tracking issue date for fine calculation
import java.util.Objects; // For Objects.equals and Objects.hash

// User.java
class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Account account; // A User 'has' an Account

    public User(String userId, String name, String email, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        // Account will be created and linked separately, or in a system initialization
    }

    // Getters for attributes
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Account getAccount() {
        return account;
    }

    // Setter for Account (to link it after creation)
    public void setAccount(Account account) {
        this.account = account;
    }

    // Operations
    public boolean login(String enteredUserId, String password) {
        // In a real system, this would involve password checking against a stored hash
        // For simplicity, assuming a dummy login or just ID check
        if (this.userId.equals(enteredUserId)) {
            System.out.println(this.name + " logged in.");
            return true;
        }
        System.out.println("Login failed for " + enteredUserId);
        return false;
    }

    public void logout() {
        System.out.println(this.name + " logged out.");
    }

    public Book searchBook(String query, List<Book> availableBooks) {
        System.out.println(this.name + " is searching for: " + query);
        for (Book book : availableBooks) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                book.getISBN().equals(query)) { // Direct ISBN match
                System.out.println("Found book: " + book.getTitle() + " by " + book.getAuthor());
                return book;
            }
        }
        System.out.println("No book found matching: " + query);
        return null;
    }

    public void reserveBook(Book book) {
        if (book.checkAvailability() > 0) {
            System.out.println(this.name + " reserved book: " + book.getTitle());
            // In a real system, this would mark the book as reserved for this user
            // and might deduct from copiesAvailable but not issue it yet.
            // For this simplified model, we'll just print a message.
        } else {
            System.out.println("Cannot reserve " + book.getTitle() + ": No copies available.");
        }
    }
}

// Account.java
class Account {
    private String accountId;
    private User user; // An Account 'has' a User (bi-directional link in diagram)
    private List<IssuedBook> issuedBooks; // List of books currently issued to this account

    public Account(String accountId, User user) {
        this.accountId = accountId;
        this.user = user;
        this.issuedBooks = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public User getUser() {
        return user;
    }

    public List<IssuedBook> getIssuedBooks() {
        return issuedBooks;
    }

    public void addIssuedBook(Book book, Date issueDate) {
        this.issuedBooks.add(new IssuedBook(book, issueDate));
        System.out.println(book.getTitle() + " issued to account " + accountId);
    }

    public void removeIssuedBook(Book book) {
        IssuedBook foundIssuedBook = null;
        for (IssuedBook ib : issuedBooks) {
            if (ib.getBook().equals(book)) {
                foundIssuedBook = ib;
                break;
            }
        }
        if (foundIssuedBook != null) {
            this.issuedBooks.remove(foundIssuedBook);
            System.out.println(book.getTitle() + " returned from account " + accountId);
        } else {
            System.out.println(book.getTitle() + " was not issued to this account.");
        }
    }

    public double fineForLateReturn() {
        double totalFine = 0.0;
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
        long DUE_DAYS = 14; // Example: books are due in 14 days
        double FINE_PER_DAY = 0.50; // Example: $0.50 per day fine

        Date currentDate = new Date(); // Current date

        System.out.println("Checking for late return fines for account " + accountId + "...");
        for (IssuedBook ib : issuedBooks) {
            long diffMillis = currentDate.getTime() - ib.getIssueDate().getTime();
            long diffDays = diffMillis / MILLIS_PER_DAY;

            if (diffDays > DUE_DAYS) {
                long lateDays = diffDays - DUE_DAYS;
                double fine = lateDays * FINE_PER_DAY;
                totalFine += fine;
                System.out.println("  - Book: " + ib.getBook().getTitle() + " is " + lateDays + " days late. Fine: $" + String.format("%.2f", fine));
            }
        }
        if (totalFine > 0) {
            System.out.println("Total outstanding fine for " + user.getName() + ": $" + String.format("%.2f", totalFine));
        } else {
            System.out.println("No outstanding fines for " + user.getName() + ".");
        }
        return totalFine;
    }

    // Helper class to store issued book with its issue date
    private static class IssuedBook {
        private Book book;
        private Date issueDate;

        public IssuedBook(Book book, Date issueDate) {
            this.book = book;
            this.issueDate = issueDate;
        }

        public Book getBook() {
            return book;
        }

        public Date getIssueDate() {
            return issueDate;
        }
    }
}

// Librarian.java
class Librarian {
    private String employeeId;
    private String name;
    private String email;

    public Librarian(String employeeId, String name, String email) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
    }

    // Getters for attributes
    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Operations
    public void addBook(Book book, List<Book> libraryCatalog) {
        libraryCatalog.add(book);
        System.out.println(this.name + " added book: " + book.getTitle());
    }

    public void removeBook(Book book, List<Book> libraryCatalog) {
        if (libraryCatalog.remove(book)) {
            System.out.println(this.name + " removed book: " + book.getTitle());
        } else {
            System.out.println(this.name + " could not remove " + book.getTitle() + ": Not found in catalog.");
        }
    }

    public void manageAccounts(Account account) {
        System.out.println(this.name + " is managing account: " + account.getAccountId() + " for user " + account.getUser().getName());
        // This method would encapsulate various account management tasks like:
        // - viewing issued books: account.getIssuedBooks()
        // - checking fines: account.fineForLateReturn()
        // - updating user info (via user object linked to account)
    }

    // Methods to issue and return books (operations on Book, affecting Account)
    public void issueBook(Book book, Account account) {
        if (book.checkAvailability() > 0) {
            book.issueBook(); // Decrement copiesAvailable
            account.addIssuedBook(book, new Date()); // Add to user's issued list with current date
            System.out.println(this.name + " issued '" + book.getTitle() + "' to " + account.getUser().getName());
        } else {
            System.out.println("Cannot issue " + book.getTitle() + ": No copies available.");
        }
    }

    public void returnBook(Book book, Account account) {
        book.returnBook(); // Increment copiesAvailable
        account.removeIssuedBook(book); // Remove from user's issued list
        System.out.println(this.name + " processed return of '" + book.getTitle() + "' from " + account.getUser().getName());
        account.fineForLateReturn(); // Check for fines on return
    }
}

// Book.java
class Book {
    private String ISBN;
    private String title;
    private String author;
    private String publisher;
    private int yearPublished;
    private int copiesAvailable;

    public Book(String ISBN, String title, String author, String publisher, int yearPublished, int copiesAvailable) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.yearPublished = yearPublished;
        this.copiesAvailable = copiesAvailable;
    }

    // Getters for attributes
    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    // Operations
    public int checkAvailability() {
        return copiesAvailable;
    }

    public void issueBook() {
        if (copiesAvailable > 0) {
            copiesAvailable--;
            System.out.println("One copy of '" + title + "' issued. Copies remaining: " + copiesAvailable);
        } else {
            System.out.println("No copies of '" + title + "' available to issue.");
        }
    }

    public void returnBook() {
        copiesAvailable++;
        System.out.println("One copy of '" + title + "' returned. Copies remaining: " + copiesAvailable);
    }

    // Corrected Override for equals and hashCode for proper list comparisons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(ISBN, book.ISBN); // Books are uniquely identified by ISBN
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBN);
    }
}

// Main class to demonstrate the Library Management System
public class LibraryManagementSystem {
    public static void main(String[] args) {
        // --- Setup the Library ---
        List<Book> libraryCatalog = new ArrayList<>();

        // Create some books
        Book book1 = new Book("978-0321765723", "The Lord of the Rings", "J.R.R. Tolkien", "Allen & Unwin", 1954, 5);
        Book book2 = new Book("978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald", "Charles Scribner's Sons", 1925, 3);
        Book book3 = new Book("978-0141439518", "Pride and Prejudice", "Jane Austen", "Penguin Classics", 1813, 2);
        Book book4 = new Book("978-0439023528", "The Hunger Games", "Suzanne Collins", "Scholastic Press", 2008, 1);

        // Create a Librarian
        Librarian mary = new Librarian("L001", "Mary Poppins", "mary@library.com");

        // Add books to the library catalog
        mary.addBook(book1, libraryCatalog);
        mary.addBook(book2, libraryCatalog);
        mary.addBook(book3, libraryCatalog);
        mary.addBook(book4, libraryCatalog);

        // --- Create Users and Accounts ---
        User alice = new User("U001", "Alice Smith", "alice@example.com", "111-222-3333");
        Account aliceAccount = new Account("A001", alice);
        alice.setAccount(aliceAccount); // Link account to user

        User bob = new User("U002", "Bob Johnson", "bob@example.com", "444-555-6666");
        Account bobAccount = new Account("A002", bob);
        bob.setAccount(bobAccount); // Link account to user

        System.out.println("\n--- User Operations ---");

        // Alice logs in and searches for a book
        alice.login("U001", "password123");
        Book foundBook = alice.searchBook("Lord", libraryCatalog); // Alice searches for "Lord"

        // Alice tries to reserve a book
        if (foundBook != null) {
            alice.reserveBook(foundBook);
        }

        // Bob searches for a book that is almost out of copies
        bob.searchBook("Hunger", libraryCatalog);

        System.out.println("\n--- Librarian Operations (Issuing Books) ---");

        // Mary issues book1 to Alice
        mary.issueBook(book1, aliceAccount); // Book 1 has 5 copies, now 4
        System.out.println("Copies of " + book1.getTitle() + " after issue: " + book1.getCopiesAvailable());

        // Mary issues book4 to Bob
        mary.issueBook(book4, bobAccount); // Book 4 has 1 copy, now 0
        System.out.println("Copies of " + book4.getTitle() + " after issue: " + book4.getCopiesAvailable());

        // Alice tries to issue an unavailable book (simulating)
        mary.issueBook(book4, aliceAccount); // Should fail

        System.out.println("\n--- Account Operations (Checking Fines) ---");
        aliceAccount.fineForLateReturn(); // Should be no fine
        bobAccount.fineForLateReturn(); // Should be no fine

        // Simulate time passing to incur a fine for Alice (for demonstration)
        // This is a crude simulation; in a real app, dates would be managed carefully.
        System.out.println("\n--- Simulating time passing for fines ---");
        try {
            // Wait for 15 days (simulated)
            Thread.sleep(15 * 1000); // 15 seconds for demonstration purposes
                                     // In a real scenario, you'd just set issueDate to a past date.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // To properly simulate late return, we would modify `issueDate` of the `IssuedBook` directly
        // For example, if `IssuedBook` had a public setter for `issueDate` or was constructed with specific past dates.
        // For this example, let's just create a new scenario for fine checking:
        System.out.println("\n--- Checking fines after simulated time passing ---");
        // Re-issue book1 to Alice, but make it appear as if issued long ago
        // This is a workaround for this single-file demo; in a real app,
        // you'd typically manage the `Date` objects properly when adding `IssuedBook`.
        // Let's create a new IssuedBook directly in Alice's account for demonstration of fines.
        Date pastDate = new Date(System.currentTimeMillis() - (20 * 24 * 60 * 60 * 1000L)); // 20 days ago
        aliceAccount.addIssuedBook(book2, pastDate); // Add book2 to Alice's account, issued 20 days ago

        aliceAccount.fineForLateReturn(); // Alice should now have a fine for book2

        System.out.println("\n--- Librarian Operations (Returning Books) ---");
        mary.returnBook(book1, aliceAccount); // Alice returns book1
        System.out.println("Copies of " + book1.getTitle() + " after return: " + book1.getCopiesAvailable());
        aliceAccount.fineForLateReturn(); // Check fines after returning one book

        mary.returnBook(book4, bobAccount); // Bob returns book4
        System.out.println("Copies of " + book4.getTitle() + " after return: " + book4.getCopiesAvailable());

        mary.returnBook(book2, aliceAccount); // Alice returns book2 (the one that had a fine)
        System.out.println("Copies of " + book2.getTitle() + " after return: " + book2.getCopiesAvailable());
        aliceAccount.fineForLateReturn(); // Check fines after returning the late book

        System.out.println("\n--- Librarian Management ---");
        mary.manageAccounts(aliceAccount);
        mary.removeBook(book3, libraryCatalog); // Mary removes book3
        mary.removeBook(new Book("NonExistentISBN", "Fake Book", "Fake Author", "Fake Publisher", 2000, 1), libraryCatalog);
    }
}
