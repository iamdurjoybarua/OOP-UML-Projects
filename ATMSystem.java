import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ATM.java
class ATM {
    private String location;
    private int atmId;
    private float balance;
    private List<String> supportedCards; // Represents supported card types, e.g., "Visa", "MasterCard"

    public ATM(String location, int atmId, float balance) {
        this.location = location;
        this.atmId = atmId;
        this.balance = balance;
        this.supportedCards = new ArrayList<>();
    }

    public String getLocation() {
        return location;
    }

    public int getAtmId() {
        return atmId;
    }

    public float getBalance() {
        return balance;
    }

    public void addSupportedCard(String cardType) {
        this.supportedCards.add(cardType);
    }

    public List<String> getSupportedCards() {
        return supportedCards;
    }

    public void dispenseCash(int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            System.out.println("Dispensing $" + amount);
        } else {
            System.out.println("Insufficient funds in ATM.");
        }
    }

    public void acceptDeposit(float amount) {
        this.balance += amount;
        System.out.println("Accepted deposit of $" + amount);
    }

    public void displayMessage(String message) {
        System.out.println("ATM Message: " + message);
    }
}

// User.java
class User {
    private String name;
    private String cardNumber; // This will likely be an identifier linked to the Card object
    private String pin;

    // Simple method to generate a unique transaction ID (for demonstration purposes)
    // Made public and static to be accessible from Main or other classes directly via User.generateTransactionId()
    private static int nextTransactionId = 1;
    public static synchronized int generateTransactionId() {
        return nextTransactionId++;
    }

    public User(String name, String cardNumber, String pin) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public boolean authenticate(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    // This method would typically interact with the Bank and ATM
    public void requestCash(int amount, ATM atm, Bank bank, Card card) {
        if (authenticate(this.pin)) { // Authenticate with the user's actual pin
            if (card.validateCard()) {
                // In a real system, bank would verify user balance and approve transaction
                Transaction transaction = new Transaction(User.generateTransactionId(), amount, "Withdrawal", card.getCardNumber());
                if (bank.processTransaction(transaction)) {
                    atm.dispenseCash(amount);
                    atm.displayMessage("Cash dispensed successfully.");
                    new Receipt("R" + transaction.getTransactionId(), transaction).printReceipt();
                } else {
                    atm.displayMessage("Transaction failed. Insufficient bank balance or other issue.");
                }
            } else {
                atm.displayMessage("Card is invalid.");
            }
        } else {
            atm.displayMessage("Authentication failed.");
        }
    }

    // This method would typically interact with the Bank
    public void checkBalance(Bank bank, Card card) {
        if (authenticate(this.pin)) { // Authenticate with the user's actual pin
            if (card.validateCard()) {
                // In a real system, bank would return the actual balance
                float balance = bank.getUserBalance(this.cardNumber); // Placeholder for actual bank interaction
                System.out.println("Your current balance is: $" + balance);
            } else {
                System.out.println("Card is invalid.");
            }
        } else {
            System.out.println("Authentication failed.");
        }
    }
}

// Bank.java
class Bank {
    private String name;
    private List<String> branches;
    private List<User> users;
    // For simplicity, maintaining user balances here. In real system, this would be in a database.
    private Map<String, Float> userBalances; // Maps card number to balance

    public Bank(String name) {
        this.name = name;
        this.branches = new ArrayList<>();
        this.users = new ArrayList<>();
        this.userBalances = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addBranch(String branch) {
        this.branches.add(branch);
    }

    public List<String> getBranches() {
        return branches;
    }

    public void addUser(User user, float initialBalance) {
        this.users.add(user);
        this.userBalances.put(user.getCardNumber(), initialBalance);
    }

    public List<User> getUsers() {
        return users;
    }

    public float getUserBalance(String cardNumber) {
        return userBalances.getOrDefault(cardNumber, 0.0f);
    }

    public boolean processTransaction(Transaction transaction) {
        // In a real system, this would involve complex logic,
        // updating databases, checking for fraud, etc.
        System.out.println("Bank processing transaction: " + transaction.getTransactionType() +
                           " of $" + transaction.getAmount() + " for card " + transaction.getCardNumber());

        if ("Withdrawal".equals(transaction.getTransactionType())) {
            float currentBalance = userBalances.getOrDefault(transaction.getCardNumber(), 0.0f);
            if (currentBalance >= transaction.getAmount()) {
                userBalances.put(transaction.getCardNumber(), currentBalance - transaction.getAmount());
                System.out.println("Bank: Withdrawal successful for " + transaction.getCardNumber());
                return true;
            } else {
                System.out.println("Bank: Insufficient funds for withdrawal for " + transaction.getCardNumber());
                return false;
            }
        } else if ("Deposit".equals(transaction.getTransactionType())) {
            float currentBalance = userBalances.getOrDefault(transaction.getCardNumber(), 0.0f);
            userBalances.put(transaction.getCardNumber(), currentBalance + transaction.getAmount());
            System.out.println("Bank: Deposit successful for " + transaction.getCardNumber());
            return true;
        }
        return false; // Unknown transaction type
    }
}

// Card.java
class Card {
    private String cardNumber;
    private LocalDate expirationDate;
    private String cardHolderName;

