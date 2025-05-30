import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // For potential use in equals/hashCode if needed

public class BankingSystemDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting Banking System Demonstration ---");

        // 1. Create a Bank
        Bank myBank = new Bank("BK001", "MyBank Corp", "123 Main St, Cityville");
        System.out.println("\n");

        // 2. Create Customers
        Customer alice = new Customer("C001", "Alice Smith", "456 Oak Ave", "555-1111", "alice@example.com");
        Customer bob = new Customer("C002", "Bob Johnson", "789 Pine Ln", "555-2222", "bob@example.com");
        System.out.println("\n");

        // Add customers to the bank
        myBank.addCustomer(alice);
        myBank.addCustomer(bob);
        System.out.println("\n");

        // 3. Create Accounts
        SavingsAccount aliceSavings = new SavingsAccount("SA001", 1000.0, 1.5);
        CheckingAccount aliceChecking = new CheckingAccount("CA001", 500.0, 200.0);
        SavingsAccount bobSavings = new SavingsAccount("SA002", 2500.0, 1.8);
        System.out.println("\n");

        // Link accounts to customers
        alice.addAccount(aliceSavings);
        alice.addAccount(aliceChecking);
        bob.addAccount(bobSavings);
        System.out.println("\n");

        // Register accounts with the bank
        myBank.addAccount(aliceSavings);
        myBank.addAccount(aliceChecking);
        myBank.addAccount(bobSavings);
        System.out.println("\n");

        // Display initial info
        myBank.displayBankInfo();
        alice.displayCustomerInfo();
        bob.displayCustomerInfo();

        // 4. Create an ATM and link it to the bank
        ATM cityATM = new ATM("ATM001", "Downtown Plaza", 10000.0, myBank);
        System.out.println("\n");

        // 5. Simulate ATM operations for Alice
        System.out.println("--- Alice using ATM ---");
        cityATM.checkBalance(aliceChecking);
        cityATM.dispenseCash(aliceChecking, 150.0);
        cityATM.checkBalance(aliceChecking);
        cityATM.acceptDeposit(aliceSavings, 200.0);
        cityATM.checkBalance(aliceSavings);
        System.out.println("\n");

        // Simulate a transfer
        System.out.println("--- Alice transferring funds ---");
        cityATM.transferFunds(aliceChecking, aliceSavings, 100.0);
        cityATM.checkBalance(aliceChecking);
        cityATM.checkBalance(aliceSavings);
        System.out.println("\n");

        // Simulate Bob using ATM
        System.out.println("--- Bob using ATM ---");
        cityATM.checkBalance(bobSavings);
        cityATM.dispenseCash(bobSavings, 3000.0); // Should fail due to insufficient funds
        cityATM.dispenseCash(bobSavings, 500.0); // Should succeed
        cityATM.checkBalance(bobSavings);
        System.out.println("\n");

        // Apply interest for Savings Account
        System.out.println("--- Applying Interest ---");
        aliceSavings.applyInterest();
        bobSavings.applyInterest();
        System.out.println("\n");

        // Display final balances
        System.out.println("--- Final Account Balances ---");
        alice.getAccounts().forEach(acc -> System.out.println(acc.getAccountNumber() + ": " + acc.getBalance()));
        bob.getAccounts().forEach(acc -> System.out.println(acc.getAccountNumber() + ": " + acc.getBalance()));
        System.out.println("------------------------------");

        // Display transactions for Alice's accounts
        System.out.println("\n--- Alice's Checking Account Transactions ---");
        List<Transaction> aliceCheckingTransactions = aliceChecking.getTransactions();
        aliceCheckingTransactions.forEach(System.out::println);

        System.out.println("\n--- Alice's Savings Account Transactions ---");
        List<Transaction> aliceSavingsTransactions = aliceSavings.getTransactions();
        aliceSavingsTransactions.forEach(System.out::println);

        System.out.println("\n--- Banking System Demonstration Complete ---");
    }
}

// Enum: TransactionType
enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER,
    FEE,
    INTEREST
}

// Class: Transaction
class Transaction {
    private String transactionId;
    private double amount;
    private TransactionType type;
    private LocalDateTime date;
    private String description;

    public Transaction(String transactionId, double amount, TransactionType type, String description) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.date = LocalDateTime.now(); // Set current time for transaction
        this.description = description;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f on %s (%s)",
                             transactionId, type, amount, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), description);
    }
}

