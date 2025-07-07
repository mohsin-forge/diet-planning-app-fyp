package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class goalAdjustmentActivity extends AppCompatActivity {
    private TextView tvCurrentGoal, tvSuggestions;
    private EditText etNewGoal;
    private Button btnAdjustGoal;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_adjustment);

        // Initialize the views
        tvCurrentGoal = findViewById(R.id.tvCurrentGoal);
        tvSuggestions = findViewById(R.id.tvSuggestions);
        etNewGoal = findViewById(R.id.etNewGoal);
        btnAdjustGoal = findViewById(R.id.btnAdjustGoal);

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        // Fetch and display the current goal
        fetchCurrentGoal();

        btnAdjustGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustGoal();
            }
        });
    }

    private void fetchCurrentGoal() {
        mDatabase.child("goal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentGoal = dataSnapshot.getValue(String.class);
                    if (currentGoal != null) {
                        tvCurrentGoal.setText("Current Goal: " + currentGoal + " Calories");
                        etNewGoal.setText(currentGoal); // Pre-fill the EditText with current goal
                        // Provide suggestions based on the current goal
                        String suggestions = getSuggestions(Integer.parseInt(currentGoal));
                        tvSuggestions.setText("Suggestions: " + suggestions);
                    }
                } else {
                    tvCurrentGoal.setText("Current Goal: Not set");
                    tvSuggestions.setText("Suggestions: Set a goal to get suggestions.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(goalAdjustmentActivity.this, "Failed to load current goal.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adjustGoal() {
        String newGoalStr = etNewGoal.getText().toString();
        if (TextUtils.isEmpty(newGoalStr)) {
            Toast.makeText(this, "Please enter a new goal", Toast.LENGTH_SHORT).show();
            return;
        }

        int newGoal;
        try {
            newGoal = Integer.parseInt(newGoalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid goal format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update goal in Firebase Realtime Database
        mDatabase.child("goal").setValue(String.valueOf(newGoal))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Goal updated!", Toast.LENGTH_SHORT).show();
                    tvCurrentGoal.setText("Current Goal: " + newGoal + " Calories");

                    // Provide suggestions based on the new goal
                    String suggestions = getSuggestions(newGoal);
                    tvSuggestions.setText("Suggestions: " + suggestions);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update goal", Toast.LENGTH_SHORT).show();
                });
    }

    private String getSuggestions(int goal) {
        if (goal < 1500) {
            return "Increase your calorie intake. Include more protein and healthy fats.";
        } else if (goal > 2500) {
            return "Reduce your calorie intake. Avoid high-calorie snacks and sugary drinks.";
        } else {
            return "Maintain a balanced diet. Keep up the good work!";
        }
    }
}
