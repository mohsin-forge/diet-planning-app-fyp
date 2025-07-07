package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dashboard extends AppCompatActivity {
    private Button addGoal,UPdateGoal,mealList;
    private FirebaseFirestore firestore;
    List<String> name;
    double totalnut=0;
    public TextView total;
    private TextView editTextCalories, editTextCarbs, editTextProteins, editTextFats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firestore = FirebaseFirestore.getInstance();
        addGoal=findViewById(R.id.buttonAddGoal);
        UPdateGoal=findViewById(R.id.buttonUpdateGoal);

        total=findViewById(R.id.totalnutrition);
     //   addRecipesToFirestore();
        editTextCalories = findViewById(R.id.idCalaoriesD);
        mealList=findViewById(R.id.selectMeal);

        editTextCarbs = findViewById(R.id.idCarbsD);
        editTextProteins = findViewById(R.id.idProteinD);
        editTextFats = findViewById(R.id.idFatsD);

        fetchData();
        mealList();

        mealList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(dashboard.this, mealList.class);
                startActivity(i);
            }
        });
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(dashboard.this, addGoal.class);
                startActivity(i);
            }
        });


        UPdateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(dashboard.this, update_goal.class);
                startActivity(i);

            }
        });
    }


    void mealList() {
        if (true) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String userId = sharedPreferences.getString("number", "0");


            // Create a reference to the user_meals collection
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("user_meals").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();

                            // Check if the document exists
                            if (document.exists()) {
                                // Retrieve the data as a Map
                                Map<String, Object> userMealsData = document.getData();

                                // Check if userMealsData is not null and contains selectedMeals
                                if (userMealsData != null && userMealsData.containsKey("selected_meals")) {
                                    List<String> selectedMeals = (List<String>) userMealsData.get("selected_meals");
                                    name=selectedMeals;
                                    fetchAndSumNutrition(name);
                                    // Now you have the list of selected meals for the user

                                }
                            } else {
                                Log.d("selected Meal: ","User has no selected meals");
                                // Handle the case where the document doesn't exist
                                System.out.println("User has no selected meals");
                            }
                        } else {
                            Log.d("selected Meal: ","Failed to fetch user meals");
                            // Handle the failure to get the document
                            System.out.println("Failed to fetch user meals");
                        }
                    });

        }
    }

    public void fetchAndSumNutrition(List<String> recipeNames) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        // Iterate through the list of recipe names
        for (String recipeName : recipeNames) {
            firestore.collection("recipes").document(recipeName.toLowerCase().replace(" ", "_"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();

                                // Retrieve nutritional values from Firestore document
                                double calories = document.getDouble("calories");
                                double carbs = document.getDouble("carbs");
                                double proteins = document.getDouble("proteins");
                                double fats = document.getDouble("fats");
                                totalnut+= calories+carbs+proteins+fats;
                                Log.d("total",String.valueOf(totalnut));
                                total.setText(String.valueOf(totalnut));
                                // Notify listener on completion of fetching values for one recipe

                            } else {
                                // Handle the failure to fetch nutritional information for a recipe
                              }
                        }
                    });


        }


    }

    void fetchData(){
       SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String id = sharedPreferences.getString("number", "0");

        // Retrieve data from Firestore
        firestore.collection("dietary_goals")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Data found, populate EditText fields
                                editTextCalories.setText(document.getString("calories"));
                                editTextCarbs.setText(document.getString("carbs"));
                                editTextProteins.setText(document.getString("proteins"));
                                editTextFats.setText(document.getString("fats"));
                             //   Toast.makeText(getApplicationContext(), "Dietary goals loaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // No data found
                              //  Toast.makeText(getApplicationContext(), "No dietary goals found for the user", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Failed to retrieve data
                            //Toast.makeText(getApplicationContext(), "Failed to fetch dietary goals", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void addRecipesToFirestore() {
//        // Check if the user is authenticated
//
//
//        // Get the current user's ID
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String userId = sharedPreferences.getString("number", "0");
//
//        // Create a reference to the recipes collection
//        CollectionReference recipesCollection = firestore.collection("recipes");
//
//        // Original recipe names
//        String[] recipeNames = {"Biryani", "Kofta", "Nihari", "Haleem", "Aloo Keema", "Chapli Kebab", "Saag", "Pakoras"};
//
//        // Add 8 different recipes to Firestore
//        for (int i = 0; i < recipeNames.length; i++) {
//            // Define the recipe data
//            Map<String, Object> recipeData = new HashMap<>();
//            recipeData.put("name", recipeNames[i]);
//            recipeData.put("type", "Pakistani");
//            recipeData.put("calories", 500 + i * 50);
//            recipeData.put("carbs", 50 + i * 5);
//            recipeData.put("proteins", 20 + i * 2);
//            recipeData.put("fats", 25 + i * 3);
//
//            // Add the recipe to Firestore
//            recipesCollection.document(recipeNames[i].toLowerCase().replace(" ", "_"))
//                    .set(recipeData, SetOptions.merge())
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                // Recipe added successfully
//                            } else {
//                                // Handle the failure to add the recipe
//                            }
//                        }
//                    });
//        }
//    }
}