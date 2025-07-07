package tech.demoproject.android_otp_vertification;

public class Meal {
    public String name;
    public int calories;
    public int protein;
    public int fat;
    public int carbs;

    // Default constructor required for calls to DataSnapshot.getValue(Meal.class)
    public Meal() {}

    // Constructor with parameters
    public Meal(String name, int calories, int protein, int fat, int carbs) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    public int getCarbs() {
        return carbs;
    }
}
