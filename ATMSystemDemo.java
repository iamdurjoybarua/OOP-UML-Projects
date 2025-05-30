import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random; // For PIN generation demo

// =============================================================================
// 1. Bank Class
// Corresponds to the 'Bank' class in the diagram.
// It maintains ATMs and manages Debit Cards.
// =============================================================================
class Bank {
    // Attributes from diagram:
    private String code;
    private String address;

    // Associations: These lists hold references to objects maintained/managed by the bank.
    // One-to-many relationship: A Bank can maintain many ATMs.
    private List<ATM> atms;
    // One-to-many relationship: A Bank can manage many Debit Cards.
    private List<DebitCard> debitCards;
    // To easily find accounts by ID (e.g., for transfers). One-to-many with Accounts.
    private Map<String, Account> accounts;

    /**
     * Constructor for the Bank class.
     * @param code Unique identifier for the bank.
     * @param address Physical address of the bank headquarters.
     */
    public Bank(String code, String address) {
        this.code = code;
        this.address = address;
        this.atms = new ArrayList<>();
        this.debitCards = new ArrayList<>();
        this.accounts = new HashMap<>();
        System.out.println("Bank " + code + " created at " + address);
    }

    // Getters for attributes (encapsulation)
    public String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Method from diagram: 'Manages()'
     * Adds a Debit Card to the bank's managed list.
     * @param debitCard The DebitCard object to manage.
     */
    public void manages(DebitCard debitCard) {
        if (!debitCards.contains(debitCard)) {
            debitCards.add(debitCard);
            System.out.println("Bank " + this.code + " now manages Debit Card " + debitCard.getCardId());
        }
    }

    /**
     * Method from diagram: 'Maintains()'
     * Adds an ATM to the bank's maintained list.
     * @param atm The ATM object to maintain.
     */
    public void maintains(ATM atm) {
        if (!atms.contains(atm)) {
            atms.add(atm);
            System.out.println("Bank " + this.code + " now maintains ATM at " + atm.getLocation());
        }
    }

    /**
     * Registers an account with the bank for easy lookup (e.g., during transfers).
     * @param account The Account object to register.
     */
    public void addAccount(Account account) {
        if (!accounts.containsKey(account.getAccountId())) {
            accounts.put(account.getAccountId(), account);
            System.out.println("Account " + account.getAccountId() + " registered with Bank " + this.code + ".");
        }
    }

    /**
     * Helper method to retrieve an account by its ID.
     * @param accountId The ID of the account to retrieve.
     * @return The Account object if found, otherwise null.
     */
    public Account getAccountById(String accountId) {
        return accounts.get(accountId);
    }
}

// =============================================================================
// 2. ATM Class
// Corresponds to the 'ATM' class in the diagram.
// Represents an Automated Teller Machine.
// =============================================================================
class ATM {
    // Attributes from diagram:
    private String location;
    // Association: ATM is managed by a Bank (composition/aggregation, depending on lifecycle)
    private Bank managedBy;
    // Association: ATM identifies/records ATM_Transactions (one-to-many)
    private List<ATMTransaction> transactions;

    /**
     * Constructor for the ATM class.
     * @param location Physical location of the ATM.
     * @param managedByBank Reference to the Bank object that manages this ATM.
     */
    public ATM(String location, Bank managedByBank) {
        this.location = location;
        this.managedBy = managedByBank;
        this.transactions = new ArrayList<>();
        System.out.println("ATM created at " + location + ", managed by Bank " + managedByBank.getCode());
    }

    // Getters
    public String getLocation() {
        return location;
    }

    /**
     * Method from diagram: 'Transaction()'
     * Records an ATM transaction in the ATM's internal log.
     * @param atmTransaction The ATMTransaction object to record.
     */
    public void recordTransaction(ATMTransaction atmTransaction) {
        this.transactions.add(atmTransaction);
        System.out.println("ATM at " + this.location + " recorded transaction " + atmTransaction.getTransactionId());
    }

