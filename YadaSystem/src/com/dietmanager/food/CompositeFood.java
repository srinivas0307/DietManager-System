package com.dietmanager.food;

import java.util.*;

public class CompositeFood extends Food {
    private final Map<Food, Double> components;

    public CompositeFood(String name, List<String> keywords, Map<Food, Double> components) {
        super(name, keywords);
        this.components = components;
    }

    @Override
    public double getCalories() {
        return components.entrySet().stream()
                .mapToDouble(e -> e.getKey().getCalories() * e.getValue())
                .sum();
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder("COMPOSITE;");
        sb.append(name).append(";")
                .append(String.join(",", keywords)).append(";");

        List<String> parts = new ArrayList<>();
        for (Map.Entry<Food, Double> entry : components.entrySet()) {
            parts.add(entry.getKey().getName() + ":" + entry.getValue());
        }
        sb.append(String.join("|", parts));
        return sb.toString();
    }

    public static Map<Food, Double> parseComponents(String componentString, List<Food> existingFoods) {
        Map<Food, Double> map = new HashMap<>();
        String[] entries = componentString.split("\\|");
        for (String entry : entries) {
            String[] pair = entry.split(":");
            String name = pair[0];
            double servings = Double.parseDouble(pair[1]);

            for (Food food : existingFoods) {
                if (food.getName().equalsIgnoreCase(name)) {
                    map.put(food, servings);
                    break;
                }
            }
        }
        return map;
    }
}
