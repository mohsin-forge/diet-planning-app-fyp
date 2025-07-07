package tech.demoproject.android_otp_vertification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mealPlanActivity extends AppCompatActivity {
    private Spinner spinnerDay;
    private EditText etBreakfast, etLunch, etDinner, etSnacks;
    private Button btnSave;
    private ListView lvMealPlans;
    private ArrayAdapter<String> mealPlanAdapter;
    private List<String> mealPlanList;
    private Map<String, String> mealPlans;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        // Initialize Views
        spinnerDay = findViewById(R.id.spinnerDay);
        etBreakfast = findViewById(R.id.etBreakfast);
        etLunch = findViewById(R.id.etLunch);
        etDinner = findViewById(R.id.etDinner);
        etSnacks = findViewById(R.id.etSnacks);
        btnSave = findViewById(R.id.btnSave);
        lvMealPlans = findViewById(R.id.lvMealPlans);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("mealPlans");

        mealPlans = new HashMap<>();
        mealPlanList = new ArrayList<>();
        mealPlanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mealPlanList);
        lvMealPlans.setAdapter(mealPlanAdapter);

        // Set the Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMealPlan();
            }
        });

        // Load existing meal plans from the database
        loadMealPlans();
    }

    private void saveMealPlan() {
        String day = spinnerDay.getSelectedItem().toString();
        String breakfast = etBreakfast.getText().toString();
        String lunch = etLunch.getText().toString();
        String dinner = etDinner.getText().toString();
        String snacks = etSnacks.getText().toString();

        String mealPlan = "Breakfast: " + breakfast + "\nLunch: " + lunch + "\nDinner: " + dinner + "\nSnacks: " + snacks;

        // Save the meal plan to the Firebase database
        mDatabase.child(day).setValue(mealPlan).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(mealPlanActivity.this, "Meal plan saved successfully", Toast.LENGTH_SHORT).show();
                loadMealPlans(); // Reload meal plans after saving
            } else {
                Toast.makeText(mealPlanActivity.this, "Failed to save meal plan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMealPlans() {
        // Fetch meal plans from the Firebase database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealPlans.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String day = child.getKey();
                    String mealPlan = child.getValue(String.class);
                    mealPlans.put(day, mealPlan);
                }
                updateMealPlanList(); // Update the ListView with the retrieved data
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mealPlanActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMealPlanList() {
        mealPlanList.clear();
        for (String day : mealPlans.keySet()) {
            mealPlanList.add(day + "\n" + mealPlans.get(day));
        }
        mealPlanAdapter.notifyDataSetChanged();
    }
}