    /**
     * Method from diagram: 'Identifies()'
     * Authenticates the debit card and PIN.
     * In a real system, this would involve complex secure verification
     * with the bank's central system.
     * For simplicity, we'll assume a dummy PIN and check if the card is linked to an account.
     * @param card The DebitCard being used.
     * @param pin The PIN entered by the user.
     * @return true if identification is successful, false otherwise.
     */
    public boolean identifies(DebitCard card, String pin) {
        // Simplified check: card must exist, be linked to an account, and PIN must be "1234"
        if (card != null && card.getLinkedAccount() != null && "1234".equals(pin)) {
            System.out.println("ATM identifies Debit Card " + card.getCardId() + " for Account " + card.getLinkedAccount().getAccountId() + ".");
            return true;
        }
        System.out.println("ATM failed to identify card or incorrect PIN.");
        return false;
    }

    /**
     * Method from diagram: 'Withdraws()'
     * Facilitates a withdrawal operation.
     * It interacts with ATMTransaction and the associated Account.
     * @param amount The amount to withdraw.
     * @param card The DebitCard used for the withdrawal.
     * @param pin The PIN entered.
     * @return true if withdrawal is successful, false otherwise.
     */
    public boolean withdraw(BigDecimal amount, DebitCard card, String pin) {
        if (!identifies(card, pin)) {
            return false;
        }

        Account account = card.getLinkedAccount();
        if (account == null) {
            System.out.println("No account linked to this debit card.");
            return false;
        }

        // Attempt to debit the account
        if (account.debit(amount)) {
            // Create a Withdrawal record (represents the specific details of a withdrawal action)
            Withdrawal withdrawalDetails = new Withdrawal(amount);

            // Create an ATMTransaction record for the overall event
            ATMTransaction atmTrans = new ATMTransaction(
                "W" + LocalDateTime.now().toString().replaceAll("[^0-9]", ""), // Generate unique ID
                LocalDateTime.now(),
                "Withdrawal"
            );
            // The ATM Transaction 'updates' the Account (logs it to account history)
            atmTrans.update(account);
            // Record this transaction within the ATM's internal logs
            this.recordTransaction(atmTrans);

            System.out.println("Withdrawal of " + amount + " from Account " + account.getAccountId() + " successful.");
            return true;
        }
        System.out.println("Withdrawal failed for Account " + account.getAccountId() + ". Insufficient funds or other error.");
        return false;
    }

    /**
     * Method related to 'Transfer' in the diagram.
     * Simulates a fund transfer between accounts.
     * @param amount The amount to transfer.
     * @param sourceCard The DebitCard used for the transfer (determines source account).
     * @param sourcePin The PIN for the source card.
     * @param targetAccountId The ID of the target account.
     * @return true if transfer is successful, false otherwise.
     */
    public boolean transfer(BigDecimal amount, DebitCard sourceCard, String sourcePin, String targetAccountId) {
        if (!identifies(sourceCard, sourcePin)) {
            return false;
        }

        Account sourceAccount = sourceCard.getLinkedAccount();
        if (sourceAccount == null) {
            System.out.println("No source account linked to this debit card.");
            return false;
        }

        // Retrieve the target account from the bank's registered accounts
        Account targetAccount = managedBy.getAccountById(targetAccountId);
        if (targetAccount == null) {
            System.out.println("Target account " + targetAccountId + " not found in this bank.");
            return false;
        }

        // Ensure source and target accounts are different
        if (sourceAccount.getAccountId().equals(targetAccount.getAccountId())) {
            System.out.println("Cannot transfer to the same account.");
            return false;
        }

        // Debit from source account and credit to target account
        if (sourceAccount.debit(amount)) {
            targetAccount.credit(amount);

            // Create a Transfer record
            Transfer transferDetails = new Transfer(amount, targetAccountId);

            // Create an ATMTransaction record for the overall event
            ATMTransaction atmTrans = new ATMTransaction(
                "T" + LocalDateTime.now().toString().replaceAll("[^0-9]", ""),
                LocalDateTime.now(),
                "Transfer"
            );
            atmTrans.update(sourceAccount); // Log for source account
            atmTrans.update(targetAccount); // Log for target account
            this.recordTransaction(atmTrans); // Record in ATM's logs

            System.out.println("Transfer of " + amount + " from Account " + sourceAccount.getAccountId() + " to " + targetAccount.getAccountId() + " successful.");
            return true;
        }
        System.out.println("Transfer failed. Insufficient funds in source account " + sourceAccount.getAccountId() + ".");
        return false;
    }
}

