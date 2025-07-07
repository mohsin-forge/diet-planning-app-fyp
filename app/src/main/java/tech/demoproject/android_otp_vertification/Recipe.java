package tech.demoproject.android_otp_vertification;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String mealType;
    private List<String> ingredients;
    private String preparationSteps;
    private int cookingTime;
    private String nutritionalContent;
    private String cuisine;
    private List<String> dietaryPreferences;

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String id, String name, String mealType, List<String> ingredients, String preparationSteps, int cookingTime, String nutritionalContent, String cuisine, List<String> dietaryPreferences) {
        this.id = id;
        this.name = name;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.preparationSteps = preparationSteps;
        this.cookingTime = cookingTime;
        this.nutritionalContent = nutritionalContent;
        this.cuisine = cuisine;
        this.dietaryPreferences = dietaryPreferences;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMealType() {
        return mealType;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getPreparationSteps() {
        return preparationSteps;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public String getNutritionalContent() {
        return nutritionalContent;
    }

    public String getCuisine() {
        return cuisine;
    }

    public List<String> getDietaryPreferences() {
        return dietaryPreferences;
    }

    @Override
    public String toString() {
        return name;
    }
}