    public Card(String cardNumber, LocalDate expirationDate, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public boolean validateCard() {
        // Simple validation: check if not expired
        return LocalDate.now().isBefore(expirationDate.plusDays(1)); // Add a day to include the expiration date itself
    }
}

// Transaction.java
class Transaction {
    private int transactionId;
    private float amount;
    private String transactionType; // e.g., "Withdrawal", "Deposit", "Balance Inquiry"
    private LocalDateTime transactionDate;
    private String cardNumber; // Added to link transaction to a card/user

    public Transaction(int transactionId, float amount, String transactionType, String cardNumber) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = LocalDateTime.now(); // Set to current time
        this.cardNumber = cardNumber;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public float getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}

// Receipt.java
class Receipt {
    private String receiptId;
    private Transaction transaction;

    public Receipt(String receiptId, Transaction transaction) {
        this.receiptId = receiptId;
        this.transaction = transaction;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void printReceipt() {
        System.out.println("--- Transaction Receipt ---");
        System.out.println("Receipt ID: " + receiptId);
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("Type: " + transaction.getTransactionType());
        System.out.println("Amount: $" + transaction.getAmount());
        System.out.println("Date: " + transaction.getTransactionDate());
        System.out.println("---------------------------");
    }
}

// Main.java (Demonstrates usage and relationships)
public class AtmSystem {
    public static void main(String[] args) {
        // 1. Create Bank
        Bank myBank = new Bank("My Awesome Bank");
        myBank.addBranch("Downtown Branch");

        // 2. Create Users and Cards
        User user1 = new User("Alice Smith", "1234567890123456", "1234");
        Card card1 = new Card("1234567890123456", LocalDate.of(2028, 12, 31), "Alice Smith");
        myBank.addUser(user1, 1000.00f); // Alice has $1000

        User user2 = new User("Bob Johnson", "9876543210987654", "5678");
        Card card2 = new Card("9876543210987654", LocalDate.of(2026, 6, 15), "Bob Johnson");
        myBank.addUser(user2, 500.00f); // Bob has $500

        // 3. Create ATM
        ATM atm = new ATM("City Center Plaza", 101, 50000.00f);
        atm.addSupportedCard("Visa");
        atm.addSupportedCard("MasterCard");

        System.out.println("--- Alice's Transactions ---");
        // Alice checks balance
        user1.checkBalance(myBank, card1);

        // Alice requests cash
        System.out.println("\nAlice trying to withdraw $200...");
        user1.requestCash(200, atm, myBank, card1);
        atm.displayMessage("Please take your cash.");
        user1.checkBalance(myBank, card1); // Check balance after withdrawal

        // Simulate a deposit for Alice
        System.out.println("\nAlice trying to deposit $150...");
        float depositAmount = 150.00f;
        // Corrected: Calling generateTransactionId() as a static method of User
        Transaction depositTxn = new Transaction(User.generateTransactionId(), depositAmount, "Deposit", card1.getCardNumber());
        if (myBank.processTransaction(depositTxn)) {
            atm.acceptDeposit(depositAmount);
            Receipt depositReceipt = new Receipt("R" + depositTxn.getTransactionId(), depositTxn);
            depositReceipt.printReceipt();
        }
        user1.checkBalance(myBank, card1);


        System.out.println("\n--- Bob's Transactions ---");
        // Bob tries to withdraw more than he has
        user2.checkBalance(myBank, card2);
        System.out.println("\nBob trying to withdraw $600...");
        user2.requestCash(600, atm, myBank, card2);
        user2.checkBalance(myBank, card2);

        // Simulate an invalid card scenario (expired card)
        System.out.println("\n--- Invalid Card Scenario (Expired) ---");
        Card expiredCard = new Card("1111222233334444", LocalDate.of(2020, 1, 1), "Expired User");
        User expiredUser = new User("Expired User", "1111222233334444", "0000");
        atm.displayMessage("Expired card validation:");
        expiredUser.requestCash(50, atm, myBank, expiredCard);

        // Simulate an invalid card scenario (unsupported card type by ATM) - conceptual, as ATM only stores types
        // In a real system, the ATM would check if the card type is supported before processing
        // For this simplified model, we're assuming the card itself is valid if it passes validation.
    }
}
