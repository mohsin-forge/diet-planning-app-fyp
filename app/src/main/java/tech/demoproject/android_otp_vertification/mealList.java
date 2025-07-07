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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mealList extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ListView recipesListView;

    private TextView total;
    private Button addToFirestoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fetchAndDisplayRecipes();

        total =findViewById(R.id.totalnutrition);
        // Initialize UI elements
        recipesListView = findViewById(R.id.recipesListView);
        addToFirestoreButton = findViewById(R.id.addToFirestoreButton);

        // Fetch and display recipes
        displayRecipes();
        addToFirestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSelectedRecipesToFirestore();
            }
        });
        // Set up button click listener

    }

    private void displayRecipes() {
        // Original recipe names
        String[] recipeNames = {"Biryani", "Kofta", "Nihari", "Haleem", "Aloo Keema", "Chapli Kebab", "Saag", "Pakoras"};

        // Populate the ListView with recipe names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, recipeNames);
        recipesListView.setAdapter(adapter);
    }

    private void fetchAndDisplayRecipes() {
        // Check if the user is authenticated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("number", "0");

        // Create a reference to the recipes collection
        CollectionReference recipesCollection = firestore.collection("recipes");

        // Fetch recipes from Firestore
        recipesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> recipeNames = new ArrayList<>();
                    // Log.d("documaanet: "+String.valueOf(task.getResult().size()));
                    // Iterate through the fetched documents and get recipe names
                    for (DocumentSnapshot document : task.getResult()) {
                        String recipeName = document.getString("name");
                        if (recipeName != null) {
                            recipeNames.add(recipeName);
                        }
                    }

                    // Populate the ListView with fetched recipe names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, recipeNames);
                    recipesListView.setAdapter(adapter);
                } else {
                    // Handle the failure to fetch recipes
                }
            }
        });
    }

    private void addSelectedRecipesToFirestore() {
        // Check if the user is authenticated
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sharedPreferences.getString("number", "0");

        // Create a reference to the user_meals collection
        CollectionReference userMealsCollection = firestore.collection("user_meals");

        // Get selected recipe names from the ListView
        List<String> selectedRecipes = new ArrayList<>();
        for (int i = 0; i < recipesListView.getCount(); i++) {
            if (recipesListView.isItemChecked(i)) {
                selectedRecipes.add(((ArrayAdapter<String>) recipesListView.getAdapter()).getItem(i));
            }
        }

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
                            Toast.makeText(getApplicationContext(), "Data added successfully", Toast.LENGTH_SHORT).show();
                            // Selected meals added successfully
                        } else {
                            Toast.makeText(getApplicationContext(), "Data added failed", Toast.LENGTH_SHORT).show();
                            // Handle the failure to add selected meals
                        }
                    }
                });
    }

}