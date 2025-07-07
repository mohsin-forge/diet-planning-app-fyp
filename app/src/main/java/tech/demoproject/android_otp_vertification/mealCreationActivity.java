package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mealCreationActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ListView recipesListView;
    private TextView totalNutrition;
    private Button addToFirestoreButton;
    private Map<String, Map<String, Object>> recipesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiw_recipes);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        recipesListView = findViewById(R.id.recipesListView);
        totalNutrition = findViewById(R.id.totalnutrition);
        addToFirestoreButton = findViewById(R.id.addToFirestoreButton);

        recipesData = new HashMap<>();

        // Fetch and display recipes
        fetchAndDisplayRecipes();

        // Set up button click listener
        addToFirestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSelectedRecipesToFirestore();
            }
        });
    }

    private void fetchAndDisplayRecipes() {
        // Check if the user is authenticated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("number", "0");

        if (userId.equals("0")) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to the recipes collection
        CollectionReference recipesCollection = firestore.collection("recipes");

        // Fetch recipes from Firestore
        recipesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> recipeNames = new ArrayList<>();
                    // Iterate through the fetched documents and get recipe names
                    for (DocumentSnapshot document : task.getResult()) {
                        String recipeName = document.getString("name");
                        if (recipeName != null) {
                            recipeNames.add(recipeName);
                            Map<String, Object> nutritionData = new HashMap<>();
                            if (document.contains("calories")) {
                                nutritionData.put("calories", document.getLong("calories"));
                            }
                            if (document.contains("protein")) {
                                nutritionData.put("protein", document.getLong("protein"));
                            }
                            if (document.contains("carbs")) {
                                nutritionData.put("carbs", document.getLong("carbs"));
                            }
                            if (document.contains("fat")) {
                                nutritionData.put("fat", document.getLong("fat"));
                            }
                            recipesData.put(recipeName, nutritionData);
                        }
                    }

                    // Populate the ListView with fetched recipe names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mealCreationActivity.this, android.R.layout.simple_list_item_multiple_choice, recipeNames);
                    recipesListView.setAdapter(adapter);
                } else {
                    Toast.makeText(mealCreationActivity.this, "Failed to fetch recipes", Toast.LENGTH_SHORT).show();
                    Log.d("mealCreationActivity", "Error fetching recipes: ", task.getException());
                }
            }
        });
    }

    private void addSelectedRecipesToFirestore() {
        // Check if the user is authenticated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("number", "0");

        if (userId.equals("0")) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to the user_meals collection
        CollectionReference userMealsCollection = firestore.collection("user_meals");

        // Get selected recipe names from the ListView
        List<String> selectedRecipes = new ArrayList<>();
        for (int i = 0; i < recipesListView.getCount(); i++) {
            if (recipesListView.isItemChecked(i)) {
                selectedRecipes.add(((ArrayAdapter<String>) recipesListView.getAdapter()).getItem(i));
            }
        }

        if (selectedRecipes.isEmpty()) {
            Toast.makeText(this, "No recipes selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate total nutrition
        calculateTotalNutrition();

        // Create a map to store the selected meals against the user ID
        Map<String, Object> userMealsMap = new HashMap<>();
        userMealsMap.put("selected_meals", selectedRecipes);

        // Add or update the document in Firestore
        userMealsCollection.document(userId)
                .set(userMealsMap, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mealCreationActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mealCreationActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                            Log.d("mealCreationActivity", "Error adding data: ", task.getException());
                        }
                    }
                });
    }

    private void calculateTotalNutrition() {
        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFat = 0;

        // Iterate over all items in the ListView
        for (int i = 0; i < recipesListView.getCount(); i++) {
            if (recipesListView.isItemChecked(i)) {
                String selectedRecipe = recipesListView.getItemAtPosition(i).toString();
                Map<String, Object> nutritionData = recipesData.get(selectedRecipe);

                if (nutritionData != null) {
                    // Safely extract nutrition data
                    totalCalories += (nutritionData.get("calories") != null) ? ((Long) nutritionData.get("calories")).intValue() : 0;
                    totalProtein += (nutritionData.get("protein") != null) ? ((Long) nutritionData.get("protein")).intValue() : 0;
                    totalCarbs += (nutritionData.get("carbs") != null) ? ((Long) nutritionData.get("carbs")).intValue() : 0;
                    totalFat += (nutritionData.get("fat") != null) ? ((Long) nutritionData.get("fat")).intValue() : 0;
                }
            }
        }

        // Display the total nutrition in the TextView
        String nutritionText = "Total Calories: " + totalCalories + " kcal\n" +
                "Protein: " + totalProtein + " g\n" +
                "Carbs: " + totalCarbs + " g\n" +
                "Fat: " + totalFat + " g";
        totalNutrition.setText(nutritionText);
    }
}
