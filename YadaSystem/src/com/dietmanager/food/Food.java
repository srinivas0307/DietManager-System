package com.dietmanager.food;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Food {
    protected String name;
    public List<String> keywords;

    public Food(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
    }

    public abstract double getCalories();
    public abstract String serialize();

    public String getName() {
        return name;
    }

    public static Food deserialize(String line, List<Food> existingFoods) {
        String[] parts = line.split(";");
        if (parts[0].equals("BASIC")) {
            String name = parts[1];
            List<String> keywords = List.of(parts[2].split(","));
            double calories = Double.parseDouble(parts[3]);

            Map<String, Double> nutrition = new HashMap<>();
            if (parts.length > 4 && !parts[4].isEmpty()) {
                String[] nutrients = parts[4].split(",");
                for (String nutrient : nutrients) {
                    int eqIndex = nutrient.indexOf('=');
                    if (eqIndex > 0) {
                        String key = nutrient.substring(0, eqIndex);
                        double value = Double.parseDouble(nutrient.substring(eqIndex + 1));
                        nutrition.put(key, value);
                    }
                }
            }

            return new BasicFood(name, keywords, calories, nutrition);
        } else if (parts[0].equals("COMPOSITE")) {
            String name = parts[1];
            List<String> keywords = List.of(parts[2].split(","));
            Map<Food, Double> components = CompositeFood.parseComponents(parts[3], existingFoods);
            return new CompositeFood(name, keywords, components);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %.2f cal", name, String.join(", ", keywords), getCalories());
    }
}
