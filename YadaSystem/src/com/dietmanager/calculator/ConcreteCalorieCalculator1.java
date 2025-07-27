package com.dietmanager.calculator;

public class ConcreteCalorieCalculator1 implements CalorieCalculator {
    @Override
    public double calculateCalories(String gender, int age, double height, double weight, String activityLevel) {
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = 66.5 + (13.75 * weight) + (5.003 * height) - (6.75 * age);
        } else {
            bmr = 655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age);
        }
        return applyActivityMultiplier(bmr, activityLevel);
    }

    private double applyActivityMultiplier(double bmr, String activityLevel) {
        return switch (activityLevel.toLowerCase()) {
            case "light" -> bmr * 1.375;
            case "moderate" -> bmr * 1.55;
            case "active" -> bmr * 1.725;
            case "very active" -> bmr * 1.9;
            default -> bmr * 1.2; // sedentary
        };
    }
}
