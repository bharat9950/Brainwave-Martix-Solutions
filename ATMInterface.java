import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Account class to represent bank accounts
class Account {
    private String accountNumber;
    public String pin;
    private double balance;
    private String accountHolderName;
    private List<Transaction> transactionHistory;
    
    public Account(String accountNumber, String pin, double initialBalance, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.accountHolderName = accountHolderName;
        this.transactionHistory = new ArrayList<>();
    }
    
    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public String getAccountHolderName() { return accountHolderName; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    
    public void setBalance(double balance) { this.balance = balance; }
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }
    
    public void setPin(String newPin) {
        this.pin = newPin;
    }
}

// Transaction class to record transaction details
class Transaction {
    private String type;
    private double amount;
    private double balanceAfter;
    private LocalDateTime timestamp;
    
    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %s | ₹%.2f | Balance: ₹%.2f", 
                           timestamp.format(formatter), type, amount, balanceAfter);
    }
}

// Bank class to manage accounts and operations
class Bank {
    private Map<String, Account> accounts;
    private DecimalFormat df;
    
    public Bank() {
        accounts = new HashMap<>();
        df = new DecimalFormat("#.00");
        initializeAccounts();
    }
    
    private void initializeAccounts() {
        // Initialize some demo accounts
        accounts.put("1234567890", new Account("1234567890", "1234", 1500000.00, "Bharat Choudhary"));
        accounts.put("0987654321", new Account("0987654321", "5678", 250000.75, "Anil Seervi"));
        accounts.put("1122334455", new Account("1122334455", "9999", 750000.25, "Manish Kumar"));
    }
    
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public boolean validateAccount(String accountNumber, String pin) {
        Account account = accounts.get(accountNumber);
        return account != null && account.validatePin(pin);
    }
    
    public boolean withdraw(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null && account.getBalance() >= amount && amount > 0) {
            double newBalance = account.getBalance() - amount;
            account.setBalance(newBalance);
            account.addTransaction(new Transaction("WITHDRAWAL", amount, newBalance));
            return true;
        }
        return false;
    }
    
    public boolean deposit(String accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account != null && amount > 0) {
            double newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
            account.addTransaction(new Transaction("DEPOSIT", amount, newBalance));
            return true;
        }
        return false;
    }
    
    public boolean transfer(String fromAccount, String toAccount, double amount) {
        Account from = accounts.get(fromAccount);
        Account to = accounts.get(toAccount);
        
        if (from != null && to != null && from.getBalance() >= amount && amount > 0) {
            double fromNewBalance = from.getBalance() - amount;
            double toNewBalance = to.getBalance() + amount;
            
            from.setBalance(fromNewBalance);
            to.setBalance(toNewBalance);
            
            from.addTransaction(new Transaction("TRANSFER OUT to " + toAccount, amount, fromNewBalance));
            to.addTransaction(new Transaction("TRANSFER IN from " + fromAccount, amount, toNewBalance));
            
            return true;
        }
        return false;
    }
    
    public void changePin(String accountNumber, String newPin) {
        Account account = accounts.get(accountNumber);
        if (account != null) {
            account.setPin(newPin);
            account.addTransaction(new Transaction("PIN CHANGE", 0, account.getBalance()));
        }
    }
}

// Main ATM class with user interface
public class ATMInterface {
    private Bank bank;
    private Scanner scanner;
    private Account currentAccount;
    private boolean isLoggedIn;
    
    public ATMInterface() {
        bank = new Bank();
        scanner = new Scanner(System.in);
        isLoggedIn = false;
    }
    
