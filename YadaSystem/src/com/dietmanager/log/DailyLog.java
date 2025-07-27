package com.dietmanager.log;

import com.dietmanager.database.FoodDatabase;
import com.dietmanager.food.Food;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class DailyLog {
    // private final FoodDatabase foodDatabase ;
    private static final String LOG_FILE = "data/daily_log.txt";
    private static final List<LogEntry> logEntries = new ArrayList<>();
    private static final Deque<Command> undoStack = new ArrayDeque<>();
    private static String currentDate;

    public void handleCLI(Scanner scanner , FoodDatabase foodDatabase) {
        loadLog();

        System.out.print("Enter the date for this session (YYYY-MM-DD): ");
        currentDate = scanner.nextLine().trim();

        boolean back = false;
        while (!back) {
            System.out.println("\n--- Daily Log (" + currentDate + ") ---");
            System.out.println("1. View Log");
            System.out.println("2. Add Food");
            System.out.println("3. Delete Food");
            System.out.println("4. Undo Last Action");
            System.out.println("5. Change Date");
            System.out.println("6. Save Log");
            System.out.println("7. Total calories consumed on this " + currentDate + ": ");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewLog();
                case "2" -> addFood(scanner , foodDatabase);
                case "3" -> deleteFood(scanner);
                case "4" -> undo();
                case "5" -> {
                    System.out.print("Enter new date (YYYY-MM-DD): ");
                    currentDate = scanner.nextLine().trim();
                }
                case "6" -> saveLog();
                case "7" -> showTotalCaloriesForDate(currentDate);
                case "8" -> {
                    saveLog();
                    back = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewLog() {
        System.out.println("\n--- Log for " + currentDate + " ---");
        logEntries.stream()
                .filter(e -> e.date.equals(currentDate))
                .forEach(System.out::println);
    }

    private static void addFood(Scanner scanner, FoodDatabase foodDatabase) {
        List<Food> foods = foodDatabase.get_Foods();
    
        System.out.print("Enter keywords (comma-separated): ");
        List<String> keywords = Arrays.asList(scanner.nextLine().toLowerCase().split(",\\s*"));
    
        System.out.print("Match all keywords? (yes/no): ");
        boolean matchAll = scanner.nextLine().trim().equalsIgnoreCase("yes");
    
        List<Food> filtered = new ArrayList<>();
        for (Food food : foods) {
            String content = (food.getName() + " " + food.toString()).toLowerCase();
    
            boolean matches = matchAll
                ? keywords.stream().allMatch(content::contains)
                : keywords.stream().anyMatch(content::contains);
    
            if (matches) {
                filtered.add(food);
            }
        }
    
        if (filtered.isEmpty()) {
            System.out.println("No matching foods found.");
            return;
        }
    
        for (int i = 0; i < filtered.size(); i++) {
            System.out.println((i + 1) + ". " + filtered.get(i));
        }
    
        System.out.print("Select food number to add: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= filtered.size()) {
            System.out.println("Invalid selection.");
            return;
        }
    
        Food selected = filtered.get(index);
        System.out.print("Enter servings: ");
        double servings = Double.parseDouble(scanner.nextLine());
    
        LogEntry entry = new LogEntry(currentDate, selected.getName(),
                String.join(",", selected.keywords), servings, selected.getCalories() * servings);
        logEntries.add(entry);
        undoStack.push(() -> logEntries.remove(entry));
        System.out.println("Food added to log.");
    }
    

    private static void deleteFood(Scanner scanner) {
        List<LogEntry> daily = new ArrayList<>();
        for (LogEntry e : logEntries) {
            if (e.date.equals(currentDate)) {
                daily.add(e);
            }
        }

        if (daily.isEmpty()) {
            System.out.println("No entries for this date.");
            return;
        }

        for (int i = 0; i < daily.size(); i++) {
            System.out.println((i + 1) + ". " + daily.get(i));
        }

        System.out.print("Select entry to delete: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= daily.size()) {
            System.out.println("Invalid index.");
            return;
        }

        LogEntry removed = daily.get(index);
        logEntries.remove(removed);
        undoStack.push(() -> logEntries.add(removed));
        System.out.println("Entry deleted.");
    }

    private static void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
        } else {
            undoStack.pop().undo();
            System.out.println("Last action undone.");
        }
    }

    private static void saveLog() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            for (LogEntry e : logEntries) {
                writer.write(e.serialize());
                writer.newLine();
            }
            System.out.println("Log saved.");
        } catch (IOException e) {
            System.out.println("Error saving log: " + e.getMessage());
        }
    }

    public double showTotalCaloriesForDate(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            double cosumer =  logEntries.stream()
                    .filter(e -> LocalDate.parse(e.date).equals(date))
                    .mapToDouble(e -> e.calories)
                    .sum();
            System.out.println("Total calories consumed on " + dateString + ": " + cosumer);
            return cosumer;
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return 0.0;
        }
    }
    
    

    

    private static void loadLog() {
        File file = new File(LOG_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            logEntries.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    logEntries.add(new LogEntry(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            Double.parseDouble(parts[3].trim()),
                            Double.parseDouble(parts[4].trim())));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading log: " + e.getMessage());
        }
    }

    private interface Command {
        void undo();
    }

    private static class LogEntry {
        String date;
        String name;
        String keywords;
        double servings;
        double calories;

        LogEntry(String date, String name, String keywords, double servings, double calories) {
            this.date = date;
            this.name = name;
            this.keywords = keywords;
            this.servings = servings;
            this.calories = calories;
        }

        String serialize() {
            return String.format("%s | %s | %s | %.2f | %.2f", date, name, keywords, servings, calories);
        }

        @Override
        public String toString() {
            return serialize();
        }
    }
}
