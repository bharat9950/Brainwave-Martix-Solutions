import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// Account class to represent bank accounts
class Account {
    private final String accountNumber;
    private String pin;
    private double balance;
    private final String accountHolderName;
    private final List<Transaction> transactionHistory;
    
    public Account(String accountNumber, String pin, double initialBalance, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.accountHolderName = accountHolderName;
        this.transactionHistory = new ArrayList<Transaction>();
    }
    
    // Getters and setters
    public String getAccountNumber() { return accountNumber; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public String getAccountHolderName() { return accountHolderName; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    
    public void setBalance(double balance) { this.balance = balance; }
    public void setPin(String pin) { this.pin = pin; }
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }
}

// Transaction class to record transaction details
class Transaction {
    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final Date timestamp;
    
    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = new Date();
    }
    
    // Getters
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public Date getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %s | Rs%.2f | Balance: Rs%.2f", 
                           formatter.format(timestamp), type, amount, balanceAfter);
    }
}

// Bank class to manage accounts and operations
class Bank {
    private final Map<String, Account> accounts;
    
    public Bank() {
        accounts = new HashMap<String, Account>();
        initializeAccounts();
    }
    
    private void initializeAccounts() {
        // Initialize demo accounts
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

// Main ATM GUI Application
public class ATMApplication extends JFrame {
    private final Bank bank;
    private Account currentAccount;
    private boolean isLoggedIn;
    
    // UI Components
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private JPanel loginPanel;
    private JPanel mainMenuPanel;
    private JTextField accountField;
    private JPasswordField pinField;
    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    
    public ATMApplication() {
        bank = new Bank();
        isLoggedIn = false;
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Secure ATM - Banking System");
        
        // Set Look and Feel
        try {
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
} catch (Exception e) {
    // fallback to default
    e.printStackTrace();
}

    }
    
    private void initializeUI() {
        createLoginPanel();
        createMainMenuPanel();
        
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(mainMenuPanel, "MAIN_MENU");
        
        add(cardPanel);
        
        // Show demo accounts info
        showDemoAccountsDialog();
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(new Color(45, 52, 74));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(45, 52, 74));
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SECURE ATM SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        // Login Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Account Number
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Account Number:"), gbc);
        
        gbc.gridx = 1;
        accountField = new JTextField(15);
        accountField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(accountField, gbc);
        
        // PIN
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("PIN:"), gbc);
        
        gbc.gridx = 1;
        pinField = new JPasswordField(15);
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(pinField, gbc);
        
        // Login Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        formPanel.add(loginButton, gbc);
        
        // Demo Accounts Button
        gbc.gridy = 3;
        JButton demoButton = new JButton("View Demo Accounts");
        demoButton.setFont(new Font("Arial", Font.PLAIN, 12));
        demoButton.setBackground(new Color(149, 165, 166));
        demoButton.setForeground(Color.WHITE);
        demoButton.setFocusPainted(false);
        demoButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        demoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDemoAccountsDialog();
            }
        });
        formPanel.add(demoButton, gbc);
        
        loginPanel.add(titlePanel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add Enter key listener for login
        pinField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BorderLayout());
        mainMenuPanel.setBackground(new Color(45, 52, 74));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 52, 74));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        welcomeLabel = new JLabel("Welcome, User!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        balanceLabel = new JLabel("Current Balance: Rs0.00");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(balanceLabel);
        
        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create menu buttons
        JButton balanceBtn = createMenuButton("Balance Inquiry", "Check your current balance");
        JButton withdrawBtn = createMenuButton("Withdraw Money", "Withdraw cash from your account");
        JButton depositBtn = createMenuButton("Deposit Money", "Deposit money to your account");
        JButton transferBtn = createMenuButton("Transfer Money", "Transfer money to another account");
        JButton historyBtn = createMenuButton("Transaction History", "View your recent transactions");
        JButton changePinBtn = createMenuButton("Change PIN", "Change your account PIN");
        JButton logoutBtn = createMenuButton("Logout", "Exit your session");
        
        // Add action listeners
        balanceBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                balanceInquiry();
            }
        });
        
        withdrawBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });
        
        depositBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });
        
        transferBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transfer();
            }
        });
        
        historyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transactionHistory();
            }
        });
        
        changePinBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changePin();
            }
        });
        
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        menuPanel.add(balanceBtn);
        menuPanel.add(withdrawBtn);
        menuPanel.add(depositBtn);
        menuPanel.add(transferBtn);
        menuPanel.add(historyBtn);
        menuPanel.add(changePinBtn);
        menuPanel.add(logoutBtn);
        
        mainMenuPanel.add(headerPanel, BorderLayout.NORTH);
        mainMenuPanel.add(menuPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        button.setToolTipText(tooltip);
        
        return button;
    }
    
    private void handleLogin() {
        String accountNumber = accountField.getText().trim();
        String pin = new String(pinField.getPassword());
        
        if (accountNumber.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both account number and PIN.", 
                                        "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (bank.validateAccount(accountNumber, pin)) {
            currentAccount = bank.getAccount(accountNumber);
            isLoggedIn = true;
            
            welcomeLabel.setText("Welcome, " + currentAccount.getAccountHolderName() + "!");
            updateBalanceLabel();
            
            cardLayout.show(cardPanel, "MAIN_MENU");
            
            // Clear login fields
            accountField.setText("");
            pinField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid account number or PIN. Please try again.", 
                                        "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Current Balance: Rs%.2f", currentAccount.getBalance()));
    }
    
    private void balanceInquiry() {
        currentAccount.addTransaction(new Transaction("BALANCE INQUIRY", 0, currentAccount.getBalance()));
        updateBalanceLabel();
        
        String message = String.format("Account Number: %s\nAccount Holder: %s\nCurrent Balance: Rs%.2f",
                                     currentAccount.getAccountNumber(),
                                     currentAccount.getAccountHolderName(),
                                     currentAccount.getBalance());
        
        JOptionPane.showMessageDialog(this, message, "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void withdraw() {
        String input = JOptionPane.showInputDialog(this, 
            String.format("Current Balance: Rs%.2f\nEnter withdrawal amount:", currentAccount.getBalance()),
            "Withdraw Money", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(input);
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount.", 
                                            "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds!", 
                                            "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (bank.withdraw(currentAccount.getAccountNumber(), amount)) {
                updateBalanceLabel();
                String message = String.format("Withdrawal successful!\nAmount: Rs%.2f\nNew Balance: Rs%.2f",
                                             amount, currentAccount.getBalance());
                JOptionPane.showMessageDialog(this, message, "Withdrawal Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Withdrawal failed. Please try again.", 
                                            "Withdrawal Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deposit() {
        String input = JOptionPane.showInputDialog(this, 
            String.format("Current Balance: Rs%.2f\nEnter deposit amount:", currentAccount.getBalance()),
            "Deposit Money", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(input);
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount.", 
                                            "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (bank.deposit(currentAccount.getAccountNumber(), amount)) {
                updateBalanceLabel();
                String message = String.format("Deposit successful!\nAmount: Rs%.2f\nNew Balance: Rs%.2f",
                                             amount, currentAccount.getBalance());
                JOptionPane.showMessageDialog(this, message, "Deposit Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Deposit failed. Please try again.", 
                                            "Deposit Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void transfer() {
        String toAccount = JOptionPane.showInputDialog(this, "Enter recipient account number:", 
                                                     "Transfer Money", JOptionPane.QUESTION_MESSAGE);
        
        if (toAccount == null || toAccount.trim().isEmpty()) return;
        
        Account recipient = bank.getAccount(toAccount);
        if (recipient == null) {
            JOptionPane.showMessageDialog(this, "Recipient account not found.", 
                                        "Transfer Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (toAccount.equals(currentAccount.getAccountNumber())) {
            JOptionPane.showMessageDialog(this, "Cannot transfer to the same account.", 
                                        "Transfer Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, 
            String.format("Recipient: %s\nCurrent Balance: Rs%.2f\nEnter transfer amount:",
                         recipient.getAccountHolderName(), currentAccount.getBalance()),
            "Transfer Money", JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            double amount = Double.parseDouble(input);
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount.", 
                                            "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds!", 
                                            "Transfer Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (bank.transfer(currentAccount.getAccountNumber(), toAccount, amount)) {
                updateBalanceLabel();
                String message = String.format("Transfer successful!\nAmount: Rs%.2f\nTo: %s (%s)\nNew Balance: Rs%.2f",
                                             amount, recipient.getAccountHolderName(), toAccount, currentAccount.getBalance());
                JOptionPane.showMessageDialog(this, message, "Transfer Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Transfer failed. Please try again.", 
                                            "Transfer Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", 
                                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void transactionHistory() {
        List<Transaction> history = currentAccount.getTransactionHistory();
        
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions found.", 
                                        "Transaction History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Recent Transactions (Last 10):\n");
        sb.append("----------------------------------------\n");
        
        int start = Math.max(0, history.size() - 10);
        for (int i = history.size() - 1; i >= start; i--) {
            sb.append(history.get(i)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void changePin() {
        String currentPin = JOptionPane.showInputDialog(this, "Enter current PIN:", 
                                                      "Change PIN", JOptionPane.QUESTION_MESSAGE);
        
        if (currentPin == null || currentPin.trim().isEmpty()) return;
        
        if (!currentAccount.validatePin(currentPin)) {
            JOptionPane.showMessageDialog(this, "Incorrect current PIN.", 
                                        "PIN Change Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newPin = JOptionPane.showInputDialog(this, "Enter new PIN (4 digits):", 
                                                  "Change PIN", JOptionPane.QUESTION_MESSAGE);
        
        if (newPin == null || newPin.trim().isEmpty()) return;
        
        if (newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Invalid PIN format. PIN must be exactly 4 digits.", 
                                        "PIN Change Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String confirmPin = JOptionPane.showInputDialog(this, "Confirm new PIN:", 
                                                      "Change PIN", JOptionPane.QUESTION_MESSAGE);
        
        if (confirmPin == null || !newPin.equals(confirmPin)) {
            JOptionPane.showMessageDialog(this, "PINs do not match. Please try again.", 
                                        "PIN Change Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        bank.changePin(currentAccount.getAccountNumber(), newPin);
        JOptionPane.showMessageDialog(this, "PIN changed successfully!", 
                                    "PIN Change Successful", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Logout", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            isLoggedIn = false;
            currentAccount = null;
            cardLayout.show(cardPanel, "LOGIN");
            
            JOptionPane.showMessageDialog(this, "Thank you for using our ATM!\nHave a great day!", 
                                        "Logout Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showDemoAccountsDialog() {
        String message = "Demo Accounts for Testing:\n\n" +
                        "Account 1:\n" +
                        "Number: 1234567890\n" +
                        "PIN: 1234\n" +
                        "Holder: Bharat Choudhary\n" +
                        "Balance: Rs1,500,000.00\n\n" +
                        "Account 2:\n" +
                        "Number: 0987654321\n" +
                        "PIN: 5678\n" +
                        "Holder: Anil Seervi\n" +
                        "Balance: Rs250,000.75\n\n" +
                        "Account 3:\n" +
                        "Number: 1122334455\n" +
                        "PIN: 9999\n" +
                        "Holder: Manish Kumar\n" +
                        "Balance: Rs750,000.25";
        
        JOptionPane.showMessageDialog(this, message, "Demo Accounts", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ATMApplication().setVisible(true);
            }
        });
    }
}