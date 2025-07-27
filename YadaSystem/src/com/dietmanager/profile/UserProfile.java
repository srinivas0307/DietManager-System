package com.dietmanager.profile;
import com.dietmanager.log.DailyLog;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class UserProfile {
    private final String PROFILE_FILE = "data/user_profile.txt";
    private final Map<LocalDate, DailyProfile> profiles = new HashMap<>();
    private String gender = "Male"; // default
    private int height = 170;
    private CalculationMethod method = CalculationMethod.HARRIS_BENEDICT;
    private enum CalculationMethod {
        HARRIS_BENEDICT, MIFFLIN_ST_JEOR
    }

    private class DailyProfile {
        int age;
        double weight;
        String activityLevel;

        DailyProfile(int age, double weight, String activityLevel) {
            this.age = age;
            this.weight = weight;
            this.activityLevel = activityLevel;
        }
    }

    public UserProfile() {
        // this.dailyLog = dailyLog;
        // Will load later explicitly if needed
    }

    public void handleCLI(Scanner scanner , DailyLog dailyLog) {
        LocalDate selectedDate = LocalDate.now();

        boolean back = false;
        while (!back) {
            System.out.println("\n--- User Profile (" + selectedDate + ") ---");
            System.out.println("1. View Profile");
            System.out.println("2. Update Gender, Height, Age, or Weight");
            System.out.println("3. Change Activity Level");
            System.out.println("4. Change Calorie Calculation Method");
            System.out.println("5. Show Target vs. Consumed Calories");
            System.out.println("6. Change Date");
            System.out.println("7. Save Profile");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewProfile(selectedDate);
                case "2" -> updatePersonalInfo(scanner, selectedDate);
                case "3" -> changeActivityLevel(scanner, selectedDate);
                case "4" -> changeMethod(scanner);
                case "5" -> compareCalories(selectedDate , dailyLog);
                case "6" -> selectedDate = changeDate(scanner);
                case "7" -> writeProfileToFile();
                case "8" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public void createNewProfile(Scanner scanner) {
        LocalDate today = LocalDate.now();

        System.out.print("Enter gender (Male/Female): ");
        gender = scanner.nextLine();

        System.out.print("Enter height (in cm): ");
        height = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter weight (in kg): ");
        double weight = Double.parseDouble(scanner.nextLine());

        System.out.println("Select activity level:");
        System.out.println("1. Sedentary");
        System.out.println("2. Lightly Active");
        System.out.println("3. Moderately Active");
        System.out.println("4. Very Active");
        System.out.print("Enter your choice: ");
        int level = Integer.parseInt(scanner.nextLine());

        String activity = switch (level) {
            case 1 -> "Sedentary";
            case 2 -> "Lightly Active";
            case 3 -> "Moderately Active";
            case 4 -> "Very Active";
            default -> "Sedentary";
        };

        profiles.put(today, new DailyProfile(age, weight, activity));
        System.out.println("New profile created for today.");
    }

    public void loadProfile() {
        readProfileFromFile();
    }

    public void saveProfile() {
        writeProfileToFile();
    }

    private void viewProfile(LocalDate date) {
        DailyProfile profile = getOrCreateProfile(date);
        System.out.println("Gender: " + gender);
        System.out.println("Height: " + height + " cm");
        System.out.println("Age: " + profile.age + " yrs");
        System.out.println("Weight: " + profile.weight + " kg");
        System.out.println("Activity Level: " + profile.activityLevel);
        System.out.println("Method: " + method);
        System.out.println("Target Calories: " + calculateTargetCalories(date));
    }

    private void updatePersonalInfo(Scanner scanner, LocalDate date) {
        System.out.print("Enter gender (Male/Female): ");
        gender = scanner.nextLine();

        System.out.print("Enter height (cm): ");
        height = Integer.parseInt(scanner.nextLine());

        DailyProfile profile = getOrCreateProfile(date);

        System.out.print("Enter age: ");
        profile.age = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter weight (kg): ");
        profile.weight = Double.parseDouble(scanner.nextLine());

        profiles.put(date, profile);
    }

    private void changeActivityLevel(Scanner scanner, LocalDate date) {
        DailyProfile profile = getOrCreateProfile(date);

        System.out.println("Activity Levels:");
        System.out.println("1. Sedentary");
        System.out.println("2. Lightly Active");
        System.out.println("3. Moderately Active");
        System.out.println("4. Very Active");
        System.out.print("Select activity level: ");
        int level = Integer.parseInt(scanner.nextLine());

        profile.activityLevel = switch (level) {
            case 1 -> "Sedentary";
            case 2 -> "Lightly Active";
            case 3 -> "Moderately Active";
            case 4 -> "Very Active";
            default -> "Sedentary";
        };

        profiles.put(date, profile);
    }

    private void changeMethod(Scanner scanner) {
        System.out.println("1. Harris-Benedict");
        System.out.println("2. Mifflin-St Jeor");
        System.out.print("Choose method: ");
        String methodChoice = scanner.nextLine();
        method = methodChoice.equals("2") ? CalculationMethod.MIFFLIN_ST_JEOR : CalculationMethod.HARRIS_BENEDICT;
    }

    private void compareCalories(LocalDate date, DailyLog dailyLog) {
        double target = calculateTargetCalories(date);
        String dateStr = date.toString();  // Convert LocalDate to String
        double consumed = dailyLog.showTotalCaloriesForDate(dateStr);
        double diff = consumed - target;
    
        System.out.printf("Target: %.2f kcal, Consumed: %.2f kcal, Difference: %.2f kcal (%s)\n",
                target, consumed, Math.abs(diff), diff > 0 ? "Over" : "Available");
    }
    

    private LocalDate changeDate(Scanner scanner) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        return LocalDate.parse(scanner.nextLine());
    }

    private DailyProfile getOrCreateProfile(LocalDate date) {
        return profiles.computeIfAbsent(date, d -> {
            LocalDate prev = d.minusDays(1);
            if (profiles.containsKey(prev)) {
                DailyProfile prevProfile = profiles.get(prev);
                return new DailyProfile(prevProfile.age, prevProfile.weight, prevProfile.activityLevel);
            }
            return new DailyProfile(25, 70.0, "Sedentary");
        });
    }

    private double getActivityMultiplier(String level) {
        return switch (level) {
            case "Sedentary" -> 1.2;
            case "Lightly Active" -> 1.375;
            case "Moderately Active" -> 1.55;
            case "Very Active" -> 1.725;
            default -> 1.2;
        };
    }

    private double calculateTargetCalories(LocalDate date) {
        DailyProfile p = getOrCreateProfile(date);
        double bmr;

        if (method == CalculationMethod.HARRIS_BENEDICT) {
            bmr = (gender.equalsIgnoreCase("Male"))
                    ? 66.47 + (13.75 * p.weight) + (5.003 * height) - (6.755 * p.age)
                    : 655.1 + (9.563 * p.weight) + (1.850 * height) - (4.676 * p.age);
        } else {
            bmr = (gender.equalsIgnoreCase("Male"))
                    ? (10 * p.weight) + (6.25 * height) - (5 * p.age) + 5
                    : (10 * p.weight) + (6.25 * height) - (5 * p.age) - 161;
        }

        return bmr * getActivityMultiplier(p.activityLevel);
    }

    private void readProfileFromFile() {
        File file = new File(PROFILE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            profiles.clear();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    int age = Integer.parseInt(parts[1]);
                    double weight = Double.parseDouble(parts[2]);
                    String activity = parts[3];
                    gender = parts[4];
                    height = Integer.parseInt(parts[5]);
                    profiles.put(date, new DailyProfile(age, weight, activity));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading profile: " + e.getMessage());
        }
    }

    private void writeProfileToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROFILE_FILE))) {
            for (Map.Entry<LocalDate, DailyProfile> entry : profiles.entrySet()) {
                DailyProfile p = entry.getValue();
                writer.printf("%s,%d,%.1f,%s,%s,%d%n",
                        entry.getKey(), p.age, p.weight, p.activityLevel, gender, height);
            }
            System.out.println("Profile saved.");
        } catch (IOException e) {
            System.out.println("Error saving profile: " + e.getMessage());
        }
    }
}
