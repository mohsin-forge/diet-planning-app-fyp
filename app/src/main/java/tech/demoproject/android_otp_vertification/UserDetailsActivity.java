package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    private EditText editTextHeight, editTextWeight, editTextAge, editTextCalorieGoal, editTextGoal, editTextOtherRequirements;
    private Spinner spinnerGender;
    private Button buttonSaveDetails;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        editTextCalorieGoal = findViewById(R.id.editTextCalorieGoal);
        editTextGoal = findViewById(R.id.editTextGoal);
        editTextOtherRequirements = findViewById(R.id.editTextOtherRequirements);
        buttonSaveDetails = findViewById(R.id.buttonSaveDetails);

        // Set up button click listener
        buttonSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
            }
        });
    }

    private void saveUserDetails() {
        String height = editTextHeight.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String calorieGoal = editTextCalorieGoal.getText().toString().trim();
        String goal = editTextGoal.getText().toString().trim();
        String otherRequirements = editTextOtherRequirements.getText().toString().trim();

        if (TextUtils.isEmpty(height) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(age) || TextUtils.isEmpty(calorieGoal) || TextUtils.isEmpty(goal)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is authenticated
        String userId = auth.getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a map to store user details
        Map<String, Object> userDetailsMap = new HashMap<>();
        userDetailsMap.put("height", Integer.parseInt(height));
        userDetailsMap.put("weight", Integer.parseInt(weight));
        userDetailsMap.put("age", Integer.parseInt(age));
        userDetailsMap.put("gender", gender);
        userDetailsMap.put("calorieGoal", Integer.parseInt(calorieGoal));
        userDetailsMap.put("goal", goal);
        userDetailsMap.put("otherRequirements", otherRequirements);

        // Save user details to Firestore
        firestore.collection("users").document(userId)
                .set(userDetailsMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserDetailsActivity.this, "Details saved successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to MainActivity
                            Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Finish this activity to remove it from the back stack
                        } else {
                            Toast.makeText(UserDetailsActivity.this, "Failed to save details", Toast.LENGTH_SHORT).show();
                            Log.d("UserDetailsActivity", "Error saving details: ", task.getException());
                        }
                    }
                });
    }
}
