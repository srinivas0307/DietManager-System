package com.dietmanager.food;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BasicFood extends Food {
    private final double calories;
    private final Map<String, Double> nutrition; // Additional nutrients: protein, fat, etc.

    public BasicFood(String name, List<String> keywords, double calories, Map<String, Double> nutrition) {
        super(name, keywords);
        this.calories = calories;
        this.nutrition = nutrition;
    }

    @Override
    public double getCalories() {
        return calories;
    }

    public Map<String, Double> getNutrition() {
        return nutrition;
    }

    @Override
    public String serialize() {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("BASIC");
        joiner.add(name);
        joiner.add(String.join(",", keywords));
        joiner.add(String.valueOf(calories));

        if (nutrition != null && !nutrition.isEmpty()) {
            StringJoiner nutriJoiner = new StringJoiner(",");
            for (Map.Entry<String, Double> entry : nutrition.entrySet()) {
                nutriJoiner.add(entry.getKey() + "=" + entry.getValue());
            }
            joiner.add(nutriJoiner.toString());
        } else {
            joiner.add(""); // blank if no nutrition added
        }

        return joiner.toString();
    }
}
