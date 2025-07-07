package tech.demoproject.android_otp_vertification;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class RecipeData {

    private DatabaseReference databaseReference;

    public RecipeData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public void addSampleRecipes() {
        // Add a sample breakfast recipe
        Recipe breakfastRecipe = new Recipe(
                databaseReference.push().getKey(),
                "Pancakes",
                "Breakfast",
                Arrays.asList("Flour", "Milk", "Eggs", "Sugar", "Butter"),
                "1. Mix ingredients. 2. Cook on a griddle.",
                15,
                "200 calories per serving",
                "American",
                Arrays.asList("Vegetarian")
        );
        databaseReference.child(breakfastRecipe.getId()).setValue(breakfastRecipe);

        Recipe lunchRecipe = new Recipe(
                databaseReference.push().getKey(),
                "Caesar Salad",
                "Lunch",
                Arrays.asList("Romaine Lettuce", "Croutons", "Parmesan Cheese", "Caesar Dressing"),
                "1. Mix lettuce with dressing. 2. Add croutons and cheese.",
                10,
                "150 calories per serving",
                "Italian",
                Arrays.asList("Vegetarian")
        );
        databaseReference.child(lunchRecipe.getId()).setValue(lunchRecipe);

        Recipe dinnerRecipe = new Recipe(
                databaseReference.push().getKey(),
                "Stir-fried Tofu with Vegetables",
                "Dinner",
                Arrays.asList("Tofu", "Broccoli", "Carrots", "Soy Sauce", "Garlic"),
                "1. Stir-fry tofu and vegetables. 2. Add soy sauce and garlic.",
                20,
                "250 calories per serving",
                "Chinese",
                Arrays.asList("Vegan", "Gluten-Free")
        );
        databaseReference.child(dinnerRecipe.getId()).setValue(dinnerRecipe);

        Recipe dinnerRecipeItalian = new Recipe(
                databaseReference.push().getKey(),
                "Spaghetti Aglio e Olio",
                "Dinner",
                Arrays.asList("Spaghetti", "Garlic", "Olive Oil", "Red Pepper Flakes", "Parsley"),
                "1. Cook spaghetti. 2. Sauté garlic in olive oil. 3. Toss spaghetti with garlic oil, red pepper flakes, and parsley.",
                15,
                "400 calories per serving",
                "Italian",
                Arrays.asList("Vegetarian")
        );
        databaseReference.child(dinnerRecipeItalian.getId()).setValue(dinnerRecipeItalian);

        Recipe dinnerRecipeChinese = new Recipe(
                databaseReference.push().getKey(),
                "Kung Pao Chicken",
                "Dinner",
                Arrays.asList("Chicken Breast", "Peanuts", "Bell Peppers", "Soy Sauce", "Garlic", "Ginger", "Chili Peppers"),
                "1. Stir-fry chicken with garlic and ginger. 2. Add vegetables, soy sauce, and peanuts. 3. Stir in chili peppers and serve.",
                30,
                "350 calories per serving",
                "Chinese",
                Arrays.asList("Spicy")
        );
        databaseReference.child(dinnerRecipeChinese.getId()).setValue(dinnerRecipeChinese);



    }
}