// Abstract Class: Account
abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected LocalDate dateOpened;
    protected String status; // e.g., "Active", "Closed", "Frozen"
    protected List<Transaction> transactions; // Association with Transaction

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.dateOpened = LocalDate.now();
        this.status = "Active";
        this.transactions = new ArrayList<>();
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public String getStatus() {
        return status;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions); // Return a copy to prevent external modification
    }

    // Setters (if needed, but usually controlled via methods like deposit/withdraw)
    public void setStatus(String status) {
        this.status = status;
    }

    // Abstract methods to be implemented by subclasses
    public abstract void deposit(double amount);
    public abstract void withdraw(double amount);

    // Common method for all accounts
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return String.format("Account #%s (Type: %s, Balance: %.2f, Status: %s)",
                             accountNumber, this.getClass().getSimpleName(), balance, status);
    }
}

// Class: SavingsAccount
class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, initialBalance);
        this.interestRate = interestRate;
        System.out.println("Savings Account " + accountNumber + " created.");
    }

    // Getter
    public double getInterestRate() {
        return interestRate;
    }

    // Setter (if interest rate can change)
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.DEPOSIT, "Savings deposit"));
            System.out.println("Deposited " + amount + " to Savings Account " + accountNumber + ". New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.WITHDRAWAL, "Savings withdrawal"));
            System.out.println("Withdrew " + amount + " from Savings Account " + accountNumber + ". New balance: " + balance);
        } else if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
        } else {
            System.out.println("Insufficient funds in Savings Account " + accountNumber + ".");
        }
    }

    public void applyInterest() {
        double interest = this.balance * (this.interestRate / 100);
        this.balance += interest;
        addTransaction(new Transaction("TRN" + System.nanoTime(), interest, TransactionType.INTEREST, "Interest applied"));
        System.out.println("Interest of " + interest + " applied to Savings Account " + accountNumber + ". New balance: " + balance);
    }
}

// Class: CheckingAccount
class CheckingAccount extends Account {
    private double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, initialBalance);
        this.overdraftLimit = overdraftLimit;
        System.out.println("Checking Account " + accountNumber + " created.");
    }

    // Getter
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    // Setter (if overdraft limit can change)
    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.DEPOSIT, "Checking deposit"));
            System.out.println("Deposited " + amount + " to Checking Account " + accountNumber + ". New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0) {
            if (this.balance + this.overdraftLimit >= amount) {
                this.balance -= amount;
                addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.WITHDRAWAL, "Checking withdrawal"));
                System.out.println("Withdrew " + amount + " from Checking Account " + accountNumber + ". New balance: " + balance);
                if (this.balance < 0) {
                    processOverdraft();
                }
            } else {
                System.out.println("Withdrawal exceeds overdraft limit in Checking Account " + accountNumber + ".");
            }
        } else {
            System.out.println("Withdrawal amount must be positive.");
        }
    }

    public void processOverdraft() {
        if (this.balance < 0) {
            System.out.println("ALERT: Checking Account " + accountNumber + " is in overdraft. Current balance: " + balance);
            // In a real system, a fee might be applied here.
            // addTransaction(new Transaction("TRN" + System.nanoTime(), overdraftFee, TransactionType.FEE, "Overdraft fee"));
        }
    }
}

// Class: Customer
class Customer {
    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private List<Account> accounts; // Association with Account

    public Customer(String customerId, String name, String address, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.accounts = new ArrayList<>();
        System.out.println("Customer " + name + " (ID: " + customerId + ") created.");
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // Return a copy
    }

    // Methods for managing accounts
    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            System.out.println("Account " + account.getAccountNumber() + " added to customer " + name);
        } else {
            System.out.println("Account " + account.getAccountNumber() + " already exists for customer " + name);
        }
    }

    public Account getAccountByNumber(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null; // Account not found for this customer
    }

    public void displayCustomerInfo() {
        System.out.println("\n--- Customer Info ---");
        System.out.println("ID: " + customerId);
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Phone: " + phone);
        System.out.println("Email: " + email);
        System.out.println("Accounts:");
        if (accounts.isEmpty()) {
            System.out.println("  No accounts linked.");
        } else {
            for (Account acc : accounts) {
                System.out.println("  - " + acc);
            }
        }
        System.out.println("---------------------");
    }
}

// Class: Bank
class Bank {
    private String bankId;
    private String name;
    private String address;
    private List<Customer> customers; // Association with Customer
    private List<Account> accounts;   // Association with Account
    private List<ATM> atms;           // Association with ATM

    public Bank(String bankId, String name, String address) {
        this.bankId = bankId;
        this.name = name;
        this.address = address;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.atms = new ArrayList<>();
        System.out.println("Bank '" + name + "' (ID: " + bankId + ") created.");
    }

