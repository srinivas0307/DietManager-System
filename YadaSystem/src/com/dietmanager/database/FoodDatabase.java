package com.dietmanager.database;

import com.dietmanager.food.BasicFood;
import com.dietmanager.food.CompositeFood;
import com.dietmanager.food.Food;
import java.io.*;
import java.util.*;

public class FoodDatabase {
    private  final String DATABASE_FILE = "data/food_database.txt";
    private final List<Food> foods;

    public FoodDatabase() {
        foods = new ArrayList<>();
        loadDatabase();
    }

    public void handleCLI(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Food Database ---");
            System.out.println("1. List Foods");
            System.out.println("2. Add Basic Food");
            System.out.println("3. Add Composite Food");
            System.out.println("4. Save Database");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> this.listFoods();
                case "2" -> addBasicFood(scanner);
                case "3" -> addCompositeFood(scanner);
                case "4" -> saveDatabase();
                case "5" -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void listFoods() {
        if (foods.isEmpty()) {
            System.out.println("No foods available in the database.");
            return;
        }
        for (Food food : foods) {
            System.out.println(food);
        }
    }

    public List<Food> get_Foods() {
        return new ArrayList<>(foods); // Return a copy to prevent external modification
    }
    
    
    private void addBasicFood(Scanner scanner) {
        System.out.print("Enter food name: ");
        String name = scanner.nextLine();
    
        System.out.print("Enter keywords (comma-separated): ");
        List<String> keywords = Arrays.asList(scanner.nextLine().split(",\\s*"));
    
        System.out.print("Enter calories per serving: ");
        double calories = Double.parseDouble(scanner.nextLine());
    
        Map<String, Double> nutrition = new HashMap<>();
        while (true) {
            System.out.print("Add a nutrient? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.equals("yes")) break;
    
            System.out.print("Enter nutrient name (e.g., protein, fat): ");
            String nutrient = scanner.nextLine().trim();
    
            System.out.print("Enter amount per serving: ");
            double amount = Double.parseDouble(scanner.nextLine());
    
            nutrition.put(nutrient, amount);
        }
    
        foods.add(new BasicFood(name, keywords, calories, nutrition));
        System.out.println("Basic food with nutritional info added.");
    }
    

    private void addCompositeFood(Scanner scanner) {
        System.out.print("Enter composite food name: ");
        String name = scanner.nextLine();

        System.out.print("Enter keywords (comma-separated): ");
        List<String> keywords = Arrays.asList(scanner.nextLine().split(",\\s*"));

        Map<Food, Double> components = new HashMap<>();
        while (true) {
            listFoods();
            System.out.print("Enter food name to add to composite (or 'done' to finish): ");
            String foodName = scanner.nextLine();
            if (foodName.equalsIgnoreCase("done")) break;

            Food selected = findFoodByName(foodName);
            if (selected == null) {
                System.out.println("Food not found.");
                continue;
            }
            System.out.print("Enter servings of selected food: ");
            double servings = Double.parseDouble(scanner.nextLine());
            components.put(selected, servings);
        }

        if (!components.isEmpty()) {
            foods.add(new CompositeFood(name, keywords, components));
            System.out.println("Composite food added.");
        } else {
            System.out.println("No components added. Composite food not created.");
        }
    }

    private Food findFoodByName(String name) {
        for (Food food : foods) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null;
    }

    private void saveDatabase() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (Food food : foods) {
                writer.write(food.serialize());
                writer.newLine();
            }
            System.out.println("Database saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save database: " + e.getMessage());
        }
    }

    private void loadDatabase() {
        File file = new File(DATABASE_FILE);

        System.out.println("Loading database from " + file.getAbsolutePath());
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            foods.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                Food food = Food.deserialize(line, foods);
                if (food != null) foods.add(food);
            }
        } catch (IOException e) {
            System.out.println("Error reading database file: " + e.getMessage());
        }
    }

    public List<Food> getFoods() {
        return foods;
    }
}
