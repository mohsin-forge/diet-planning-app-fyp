package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class addGoal extends AppCompatActivity {
    private EditText editTextCalories, editTextCarbs, editTextProteins, editTextFats;
    private CheckBox checkBoxVegetarian, checkBoxGlutenFree;
    private Button addGoalButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private TextView textViewSavedCalories, textViewSavedCarbs, textViewSavedProteins, textViewSavedFats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize Views
        addGoalButton = findViewById(R.id.buttonAddGoal);
        editTextCalories = findViewById(R.id.idCalories);
        editTextCarbs = findViewById(R.id.idCarbs);
        editTextProteins = findViewById(R.id.idProteins);
        editTextFats = findViewById(R.id.idFats);
        checkBoxVegetarian = findViewById(R.id.checkboxVegetarian);
        checkBoxGlutenFree = findViewById(R.id.checkboxGlutenFree);
        textViewSavedCalories = findViewById(R.id.textViewSavedCalories);
        textViewSavedCarbs = findViewById(R.id.textViewSavedCarbs);
        textViewSavedProteins = findViewById(R.id.textViewSavedProteins);
        textViewSavedFats = findViewById(R.id.textViewSavedFats);

        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDietaryGoals();
            }
        });

        loadSavedGoals();
    }

    private void saveDietaryGoals() {
        // Get current user ID
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        // Retrieve user input
        String calories = editTextCalories.getText().toString().trim();
        String carbs = editTextCarbs.getText().toString().trim();
        String proteins = editTextProteins.getText().toString().trim();
        String fats = editTextFats.getText().toString().trim();
        boolean isVegetarian = checkBoxVegetarian.isChecked();
        boolean isGlutenFree = checkBoxGlutenFree.isChecked();

        // Prepare data for saving
        Map<String, Object> dietaryGoals = new HashMap<>();
        dietaryGoals.put("calories", calories);
        dietaryGoals.put("carbs", carbs);
        dietaryGoals.put("proteins", proteins);
        dietaryGoals.put("fats", fats);
        dietaryGoals.put("vegetarian", isVegetarian);
        dietaryGoals.put("glutenFree", isGlutenFree);

        // Save to Firestore under the user's UID
        firestore.collection("users").document(userId)
                .set(dietaryGoals)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(addGoal.this, "Goals saved successfully!", Toast.LENGTH_SHORT).show();
                            loadSavedGoals();
                        } else {
                            Toast.makeText(addGoal.this, "Failed to save goals.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadSavedGoals() {
        // Get current user ID
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        // Load saved goals from Firestore under the user's UID
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            textViewSavedCalories.setText("Calories: " + document.getString("calories"));
                            textViewSavedCarbs.setText("Carbs: " + document.getString("carbs"));
                            textViewSavedProteins.setText("Proteins: " + document.getString("proteins"));
                            textViewSavedFats.setText("Fats: " + document.getString("fats"));
                        } else {
                            Toast.makeText(addGoal.this, "Failed to load goals.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
