package tech.demoproject.android_otp_vertification;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class weightTrackingActivity extends AppCompatActivity {

    private TextView tvWeight;
    private EditText etWeight;
    private Button btnSave;
    private LineChart lineChart;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_tracking);

        tvWeight = findViewById(R.id.tv_weight);
        etWeight = findViewById(R.id.et_weight);
        btnSave = findViewById(R.id.btn_save);
        lineChart = findViewById(R.id.line_chart);

        // Initialize Firebase reference and auth
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("weights");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWeight();
            }
        });

        loadWeightData();
    }

    private void saveWeight() {
        String weightStr = etWeight.getText().toString().trim();
        if (!TextUtils.isEmpty(weightStr)) {
            try {
                float weight = Float.parseFloat(weightStr);
                String id = databaseReference.push().getKey();
                if (id != null) {
                    WeightEntry weightEntry = new WeightEntry(id, weight, System.currentTimeMillis());
                    databaseReference.child(id).setValue(weightEntry)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Weight saved!", Toast.LENGTH_SHORT).show();
                                etWeight.setText("");
                                loadWeightData();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to save weight", Toast.LENGTH_SHORT).show();
                                Log.e("WeightTrackingActivity", "Error saving weight", e);
                            });
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWeightData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WeightEntry weightEntry = snapshot.getValue(WeightEntry.class);
                    if (weightEntry != null) {
                        // Use timestamp as the x-axis value
                        entries.add(new Entry(weightEntry.getTimestamp(), weightEntry.getWeight()));
                    }
                }
                LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
                dataSet.setValueTextSize(12f); // Adjust text size if needed
                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // refresh
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("WeightTrackingActivity", "loadWeightData:onCancelled", databaseError.toException());
            }
        });
    }

    // Define the WeightEntry class
    public static class WeightEntry {
        private String id;
        private float weight;
        private long timestamp;

        public WeightEntry() {
            // Default constructor required for calls to DataSnapshot.getValue(WeightEntry.class)
        }

        public WeightEntry(String id, float weight, long timestamp) {
            this.id = id;
            this.weight = weight;
            this.timestamp = timestamp;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