// =============================================================================
// 3. Customer Class
// Corresponds to the 'Customer' class in the diagram.
// Represents a bank customer.
// =============================================================================
class Customer {
    // Attributes from diagram:
    private String name;
    private String address;
    private LocalDate dob; // Using LocalDate for Date of Birth
    private String uid;    // Unique Identification Number

    // Associations:
    private List<DebitCard> debitCards; // One-to-many: Customer owns Debit Cards
    private List<Account> accounts;     // One-to-many: Customer owns Accounts

    /**
     * Constructor for the Customer class.
     * @param name Customer's full name.
     * @param address Customer's residential address.
     * @param dob Date of birth (e.g., LocalDate.of(1990, 1, 15)).
     * @param uid Unique identification number for the customer.
     */
    public Customer(String name, String address, LocalDate dob, String uid) {
        this.name = name;
        this.address = address;
        this.dob = dob;
        this.uid = uid;
        this.debitCards = new ArrayList<>();
        this.accounts = new ArrayList<>();
        System.out.println("Customer " + name + " (UID: " + uid + ") created.");
    }

    // Getters for attributes
    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    /**
     * Method from diagram: 'owns()' (implicitly for debit card)
     * Establishes the ownership relationship between a customer and a debit card.
     * @param debitCard The DebitCard object to associate.
     */
    public void ownsDebitCard(DebitCard debitCard) {
        if (!debitCards.contains(debitCard)) {
            debitCards.add(debitCard);
            debitCard.setOwner(this); // Set the owner in the debit card object as well
            System.out.println(this.name + " now owns Debit Card " + debitCard.getCardId());
        }
    }

    /**
     * Method from diagram: 'owns()' (implicitly for account)
     * Establishes the ownership relationship between a customer and an account.
     * @param account The Account object to associate.
     */
    public void ownsAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            account.setOwner(this); // Set the owner in the account object as well
            System.out.println(this.name + " now owns Account " + account.getAccountId());
        }
    }
}

// =============================================================================
// 4. Debit Card Class
// Corresponds to the 'Debit Card' class in the diagram.
// Represents a debit card issued to a customer.
// =============================================================================
class DebitCard {
    // Attributes from diagram:
    private String cardId;
    // Association: Debit Card owned by Customer (one-to-one relationship)
    private Customer owner; // 'own_by' in diagram
    // Association: Debit Card provides access to Account (one-to-one relationship)
    private Account linkedAccount; // 'access_to' in diagram (could be a method too)

    /**
     * Constructor for the DebitCard class.
     * @param cardId Unique identifier for the debit card.
     * @param owner The Customer object who owns this card (can be null initially).
     */
    public DebitCard(String cardId, Customer owner) {
        this.cardId = cardId;
        this.owner = owner;
        System.out.println("Debit Card " + cardId + " created.");
        if (owner != null) {
            owner.ownsDebitCard(this); // Ensure the customer also knows they own this card
        }
    }

    // Getters and Setters
    public String getCardId() {
        return cardId;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Account getLinkedAccount() {
        return linkedAccount;
    }

    /**
     * Method representing 'provides access to' from the diagram's relationship.
     * Links this debit card to a specific bank account.
     * @param account The Account object this card will access.
     */
    public void providesAccessTo(Account account) {
        this.linkedAccount = account;
        System.out.println("Debit Card " + this.cardId + " now provides access to Account " + account.getAccountId());
    }
}

// =============================================================================
// 5. Account Class (Superclass/Abstract Class)
// Corresponds to the 'Account' class in the diagram.
// This is an abstract base class for different types of accounts.
// =============================================================================
abstract class Account { // Made abstract because a generic 'Account' might not be instantiated directly
    // Attributes from diagram:
    private String accountId;
    private String accountType; // 'type' in diagram
    // Association: Account owned by Customer (one-to-one relationship)
    private Customer owner; // 'Owner' in diagram
    // Using private attribute for balance, managed by public methods (encapsulation)
    private BigDecimal balance;

    // To store transaction history for this account (implicit in "updates" relationship)
    private List<ATMTransaction> transactionHistory;