    // Getters
    public String getBankId() {
        return bankId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public List<ATM> getAtms() {
        return new ArrayList<>(atms);
    }

    // Methods for managing customers and accounts
    public void addCustomer(Customer customer) {
        if (!customers.contains(customer)) {
            customers.add(customer);
            System.out.println("Customer " + customer.getName() + " added to " + name);
        }
    }

    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            System.out.println("Account " + account.getAccountNumber() + " registered with " + name);
        }
    }

    public void addATM(ATM atm) {
        if (!atms.contains(atm)) {
            atms.add(atm);
            System.out.println("ATM " + atm.getAtmId() + " added to " + name + " network.");
        }
    }

    public Account findAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        return null; // Account not found in this bank
    }

    public void displayBankInfo() {
        System.out.println("\n--- Bank Info: " + name + " ---");
        System.out.println("ID: " + bankId);
        System.out.println("Address: " + address);
        System.out.println("Total Customers: " + customers.size());
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Total ATMs: " + atms.size());
        System.out.println("-------------------------");
    }
}

// Class: ATM
class ATM {
    private String atmId;
    private String location;
    private double cashOnHand;
    private Bank bank; // Association with Bank (an ATM belongs to one bank)

    public ATM(String atmId, String location, double cashOnHand, Bank bank) {
        this.atmId = atmId;
        this.location = location;
        this.cashOnHand = cashOnHand;
        this.bank = bank; // Link to the bank this ATM belongs to
        bank.addATM(this); // Add this ATM to the bank's list of ATMs
        System.out.println("ATM " + atmId + " at " + location + " initialized with " + cashOnHand + " cash.");
    }

    // Getters
    public String getAtmId() {
        return atmId;
    }

    public String getLocation() {
        return location;
    }

    public double getCashOnHand() {
        return cashOnHand;
    }

    public Bank getBank() {
        return bank;
    }

    // ATM Operations (these would typically interact with the bank's central system)
    public void dispenseCash(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("ATM " + atmId + ": Invalid withdrawal amount.");
            return;
        }
        if (cashOnHand >= amount) {
            // In a real system, communicate with the bank to process the withdrawal
            if (account != null) {
                double initialBalance = account.getBalance();
                account.withdraw(amount); // Attempt withdrawal from account
                if (account.getBalance() < initialBalance) { // If withdrawal was successful
                    this.cashOnHand -= amount;
                    System.out.println("ATM " + atmId + ": Dispensed " + amount + ". Remaining cash: " + cashOnHand);
                } else {
                    System.out.println("ATM " + atmId + ": Account withdrawal failed or insufficient funds.");
                }
            } else {
                System.out.println("ATM " + atmId + ": Account not found.");
            }
        } else {
            System.out.println("ATM " + atmId + ": Insufficient cash in ATM to dispense " + amount + ".");
        }
    }

    public void acceptDeposit(Account account, double amount) {
        if (amount <= 0) {
            System.out.println("ATM " + atmId + ": Invalid deposit amount.");
            return;
        }
        if (account != null) {
            account.deposit(amount); // Deposit to account
            // In a real system, the ATM would store the cash and update its internal count
            // this.cashOnHand += amount; // Assuming ATM takes physical cash
            System.out.println("ATM " + atmId + ": Accepted deposit of " + amount + " for account " + account.getAccountNumber());
        } else {
            System.out.println("ATM " + atmId + ": Account not found for deposit.");
        }
    }

    public void checkBalance(Account account) {
        if (account != null) {
            System.out.println("ATM " + atmId + ": Account " + account.getAccountNumber() + " balance: " + account.getBalance());
        } else {
            System.out.println("ATM " + atmId + ": Account not found for balance inquiry.");
        }
    }

    public void transferFunds(Account fromAccount, Account toAccount, double amount) {
        if (amount <= 0) {
            System.out.println("ATM " + atmId + ": Invalid transfer amount.");
            return;
        }
        if (fromAccount == null || toAccount == null) {
            System.out.println("ATM " + atmId + ": Source or destination account not found.");
            return;
        }

        if (fromAccount.getBalance() >= amount) {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            fromAccount.addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.TRANSFER, "Transfer to " + toAccount.getAccountNumber()));
            toAccount.addTransaction(new Transaction("TRN" + System.nanoTime(), amount, TransactionType.TRANSFER, "Transfer from " + fromAccount.getAccountNumber()));
            System.out.println("ATM " + atmId + ": Transferred " + amount + " from " + fromAccount.getAccountNumber() + " to " + toAccount.getAccountNumber());
        } else {
            System.out.println("ATM " + atmId + ": Insufficient funds for transfer from " + fromAccount.getAccountNumber());
        }
    }
}