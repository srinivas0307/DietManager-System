package com.dietmanager.calculator;

public class ConcreteCalorieCalculator2 implements CalorieCalculator {
    @Override
    public double calculateCalories(String gender, int age, double height, double weight, String activityLevel) {
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
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