    /**
     * Constructor for the Account class.
     * @param accountId Unique identifier for the account.
     * @param accountType Type of account (e.g., "Savings", "Current").
     * @param owner The Customer object who owns this account (can be null initially).
     */
    public Account(String accountId, String accountType, Customer owner) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.owner = owner;
        this.balance = BigDecimal.ZERO; // Initialize balance to zero using BigDecimal
        this.transactionHistory = new ArrayList<>();
        System.out.println(accountType + " Account " + accountId + " created.");
        if (owner != null) {
            owner.ownsAccount(this); // Ensure the customer also knows they own this account
        }
    }

    // Getters and Setters for attributes
    public String getAccountId() {
        return accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    /**
     * Method from diagram: 'check_balance()'
     * Returns the current balance of the account.
     * @return The current balance as a BigDecimal.
     */
    public BigDecimal checkBalance() {
        return balance;
    }

    /**
     * Method implicitly defined by 'debits and credits' relationship.
     * Adds funds to the account.
     * @param amount The amount to credit.
     * @return true if credit is successful, false otherwise.
     */
    public boolean credit(BigDecimal amount) {
        // Ensure amount is positive and not null
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Credit failed for Account " + accountId + ": Invalid amount " + amount + ".");
            return false;
        }
        balance = balance.add(amount);
        System.out.println("Account " + accountId + " credited " + amount + ". New balance: " + balance);
        return true;
    }

    /**
     * Method implicitly defined by 'debits and credits' relationship.
     * Removes funds from the account. This is a generic debit,
     * subclasses will implement specific logic (e.g., overdraft).
     * @param amount The amount to debit.
     * @return true if debit is successful, false otherwise.
     */
    public abstract boolean debit(BigDecimal amount); // Made abstract, implemented by subclasses

    /**
     * Adds an ATMTransaction to this account's transaction history.
     * @param transaction The ATMTransaction to add.
     */
    public void addTransactionToHistory(ATMTransaction transaction) {
        this.transactionHistory.add(transaction);
        System.out.println("Transaction " + transaction.getTransactionId() + " logged for Account " + this.accountId + ".");
    }

    public List<ATMTransaction> getTransactionHistory() {
        return transactionHistory;
    }
}

// =============================================================================
// 6. Savings Account Class (Subclass of Account)
// Corresponds to the 'Savings Account' class in the diagram.
// Represents a savings account, inheriting from the base Account class.
// =============================================================================
class SavingsAccount extends Account {
    // Specific attribute for Savings Account
    private BigDecimal interestRate;

    /**
     * Constructor for the SavingsAccount class.
     * @param accountId Unique identifier for the savings account.
     * @param owner The Customer object who owns this account.
     * @param interestRate The annual interest rate for the savings account.
     */
    public SavingsAccount(String accountId, Customer owner, BigDecimal interestRate) {
        // Call the constructor of the parent class (Account)
        super(accountId, "Savings", owner);
        this.interestRate = interestRate;
        System.out.println("Savings Account " + accountId + " created with interest rate " + interestRate.multiply(new BigDecimal("100")) + "%.");
    }

    // Getter for interest rate
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    /**
     * Implements the abstract debit method from the Account class.
     * Savings accounts typically do not allow overdrafts.
     * @param amount The amount to debit.
     * @return true if debit is successful, false otherwise.
     */
    @Override // Good practice to use @Override annotation
    public boolean debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Debit failed for Savings Account " + getAccountId() + ": Invalid amount " + amount + ".");
            return false;
        }
        // Check if there are sufficient funds without allowing overdraft
        if (checkBalance().compareTo(amount) >= 0) {
            // Call the parent class's credit method to reduce the balance
            super.credit(amount.negate()); // Subtract by adding a negative amount
            System.out.println("Savings Account " + getAccountId() + " debited " + amount + ". New balance: " + checkBalance());
            return true;
        }
        System.out.println("Debit failed for Savings Account " + getAccountId() + ": Insufficient funds. Current balance: " + checkBalance());
        return false;
    }

    /**
     * Specific method for Savings Account: calculates and applies interest.
     */
    public void calculateAndApplyInterest() {
        BigDecimal interest = checkBalance().multiply(interestRate);
        credit(interest); // Use the existing credit method
        System.out.println("Interest of " + interest + " applied to Savings Account " + getAccountId() + ".");
    }
}

