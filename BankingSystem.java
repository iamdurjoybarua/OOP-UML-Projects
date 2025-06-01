import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // For generating unique IDs

// Bank.java
class Bank {
    private String name;
    private String location;
    private List<Account> accounts; // 1-to-many relationship with Account

    public Bank(String name, String location) {
        this.name = name;
        this.location = location;
        this.accounts = new ArrayList<>();
    }

    // Getters
    public String getName() { return name; }
    public String getLocation() { return location; }
    public List<Account> getAccounts() { return accounts; }

    // Operations
    public Account openAccount(Customer customer, String accountType, double initialDeposit) {
        // Generate a unique account number
        String accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8);
        Account newAccount = new Account(accountNumber, accountType, initialDeposit);
        this.accounts.add(newAccount);
        customer.addAccount(newAccount); // Link account to customer
        System.out.println(customer.getName() + " opened a new " + accountType + " account: " + accountNumber + " with initial deposit: $" + String.format("%.2f", initialDeposit));
        return newAccount;
    }

    public void closeAccount(Account account) {
        if (this.accounts.remove(account)) {
            // Remove account from its associated customer as well
            if (account.getCustomer() != null) {
                account.getCustomer().removeAccount(account);
            }
            System.out.println("Account " + account.getAccountNumber() + " closed.");
        } else {
            System.out.println("Account " + account.getAccountNumber() + " not found in " + this.name + " bank.");
        }
    }

    public Account getAccount(String accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accountNumber)) {
                return acc;
            }
        }
        System.out.println("Account " + accountNumber + " not found.");
        return null;
    }

    // Method to process a transaction that involves the bank's accounts
    public boolean processTransaction(Transaction transaction) {
        System.out.println("Bank processing transaction: " + transaction.getTransactionId() + " (" + transaction.getTransactionType() + ")");
        // The Transaction's execute() method will now correctly use the Account's public methods.
        if (transaction.execute()) {
            System.out.println("Transaction " + transaction.getTransactionId() + " processed successfully by bank.");
            return true;
        } else {
            System.out.println("Transaction " + transaction.getTransactionId() + " failed during bank processing.");
            return false;
        }
    }
}

// Customer.java
class Customer {
    private String name;
    private String customerId;
    private String address;
    private List<Account> accounts; // 1-to-many relationship with Account
    private List<Loan> loans; // 0-to-many relationship with Loan

    public Customer(String name, String customerId, String address) {
        this.name = name;
        this.customerId = customerId;
        this.address = address;
        this.accounts = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    // Getters
    public String getName() { return name; }
    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public List<Account> getAccounts() { return accounts; }
    public List<Loan> getLoans() { return loans; }

    // Setters for relationships
    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setCustomer(this); // Link back to customer
    }

    public void removeAccount(Account account) {
        if (this.accounts.remove(account)) {
            account.setCustomer(null); // Unlink
            System.out.println("Account " + account.getAccountNumber() + " removed from " + this.name + "'s profile.");
        }
    }

    public void addLoan(Loan loan) {
        this.loans.add(loan);
        loan.setCustomer(this); // Link back to customer
    }

    // Operations
    public Loan requestLoan(double amount) {
        String loanId = "LOAN-" + UUID.randomUUID().toString().substring(0, 8);
        // Interest rate is usually determined by bank policy, not set by customer directly in request
        Loan newLoan = new Loan(loanId, amount, 0.05); // Example interest rate
        this.loans.add(newLoan); // Add to customer's loans
        System.out.println(this.name + " requested a loan of $" + String.format("%.2f", amount));
        return newLoan;
    }

