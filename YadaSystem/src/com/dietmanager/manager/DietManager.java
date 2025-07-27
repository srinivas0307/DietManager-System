package com.dietmanager.manager;

import com.dietmanager.database.FoodDatabase;
import com.dietmanager.log.DailyLog;
import com.dietmanager.profile.UserProfile;
import java.io.File;
import java.util.Scanner;

public class DietManager {

    private final DailyLog dailyLog;
    private final FoodDatabase foodDatabase;
    private final UserProfile userProfile;

    public DietManager(Scanner scanner) {
        this.dailyLog = new DailyLog();
        this.foodDatabase = new FoodDatabase();

        File profileFile = new File("data/user_profile.txt");

        if (profileFile.exists()) {
            System.out.print("Previous profile found. Do you want to load it? (y/n): ");
            String response = scanner.nextLine().trim();

            if (response.equalsIgnoreCase("y")) {
                userProfile = new UserProfile();
                userProfile.loadProfile();
                System.out.println("Profile loaded successfully.");
            } else {
                userProfile = new UserProfile();
                userProfile.createNewProfile(scanner);
                userProfile.saveProfile();
                System.out.println("New profile created and saved.");
            }
        } else {
            System.out.println("No previous profile found. Creating a new one.");
            userProfile = new UserProfile();
            userProfile.createNewProfile(scanner);
            userProfile.saveProfile();
        }
    }

    public void run(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- YADA: Yet Another Diet Assistant ---");
            System.out.println("1. Daily Log");
            System.out.println("2. Food Database");
            System.out.println("3. Profile");
            System.out.println("4. Get all foods");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> dailyLog.handleCLI(scanner, foodDatabase);
                    case "2" -> foodDatabase.handleCLI(scanner);
                    case "3" -> {
                        userProfile.handleCLI(scanner , dailyLog); // For updates like weight, age, activity level
                        userProfile.saveProfile(); // Save changes
                    }
                    case "4" -> foodDatabase.listFoods();
                    case "5" -> {
                        System.out.print("Are you sure you want to exit? (y/n): ");
                        String confirm = scanner.nextLine().trim();
                        if (confirm.equalsIgnoreCase("y")) {
                            userProfile.saveProfile();
                            System.out.println("Saving and exiting. Goodbye!");
                            exit = true;
                        }
                    }
                    default -> System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            DietManager dietManager = new DietManager(scanner);
            dietManager.run(scanner);
        }
    }
}