// =============================================================================
// 7. Current Account Class (Subclass of Account)
// Corresponds to the 'Current Account' class in the diagram.
// Represents a current account, often with overdraft facilities.
// =============================================================================
class CurrentAccount extends Account {
    // Specific attribute for Current Account
    private BigDecimal overdraftLimit;

    /**
     * Constructor for the CurrentAccount class.
     * @param accountId Unique identifier for the current account.
     * @param owner The Customer object who owns this account.
     * @param overdraftLimit The maximum amount allowed to be overdrawn.
     */
    public CurrentAccount(String accountId, Customer owner, BigDecimal overdraftLimit) {
        // Call the constructor of the parent class (Account)
        super(accountId, "Current", owner);
        this.overdraftLimit = overdraftLimit;
        System.out.println("Current Account " + accountId + " created with overdraft limit " + overdraftLimit + ".");
    }

    // Getter for overdraft limit
    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Implements the abstract debit method from the Account class.
     * Overrides the debit method to allow for overdrafts up to the limit.
     * @param amount The amount to debit.
     * @return true if debit is successful, false otherwise.
     */
    @Override // Good practice to use @Override annotation
    public boolean debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Debit failed for Current Account " + getAccountId() + ": Invalid amount " + amount + ".");
            return false;
        }
        // Allow debit if balance plus overdraft limit covers the amount
        // currentBalance + overdraftLimit >= amount
        if (checkBalance().add(overdraftLimit).compareTo(amount) >= 0) {
            super.credit(amount.negate()); // Subtract by adding a negative amount
            System.out.println("Current Account " + getAccountId() + " debited " + amount + ". New balance: " + checkBalance());
            return true;
        }
        System.out.println("Debit failed for Current Account " + getAccountId() + ": Exceeds overdraft limit. Current balance: " + checkBalance() + ", Overdraft Limit: " + overdraftLimit);
        return false;
    }
}

// =============================================================================
// 8. ATM Transactions Class
// Corresponds to the 'ATM Transactions' class in the diagram.
// Represents a generic record of an ATM transaction.
// =============================================================================
class ATMTransaction {
    // Attributes from diagram:
    private String transactionId;
    private LocalDateTime date; // Using LocalDateTime for date and time of transaction
    private String type;        // e.g., "Withdrawal", "Deposit", "Transfer"

    /**
     * Constructor for the ATMTransaction class.
     * @param transactionId Unique identifier for the transaction.
     * @param date Date and time of the transaction.
     * @param type Type of transaction.
     */
    public ATMTransaction(String transactionId, LocalDateTime date, String type) {
        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        System.out.println("ATM Transaction " + transactionId + " (" + type + ") created at " + date + ".");
    }

    // Getters for attributes
    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    /**
     * Method from diagram: 'Update()'
     * This method would typically persist the transaction to a database or
     * add it to the relevant account's history.
     * @param account The Account object whose history needs to be updated.
     */
    public void update(Account account) {
        // In a real system, this would involve database operations.
        // For this demo, we'll add it to the account's in-memory history.
        account.addTransactionToHistory(this);
        System.out.println("Transaction " + this.transactionId + " of type '" + this.type + "' updated for Account " + account.getAccountId() + ".");
    }
}

// =============================================================================
// 9. Withdrawal Class
// Corresponds to the 'Withdrawal' class in the diagram.
// This appears to be a specific type of transaction details or record.
// In a more formal UML, it might be a subclass of ATMTransaction or a detail object.
// Here, we treat it as a data container for withdrawal specifics.
// =============================================================================
class Withdrawal {
    // Attribute from diagram:
    private BigDecimal amount;

    /**
     * Constructor for the Withdrawal class.
     * @param amount The amount of money withdrawn.
     */
    public Withdrawal(BigDecimal amount) {
        this.amount = amount;
        System.out.println("Withdrawal record created for amount: " + amount);
    }

    // Getter
    public BigDecimal getAmount() {
        return amount;
    }
}

// =============================================================================
// 10. Transfer Class
// Corresponds to the 'Transfer' class in the diagram.
// Similar to Withdrawal, this seems to represent specific transfer details.
// =============================================================================
class Transfer {
    // Attributes from diagram:
    private BigDecimal amount;
    private String targetAccountId; // 'Account_id' in diagram for target