    public boolean transferFunds(Account fromAccount, Account toAccount, double amount) {
        System.out.println(this.name + " attempting to transfer $" + String.format("%.2f", amount) +
                           " from " + fromAccount.getAccountNumber() + " to " + toAccount.getAccountNumber());
        if (fromAccount.getBalance() >= amount) {
            // Use Account's public methods for withdrawal and deposit
            if (fromAccount.withdraw(amount)) { // This will also record the transaction
                toAccount.deposit(amount);      // This will also record the transaction
                System.out.println("Transfer successful!");
                return true;
            } else {
                System.out.println("Transfer failed during withdrawal from source account.");
                return false;
            }
        } else {
            System.out.println("Insufficient funds in " + fromAccount.getAccountNumber() + " for transfer.");
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}

// Account.java
class Account {
    private String accountNumber;
    private double balance;
    private String accountType; // e.g., "Savings", "Checking", "Loan"
    private Customer customer; // 1-to-1 relationship with Customer (owns) - added for bi-directional navigation
    private List<Transaction> transactions; // 0-to-many relationship with Transaction (records)

    public Account(String accountNumber, String accountType, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public Customer getCustomer() { return customer; }
    public List<Transaction> getTransactions() { return transactions; }

    // Setter for Customer (to link it)
    public void setCustomer(Customer customer) { this.customer = customer; }

    // Operations
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            Transaction depositTxn = new Transaction(transactionId, amount, "Deposit", this);
            this.transactions.add(depositTxn);
            System.out.println("Deposited $" + String.format("%.2f", amount) + " to account " + accountNumber + ". New balance: $" + String.format("%.2f", balance));
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return false;
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            Transaction withdrawalTxn = new Transaction(transactionId, amount, "Withdrawal", this);
            this.transactions.add(withdrawalTxn);
            System.out.println("Withdrew $" + String.format("%.2f", amount) + " from account " + accountNumber + ". New balance: $" + String.format("%.2f", balance));
            return true;
        } else {
            System.out.println("Insufficient funds in account " + accountNumber + ". Current balance: $" + String.format("%.2f", balance));
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}

// ATM.java
class ATM {
    private String location;
    // An ATM 'owns' a Bank and 'processes' Transactions implicitly via its interactions
    // In a real system, ATM would interface with a central Bank system.
    private Bank bank; // Link to the bank it belongs to/interfaces with

    public ATM(String location, Bank bank) {
        this.location = location;
        this.bank = bank;
    }

    // Getter
    public String getLocation() { return location; }

    // Operations
    public void dispenseCash(Account account, double amount) {
        System.out.println("ATM at " + location + " attempting to dispense $" + String.format("%.2f", amount) + " from account " + account.getAccountNumber());
        // Call the account's withdraw method directly
        if (account.withdraw(amount)) {
            System.out.println("Cash dispensed successfully.");
        } else {
            System.out.println("Cash dispensing failed. Check account balance.");
        }
    }

    public void acceptDeposit(Account account, double amount) {
        System.out.println("ATM at " + location + " attempting to accept deposit of $" + String.format("%.2f", amount) + " to account " + account.getAccountNumber());
        // Call the account's deposit method directly
        account.deposit(amount);
        System.out.println("Deposit accepted successfully by ATM.");
    }
}

// Loan.java
class Loan {
    private String loanId;
    private double loanAmount;
    private double interestRate;
    private Customer customer; // 1-to-1 relationship with Customer (requests)
    private boolean approved;

    public Loan(String loanId, double loanAmount, double interestRate) {
        this.loanId = loanId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.approved = false; // Not approved by default
    }

    // Getters
    public String getLoanId() { return loanId; }
    public double getLoanAmount() { return loanAmount; }
    public double getInterestRate() { return interestRate; }
    public Customer getCustomer() { return customer; }
    public boolean isApproved() { return approved; }

    // Setter for Customer
    public void setCustomer(Customer customer) { this.customer = customer; }

    // Operations
    public void approveLoan() {
        this.approved = true;
        System.out.println("Loan " + loanId + " for $" + String.format("%.2f", loanAmount) + " approved for " + (customer != null ? customer.getName() : "N/A Customer") + ".");
        // In a real system, approved loan amount would be credited to customer's account
        // For simplicity, we'll just print a message.
    }

    public void repayLoan(double amount) {
        if (this.approved) {
            if (loanAmount >= amount) {
                this.loanAmount -= amount;
                System.out.println("Repaid $" + String.format("%.2f", amount) + " for loan " + loanId + ". Remaining balance: $" + String.format("%.2f", loanAmount));
            } else {
                System.out.println("Repaying more than outstanding loan amount for loan " + loanId + ". Loan balance set to 0.");
                this.loanAmount = 0; // Loan fully repaid
            }
        } else {
            System.out.println("Loan " + loanId + " is not yet approved.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(loanId, loan.loanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId);
    }
}

// Transaction.java
class Transaction {
    private String transactionId;
    private Date date;
    private double amount;
    private String transactionType; // e.g., "Deposit", "Withdrawal", "Transfer"
    private Account account; // 1-to-1 relationship with Account (records)

    public Transaction(String transactionId, double amount, String transactionType, Account account) {
        this.transactionId = transactionId;
        this.date = new Date(); // Current date
        this.amount = amount;
        this.transactionType = transactionType;
        this.account = account;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public Date getDate() { return date; }
    public double getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public Account getAccount() { return account; }

    // Operations
    public boolean execute() {
        // This method applies the transaction's effect to the account by using its public methods.
        System.out.println("Executing transaction " + transactionId + " (" + transactionType + ") for account " + account.getAccountNumber());
        switch (transactionType) {
            case "Deposit":
                account.deposit(amount); // Use public deposit method
                return true;
            case "Withdrawal":
                return account.withdraw(amount); // Use public withdraw method
            default:
                System.out.println("Unknown transaction type: " + transactionType);
                return false;
        }
    }
}

// Main class to demonstrate the Banking System
public class BankingSystem {
    public static void main(String[] args) {
        // --- 1. Create a Bank ---
        Bank nationalBank = new Bank("National Bank of Java", "Downtown Branch");
        System.out.println(nationalBank.getName() + " opened at " + nationalBank.getLocation());

        // --- 2. Create Customers ---
        Customer alice = new Customer("Alice Smith", "CUST001", "123 Main St");
        Customer bob = new Customer("Bob Johnson", "CUST002", "456 Oak Ave");

        // --- 3. Open Accounts for Customers ---
        Account aliceChecking = nationalBank.openAccount(alice, "Checking", 1000.00);
        Account aliceSavings = nationalBank.openAccount(alice, "Savings", 5000.00);
        Account bobChecking = nationalBank.openAccount(bob, "Checking", 200.00);

        System.out.println("\n--- Customer Operations ---");

        // Alice transfers funds
        alice.transferFunds(aliceChecking, aliceSavings, 200.00);
        System.out.println("Alice's checking balance: $" + String.format("%.2f", aliceChecking.getBalance()));
        System.out.println("Alice's savings balance: $" + String.format("%.2f", aliceSavings.getBalance()));

        // Bob tries to withdraw more than he has
        bobChecking.withdraw(300.00); // Should fail

        // Bob requests a loan
        Loan bobLoan = bob.requestLoan(10000.00);

        System.out.println("\n--- Loan Operations ---");
        // Bank approves Bob's loan
        bobLoan.approveLoan();

        // Simulate Bob repaying the loan
        bobLoan.repayLoan(1000.00);
        bobLoan.repayLoan(9000.00); // Repay remaining

        System.out.println("\n--- ATM Operations ---");
        ATM downtownATM = new ATM("Downtown Kiosk", nationalBank);

        // Alice uses the ATM to withdraw
        downtownATM.dispenseCash(aliceChecking, 150.00);
        downtownATM.dispenseCash(aliceChecking, 1000.00); // Should fail due to insufficient funds after previous withdrawal

        // Bob uses the ATM to deposit
        downtownATM.acceptDeposit(bobChecking, 500.00);

        System.out.println("\n--- Final Balances ---");
        System.out.println("Alice's final checking balance: $" + String.format("%.2f", aliceChecking.getBalance()));
        System.out.println("Alice's final savings balance: $" + String.format("%.2f", aliceSavings.getBalance()));
        System.out.println("Bob's final checking balance: $" + String.format("%.2f", bobChecking.getBalance()));

        // Show transactions recorded for Alice's checking account
        System.out.println("\n--- Transactions for Alice's Checking Account ---");
        for (Transaction txn : aliceChecking.getTransactions()) {
            System.out.println("ID: " + txn.getTransactionId() + ", Type: " + txn.getTransactionType() + ", Amount: $" + String.format("%.2f", txn.getAmount()) + ", Date: " + txn.getDate());
        }

        // Close an account
        nationalBank.closeAccount(bobChecking);
        System.out.println("Is Bob's checking account still in the bank's list? " + nationalBank.getAccounts().contains(bobChecking));
    }
}
