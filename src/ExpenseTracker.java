import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Transaction class to represent each expense/income
class Transaction {
    private String id;
    private String description;
    private double amount;
    private String category;
    private LocalDate date;
    private TransactionType type;

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public Transaction(String description, double amount, String category, TransactionType type) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = LocalDate.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    @Override
    public String toString() {
        return String.format("%s | %s | $%.2f | %s | %s | %s",
                id, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                amount, type, category, description);
    }
}

// Main ExpenseTracker class
public class ExpenseTracker {
    private List<Transaction> transactions;
    private Scanner scanner;

    public ExpenseTracker() {
        this.transactions = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Personal Expense Tracker ===");

        while (true) {
            showMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    viewAllTransactions();
                    break;
                case 3:
                    viewTransactionsByCategory();
                    break;
                case 4:
                    showSummary();
                    break;
                case 5:
                    editTransaction();
                    break;
                case 6:
                    deleteTransaction();
                    break;
                case 7:
                    showMonthlyReport();
                    break;
                case 8:
                    System.out.println("Thank you for using Expense Tracker!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Add Transaction");
        System.out.println("2. View All Transactions");
        System.out.println("3. View by Category");
        System.out.println("4. Show Summary");
        System.out.println("5. Edit Transaction");
        System.out.println("6. Delete Transaction");
        System.out.println("7. Monthly Report");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
    }

    private void addTransaction() {
        System.out.println("\n--- Add New Transaction ---");

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Amount: $");
        double amount = getDoubleInput();

        System.out.print("Category: ");
        String category = scanner.nextLine();

        System.out.println("Transaction Type:");
        System.out.println("1. Income");
        System.out.println("2. Expense");
        System.out.print("Choose type: ");

        int typeChoice = getIntInput();
        Transaction.TransactionType type = typeChoice == 1 ?
                Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE;

        Transaction transaction = new Transaction(description, amount, category, type);
        transactions.add(transaction);

        System.out.println("Transaction added successfully! ID: " + transaction.getId());
    }

    private void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("ID | Date | Amount | Type | Category | Description");
        System.out.println("-".repeat(70));

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }

        System.out.println("\nTotal transactions: " + transactions.size());
    }

    private void viewTransactionsByCategory() {
        System.out.println("\n--- Transactions by Category ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        // Group transactions by category
        Map<String, List<Transaction>> byCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            byCategory.computeIfAbsent(transaction.getCategory(), k -> new ArrayList<>())
                    .add(transaction);
        }

        for (Map.Entry<String, List<Transaction>> entry : byCategory.entrySet()) {
            System.out.println("\nCategory: " + entry.getKey());
            System.out.println("-".repeat(50));

            double categoryTotal = 0;
            for (Transaction transaction : entry.getValue()) {
                System.out.println(transaction);
                categoryTotal += transaction.getType() == Transaction.TransactionType.INCOME ?
                        transaction.getAmount() : -transaction.getAmount();
            }

            System.out.printf("Category Total: $%.2f\n", categoryTotal);
        }
    }

    private void showSummary() {
        System.out.println("\n--- Financial Summary ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        double totalIncome = 0;
        double totalExpenses = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpenses += transaction.getAmount();
            }
        }

        double balance = totalIncome - totalExpenses;

        System.out.printf("Total Income: $%.2f\n", totalIncome);
        System.out.printf("Total Expenses: $%.2f\n", totalExpenses);
        System.out.printf("Net Balance: $%.2f\n", balance);

        if (balance >= 0) {
            System.out.println("✅ You're in the positive!");
        } else {
            System.out.println("⚠️  You're spending more than you earn.");
        }
    }

    private void editTransaction() {
        System.out.println("\n--- Edit Transaction ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions to edit.");
            return;
        }

        System.out.print("Enter transaction ID to edit: ");
        String id = scanner.nextLine();

        Transaction transaction = findTransactionById(id);
        if (transaction == null) {
            System.out.println("Transaction not found.");
            return;
        }

        System.out.println("Current transaction: " + transaction);
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Description");
        System.out.println("2. Amount");
        System.out.println("3. Category");
        System.out.print("Choose: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.print("New description: ");
                transaction.setDescription(scanner.nextLine());
                break;
            case 2:
                System.out.print("New amount: $");
                transaction.setAmount(getDoubleInput());
                break;
            case 3:
                System.out.print("New category: ");
                transaction.setCategory(scanner.nextLine());
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("Transaction updated successfully!");
    }

    private void deleteTransaction() {
        System.out.println("\n--- Delete Transaction ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions to delete.");
            return;
        }

        System.out.print("Enter transaction ID to delete: ");
        String id = scanner.nextLine();

        Transaction transaction = findTransactionById(id);
        if (transaction == null) {
            System.out.println("Transaction not found.");
            return;
        }

        System.out.println("Transaction to delete: " + transaction);
        System.out.print("Are you sure? (y/n): ");

        String confirmation = scanner.nextLine();
        if (confirmation.toLowerCase().equals("y")) {
            transactions.remove(transaction);
            System.out.println("Transaction deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void showMonthlyReport() {
        System.out.println("\n--- Monthly Report ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        Map<String, Double> monthlyIncome = new HashMap<>();
        Map<String, Double> monthlyExpenses = new HashMap<>();

        for (Transaction transaction : transactions) {
            String month = transaction.getDate().getYear() + "-" +
                    String.format("%02d", transaction.getDate().getMonthValue());

            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                monthlyIncome.put(month, monthlyIncome.getOrDefault(month, 0.0) + transaction.getAmount());
            } else {
                monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + transaction.getAmount());
            }
        }

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(monthlyIncome.keySet());
        allMonths.addAll(monthlyExpenses.keySet());

        System.out.printf("%-10s | %-12s | %-12s | %-12s%n", "Month", "Income", "Expenses", "Balance");
        System.out.println("-".repeat(55));

        for (String month : allMonths) {
            double income = monthlyIncome.getOrDefault(month, 0.0);
            double expenses = monthlyExpenses.getOrDefault(month, 0.0);
            double balance = income - expenses;

            System.out.printf("%-10s | $%-11.2f | $%-11.2f | $%-11.2f%n",
                    month, income, expenses, balance);
        }
    }

    private Transaction findTransactionById(String id) {
        return transactions.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private int getIntInput() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value < 0) {
                    System.out.print("Please enter a positive amount: ");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid amount: ");
            }
        }
    }

    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.start();
    }
}