    /**
     * Constructor for the Transfer class.
     * @param amount The amount of money transferred.
     * @param targetAccountId The ID of the account receiving the transfer.
     */
    public Transfer(BigDecimal amount, String targetAccountId) {
        this.amount = amount;
        this.targetAccountId = targetAccountId;
        System.out.println("Transfer record created for amount: " + amount + " to account: " + targetAccountId);
    }

    // Getters
    public BigDecimal getAmount() {
        return amount;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }
}

// =============================================================================
// 11. Pin Generation Class
// Corresponds to the 'Pin Generation' class in the diagram.
// Responsible for generating PINs.
// =============================================================================
class PinGeneration {
    // Attributes from diagram:
    private String accountTypeNewOrOld; // 'Account_type(new or old)'
    private String generatedPin;        // 'pin_generated'

    private Random random; // For generating random PINs

    /**
     * Constructor for the PinGeneration class.
     * @param accountTypeNewOrOld Indicates if it's for a new account or a reset for an old one.
     */
    public PinGeneration(String accountTypeNewOrOld) {
        this.accountTypeNewOrOld = accountTypeNewOrOld;
        this.random = new Random();
        System.out.println("PIN Generation service initialized for " + accountTypeNewOrOld + " accounts.");
    }

    // Getters
    public String getAccountTypeNewOrOld() {
        return accountTypeNewOrOld;
    }

    public String getGeneratedPin() {
        return generatedPin;
    }

    /**
     * Method from diagram: 'pin_generata()' (corrected to generatePin)
     * Generates a new PIN for a given account.
     * In a real system, this would securely generate, encrypt, and store the PIN,
     * often not directly returning it or printing it to console for security reasons.
     * @param account The Account for which to generate the PIN.
     * @return The newly generated PIN (for demo purposes).
     */
    public String generatePin(Account account) {
        // Generate a 4-digit random PIN
        // In a real system, this would be more complex (e.g., strong random, hashing)
        int pinInt = random.nextInt(9000) + 1000; // Generates number between 1000 and 9999
        this.generatedPin = String.valueOf(pinInt);

        // This would update the account's stored PIN securely in a real system.
        // For demo, we just print it.
        System.out.println("PIN generated for Account " + account.getAccountId() + ": " + this.generatedPin + " (Note: In real system, this wouldn't be printed/returned directly for security)");
        return this.generatedPin;
    }
}