    public void start() {
        System.out.println("=================================");
        System.out.println("   WELCOME TO SECURE ATM");
        System.out.println("=================================");
        
        while (true) {
            if (!isLoggedIn) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Account Number (or 'exit' to quit): ");
        String accountNumber = scanner.nextLine();
        
        if (accountNumber.equalsIgnoreCase("exit")) {
            System.out.println("Thank you for using our ATM!");
            System.exit(0);
        }
        
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();
        
        if (bank.validateAccount(accountNumber, pin)) {
            currentAccount = bank.getAccount(accountNumber);
            isLoggedIn = true;
            System.out.println("\nLogin successful! Welcome, " + currentAccount.getAccountHolderName());
        } else {
            System.out.println("Invalid account number or PIN. Please try again.");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=================================");
        System.out.println("         ATM MAIN MENU");
        System.out.println("=================================");
        System.out.println("1. Balance Inquiry");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Deposit Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Transaction History");
        System.out.println("6. Change PIN");
        System.out.println("7. Logout");
        System.out.print("\nSelect an option (1-7): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
                case 1:
                    balanceInquiry();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    transactionHistory();
                    break;
                case 6:
                    changePin();
                    break;
                case 7:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option. Please select 1-7.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void balanceInquiry() {
        System.out.println("\n--- BALANCE INQUIRY ---");
        System.out.printf("Account Number: %s\n", currentAccount.getAccountNumber());
        System.out.printf("Account Holder: %s\n", currentAccount.getAccountHolderName());
        System.out.printf("Current Balance: ₹%.2f\n", currentAccount.getBalance());
        
        currentAccount.addTransaction(new Transaction("BALANCE INQUIRY", 0, currentAccount.getBalance()));
        pressEnterToContinue();
    }
    
    private void withdraw() {
        System.out.println("\n--- WITHDRAW MONEY ---");
        System.out.printf("Current Balance: ₹%.2f\n", currentAccount.getBalance());
        System.out.print("Enter withdrawal amount: ₹");
        
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive number.");
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                System.out.println("Insufficient funds. Your current balance is ₹" + 
                                 String.format("%.2f", currentAccount.getBalance()));
                return;
            }
            
            if (bank.withdraw(currentAccount.getAccountNumber(), amount)) {
                System.out.println("Withdrawal successful!");
                System.out.printf("Amount withdrawn: ₹%.2f\n", amount);
                System.out.printf("New balance: ₹%.2f\n", currentAccount.getBalance());
            } else {
                System.out.println("Withdrawal failed. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
        
        pressEnterToContinue();
    }
    
    private void deposit() {
        System.out.println("\n--- DEPOSIT MONEY ---");
        System.out.printf("Current Balance: ₹%.2f\n", currentAccount.getBalance());
        System.out.print("Enter deposit amount: ₹");
        
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive number.");
                return;
            }
            
            if (bank.deposit(currentAccount.getAccountNumber(), amount)) {
                System.out.println("Deposit successful!");
                System.out.printf("Amount deposited: ₹%.2f\n", amount);
                System.out.printf("New balance: ₹%.2f\n", currentAccount.getBalance());
            } else {
                System.out.println("Deposit failed. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
        
        pressEnterToContinue();
    }
    
    private void transfer() {
        System.out.println("\n--- TRANSFER MONEY ---");
        System.out.printf("Current Balance: ₹%.2f\n", currentAccount.getBalance());
        System.out.print("Enter recipient account number: ");
        String toAccount = scanner.nextLine();
        
        Account recipient = bank.getAccount(toAccount);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            pressEnterToContinue();
            return;
        }
        
        if (toAccount.equals(currentAccount.getAccountNumber())) {
            System.out.println("Cannot transfer to the same account.");
            pressEnterToContinue();
            return;
        }
        
        System.out.printf("Recipient: %s\n", recipient.getAccountHolderName());
        System.out.print("Enter transfer amount: ₹");
        
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            
            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive number.");
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                System.out.println("Insufficient funds. Your current balance is ₹" + 
                                 String.format("%.2f", currentAccount.getBalance()));
                return;
            }
            
            if (bank.transfer(currentAccount.getAccountNumber(), toAccount, amount)) {
                System.out.println("Transfer successful!");
                System.out.printf("Amount transferred: ₹%.2f\n", amount);
                System.out.printf("To: %s (%s)\n", recipient.getAccountHolderName(), toAccount);
                System.out.printf("New balance: ₹%.2f\n", currentAccount.getBalance());
            } else {
                System.out.println("Transfer failed. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
        
        pressEnterToContinue();
    }
    
    private void transactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        List<Transaction> history = currentAccount.getTransactionHistory();
        
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Recent Transactions (most recent first):");
            System.out.println("----------------------------------------");
            
            // Show last 10 transactions
            int start = Math.max(0, history.size() - 10);
            for (int i = history.size() - 1; i >= start; i--) {
                System.out.println(history.get(i));
            }
            
            if (history.size() > 10) {
                System.out.println("\n(Showing last 10 transactions)");
            }
        }
        
        pressEnterToContinue();
    }
    
    private void changePin() {
        System.out.println("\n--- CHANGE PIN ---");
        System.out.print("Enter current PIN: ");
        String currentPin = scanner.nextLine();
        
        if (!currentAccount.validatePin(currentPin)) {
            System.out.println("Incorrect current PIN.");
            pressEnterToContinue();
            return;
        }
        
        System.out.print("Enter new PIN (4 digits): ");
        String newPin = scanner.nextLine();
        
        if (newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            System.out.println("Invalid PIN format. PIN must be exactly 4 digits.");
            pressEnterToContinue();
            return;
        }
        
        System.out.print("Confirm new PIN: ");
        String confirmPin = scanner.nextLine();
        
        if (!newPin.equals(confirmPin)) {
            System.out.println("PINs do not match. Please try again.");
            pressEnterToContinue();
            return;
        }
        
        bank.changePin(currentAccount.getAccountNumber(), newPin);
        System.out.println("PIN changed successfully!");
        
        pressEnterToContinue();
    }
    
    private void logout() {
        System.out.println("\n--- LOGOUT ---");
        System.out.println("Thank you for using our ATM, " + currentAccount.getAccountHolderName() + "!");
        System.out.println("Have a great day!");
        isLoggedIn = false;
        currentAccount = null;
        
        System.out.println("\nPress Enter to return to login screen...");
        scanner.nextLine();
    }
    
    private void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    // Display demo accounts for testing
    private void showDemoAccounts() {
        System.out.println("\n=== DEMO ACCOUNTS FOR TESTING ===");
        System.out.println("Account 1: 1234567890, PIN: 1234 (John Doe, ₹1500000.00)");
        System.out.println("Account 2: 0987654321, PIN: 5678 (Jane Smith, ₹250000.75)");
        System.out.println("Account 3: 1122334455, PIN: 9999 (Bob Johnson, ₹750000.25)");
        System.out.println("===================================");
    }
    
    public static void main(String[] args) {
        ATMInterface atm = new ATMInterface();
        atm.showDemoAccounts();
        atm.start();
    }
}