package tech.demoproject.android_otp_vertification;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class nutritionMonitorActivity extends AppCompatActivity {
    private EditText mealName, calories, protein, fat, carbs;
    private Button addMealButton;
    private TextView dailySummary, weeklySummary;

    private FirebaseFirestore firestoreDb;
    private FirebaseAuth mAuth;
    private String TAG = "NutritionMonitorActivity";

    // Use a Map to store daily and weekly totals
    private Map<String, Integer> dailyTotals = new HashMap<>();
    private Map<String, Integer> weeklyTotals = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_monitor);

        try {
            // Initialize views
            mealName = findViewById(R.id.meal_name);
            calories = findViewById(R.id.calories);
            protein = findViewById(R.id.protein);
            fat = findViewById(R.id.fat);
            carbs = findViewById(R.id.carbs);
            addMealButton = findViewById(R.id.add_meal_button);
            dailySummary = findViewById(R.id.daily_summary);
            weeklySummary = findViewById(R.id.weekly_summary);

            // Initialize Firebase
            firestoreDb = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            // Check if user is logged in
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity if the user is not logged in
                return;
            }

            // Set the add meal button click listener
            addMealButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMeal();
                }
            });

            // Update summaries on activity start
            updateSummaries();
        } catch (Exception e) {
            Log.e(TAG, "Error during onCreate: ", e);
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMeal() {
        try {
            // Log the start of the addMeal function
            Log.d(TAG, "Starting to add a meal");

            // Get input from EditText fields
            String mealNameStr = mealName.getText().toString().trim();
            String caloriesStr = calories.getText().toString().trim();
            String proteinStr = protein.getText().toString().trim();
            String fatStr = fat.getText().toString().trim();
            String carbsStr = carbs.getText().toString().trim();

            // Validate input fields
            if (mealNameStr.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() ||
                    fatStr.isEmpty() || carbsStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int caloriesInt, proteinInt, fatInt, carbsInt;
            try {
                caloriesInt = Integer.parseInt(caloriesStr);
                proteinInt = Integer.parseInt(proteinStr);
                fatInt = Integer.parseInt(fatStr);
                carbsInt = Integer.parseInt(carbsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                return; // Stop execution if invalid input
            }

            // Create a Map to store meal data
            Map<String, Object> mealData = new HashMap<>();
            mealData.put("mealName", mealNameStr);
            mealData.put("calories", caloriesInt);
            mealData.put("protein", proteinInt);
            mealData.put("fat", fatInt);
            mealData.put("carbs", carbsInt);

            // Write meal data to Firestore
            firestoreDb.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("meals")
                    .add(mealData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Meal added successfully to Firestore: " + documentReference.getId());
                            Toast.makeText(nutritionMonitorActivity.this, "Meal added successfully", Toast.LENGTH_SHORT).show();

                            // Update daily and weekly totals
                            updateTotals(caloriesInt, proteinInt, fatInt, carbsInt);

                            // Clear input fields after adding a meal
                            mealName.setText("");
                            calories.setText("");
                            protein.setText("");
                            fat.setText("");
                            carbs.setText("");

                            // Update the daily and weekly summaries
                            updateSummaries();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error adding meal to Firestore: ", e);
                            Toast.makeText(nutritionMonitorActivity.this, "Failed to add meal", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in addMeal: ", e);
            Toast.makeText(this, "An error occurred while adding the meal.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotals(int calories, int protein, int fat, int carbs) {
        // Update daily totals
        dailyTotals.put("calories", dailyTotals.containsKey("calories") ? dailyTotals.get("calories") + calories : calories);
        dailyTotals.put("protein", dailyTotals.containsKey("protein") ? dailyTotals.get("protein") + protein : protein);
        dailyTotals.put("fat", dailyTotals.containsKey("fat") ? dailyTotals.get("fat") + fat : fat);
        dailyTotals.put("carbs", dailyTotals.containsKey("carbs") ? dailyTotals.get("carbs") + carbs : carbs);

        // Update weekly totals
        weeklyTotals.put("calories", weeklyTotals.containsKey("calories") ? weeklyTotals.get("calories") + calories : calories);
        weeklyTotals.put("protein", weeklyTotals.containsKey("protein") ? weeklyTotals.get("protein") + protein : protein);
        weeklyTotals.put("fat", weeklyTotals.containsKey("fat") ? weeklyTotals.get("fat") + fat : fat);
        weeklyTotals.put("carbs", weeklyTotals.containsKey("carbs") ? weeklyTotals.get("carbs") + carbs : carbs);
    }

    private void updateSummaries() {
        try {
            // Update daily summary text
            String dailySummaryText = "Daily Summary:\n" +
                    "Calories: " + (dailyTotals.containsKey("calories") ? dailyTotals.get("calories") : 0) + " kcal\n" +
                    "Protein: " + (dailyTotals.containsKey("protein") ? dailyTotals.get("protein") : 0) + " g\n" +
                    "Fat: " + (dailyTotals.containsKey("fat") ? dailyTotals.get("fat") : 0) + " g\n" +
                    "Carbs: " + (dailyTotals.containsKey("carbs") ? dailyTotals.get("carbs") : 0) + " g";

            dailySummary.setText(dailySummaryText);

            // Update weekly summary text
            String weeklySummaryText = "Weekly Summary:\n" +
                    "Calories: " + (weeklyTotals.containsKey("calories") ? weeklyTotals.get("calories") : 0) + " kcal\n" +
                    "Protein: " + (weeklyTotals.containsKey("protein") ? weeklyTotals.get("protein") : 0) + " g\n" +
                    "Fat: " + (weeklyTotals.containsKey("fat") ? weeklyTotals.get("fat") : 0) + " g\n" +
                    "Carbs: " + (weeklyTotals.containsKey("carbs") ? weeklyTotals.get("carbs") : 0) + " g";

            weeklySummary.setText(weeklySummaryText);

        } catch (Exception e) {
            Log.e(TAG, "Error updating summaries: ", e);
            Toast.makeText(this, "An error occurred while updating summaries.", Toast.LENGTH_SHORT).show();
        }
    }
}