// =============================================================================
// Main Class for Demonstration
// This class will set up objects and simulate interactions based on the diagram.
// =============================================================================
public class ATMSystemDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting ATM System Demonstration ---");

        // 1. Create instances of Bank, Customer, ATM
        Bank myBank = new Bank("GLOBALBANK", "123 Main Street, Cityville");
        Customer alice = new Customer("Alice Smith", "456 Oak Ave", LocalDate.of(1990, 5, 10), "CUST1001");
        Customer bob = new Customer("Bob Johnson", "789 Pine Lane", LocalDate.of(1985, 11, 22), "CUST1002");
        ATM branchATM = new ATM("Downtown Branch", myBank);

        // 2. Establish Bank-ATM relationship (Bank maintains ATM)
        myBank.maintains(branchATM);

        // 3. Create Accounts for Customers
        // Alice gets a Savings Account
        SavingsAccount aliceSavings = new SavingsAccount("S001-ALICE", alice, new BigDecimal("0.015")); // 1.5% interest
        myBank.addAccount(aliceSavings); // Register account with the bank
        // Alice gets a Current Account
        CurrentAccount aliceCurrent = new CurrentAccount("C002-ALICE", alice, new BigDecimal("500.00")); // $500 overdraft
        myBank.addAccount(aliceCurrent); // Register account with the bank

        // Bob gets a Savings Account
        SavingsAccount bobSavings = new SavingsAccount("S003-BOB", bob, new BigDecimal("0.012")); // 1.2% interest
        myBank.addAccount(bobSavings); // Register account with the bank

        // 4. Create Debit Cards and link them
        DebitCard aliceCard = new DebitCard("DC001-ALICE", alice);
        aliceCard.providesAccessTo(aliceSavings); // Alice's card links to her savings

        DebitCard bobCard = new DebitCard("DC002-BOB", bob);
        bobCard.providesAccessTo(bobSavings); // Bob's card links to his savings

        // 5. Initial deposits to accounts
        System.out.println("\n--- Initial Deposits ---");
        aliceSavings.credit(new BigDecimal("1000.00"));
        aliceCurrent.credit(new BigDecimal("500.00"));
        bobSavings.credit(new BigDecimal("2000.00"));

        System.out.println("\n--- Current Balances ---");
        System.out.println("Alice's Savings: $" + aliceSavings.checkBalance());
        System.out.println("Alice's Current: $" + aliceCurrent.checkBalance());
        System.out.println("Bob's Savings:   $" + bobSavings.checkBalance());

        // 6. Simulate ATM Operations
        System.out.println("\n--- ATM Operations ---");

        // Alice attempts withdrawal from Savings
        System.out.println("\n>>> Alice withdraws $300 from Savings (correct PIN)");
        branchATM.withdraw(new BigDecimal("300.00"), aliceCard, "1234");
        System.out.println("Alice's Savings (after withdrawal): $" + aliceSavings.checkBalance());

        // Alice attempts withdrawal from Savings with wrong PIN
        System.out.println("\n>>> Alice tries to withdraw $100 from Savings (wrong PIN)");
        branchATM.withdraw(new BigDecimal("100.00"), aliceCard, "9999"); // Wrong PIN

        // Alice attempts withdrawal from Current (testing overdraft)
        System.out.println("\n>>> Alice withdraws $700 from Current (with $500 overdraft)");
        branchATM.withdraw(new BigDecimal("700.00"), aliceCard, "1234"); // Should succeed due to overdraft
        System.out.println("Alice's Current (after withdrawal): $" + aliceCurrent.checkBalance());

        // Alice attempts transfer from Savings to Bob's Savings
        System.out.println("\n>>> Alice transfers $250 from Savings to Bob's Savings");
        // For transfer, Alice's card accesses her Savings (source), and Bob's Savings (target) is identified by ID.
        branchATM.transfer(new BigDecimal("250.00"), aliceCard, "1234", bobSavings.getAccountId());
        System.out.println("Alice's Savings (after transfer): $" + aliceSavings.checkBalance());
        System.out.println("Bob's Savings (after transfer):   $" + bobSavings.checkBalance());

        // Attempt transfer to a non-existent account
        System.out.println("\n>>> Alice tries to transfer $50 to a non-existent account");
        branchATM.transfer(new BigDecimal("50.00"), aliceCard, "1234", "NONEXISTENT");

        // Test insufficient funds in Savings
        System.out.println("\n>>> Alice tries to withdraw $1000 from Savings (insufficient funds)");
        branchATM.withdraw(new BigDecimal("1000.00"), aliceCard, "1234");
        System.out.println("Alice's Savings (after failed withdrawal): $" + aliceSavings.checkBalance());


        // 7. PIN Generation
        System.out.println("\n--- PIN Generation ---");
        PinGeneration pinService = new PinGeneration("new account");
        String newPin = pinService.generatePin(aliceCurrent);
        System.out.println("Generated PIN for Alice's Current Account (for demo): " + newPin);

        // 8. Account History
        System.out.println("\n--- Account Transaction History ---");
        System.out.println("\nAlice's Savings Account History:");
        for (ATMTransaction t : aliceSavings.getTransactionHistory()) {
            System.out.println("  - ID: " + t.getTransactionId() + ", Type: " + t.getType() + ", Date: " + t.getDate());
        }

        System.out.println("\nAlice's Current Account History:");
        for (ATMTransaction t : aliceCurrent.getTransactionHistory()) {
            System.out.println("  - ID: " + t.getTransactionId() + ", Type: " + t.getType() + ", Date: " + t.getDate());
        }

        System.out.println("\nBob's Savings Account History:");
        for (ATMTransaction t : bobSavings.getTransactionHistory()) {
            System.out.println("  - ID: " + t.getTransactionId() + ", Type: " + t.getType() + ", Date: " + t.getDate());
        }

        System.out.println("\n--- Final Balances ---");
        System.out.println("Alice's Savings: $" + aliceSavings.checkBalance());
        System.out.println("Alice's Current: $" + aliceCurrent.checkBalance());
        System.out.println("Bob's Savings:   $" + bobSavings.checkBalance());

        System.out.println("\n--- ATM System Demonstration Complete ---");
    }
}