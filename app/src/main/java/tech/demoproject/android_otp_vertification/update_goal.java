package tech.demoproject.android_otp_vertification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class update_goal extends AppCompatActivity {

    private EditText editTextCalories, editTextCarbs, editTextProteins, editTextFats;
    private Button addGoal;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_goal);

        firestore = FirebaseFirestore.getInstance();
        fetchData();
        // Initialize Views
        addGoal=findViewById(R.id.buttonAddGoalButtonU);
        editTextCalories = findViewById(R.id.idCalaoriesU);
        editTextCarbs = findViewById(R.id.idCarbsU);
        editTextProteins = findViewById(R.id.idProteinU);
        editTextFats = findViewById(R.id.idFatsU);

        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String id = sharedPreferences.getString("number", "0");

                // Get updated data from EditText fields
                String updatedCalories = editTextCalories.getText().toString().trim();
                String updatedCarbs = editTextCarbs.getText().toString().trim();
                String updatedProteins = editTextProteins.getText().toString().trim();
                String updatedFats = editTextFats.getText().toString().trim();

                // Check if any field is empty


                // Create a data map with updated values
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("calories", updatedCalories);
                updatedData.put("carbs", updatedCarbs);
                updatedData.put("proteins", updatedProteins);
                updatedData.put("fats", updatedFats);

                // Update data in Firestore
                firestore.collection("dietary_goals")
                        .document(id)
                        .update(updatedData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Dietary goals updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to update dietary goals", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
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
}