package com.dietmanager.calculator;

public interface CalorieCalculator {
    double calculateCalories(String gender, int age, double height, double weight, String activityLevel);
}
