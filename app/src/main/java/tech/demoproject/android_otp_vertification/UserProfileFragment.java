package tech.demoproject.android_otp_vertification;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {

    private EditText etAge, etWeight, etHeight, etDietaryRestrictions, etGoals;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spActivityLevel;
    private CheckBox cbKeepAgePrivate, cbKeepWeightPrivate, cbKeepHeightPrivate;
    private Button btnSave;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize views
        etAge = view.findViewById(R.id.et_age);
        rgGender = view.findViewById(R.id.rg_gender);
        rbMale = view.findViewById(R.id.rb_male);
        rbFemale = view.findViewById(R.id.rb_female);
        etWeight = view.findViewById(R.id.et_weight);
        etHeight = view.findViewById(R.id.et_height);
        spActivityLevel = view.findViewById(R.id.sp_activity_level);
        etDietaryRestrictions = view.findViewById(R.id.et_dietary_restrictions);
        etGoals = view.findViewById(R.id.et_goals);
        cbKeepAgePrivate = view.findViewById(R.id.cb_keep_age_private);
        cbKeepWeightPrivate = view.findViewById(R.id.cb_keep_weight_private);
        cbKeepHeightPrivate = view.findViewById(R.id.cb_keep_height_private);
        btnSave = view.findViewById(R.id.btn_save);

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Initialize Firebase reference
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("userProfile");

            // Set up save button listener
            btnSave.setOnClickListener(v -> saveUserProfile());

            // Load existing user profile
            loadUserProfile();
        } else {
            Toast.makeText(getActivity(), "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            // Redirect to login screen or handle as needed
        }

        return view;
    }

    private void saveUserProfile() {
        String age = etAge.getText().toString().trim();
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = selectedGenderId == R.id.rb_male ? "Male" : "Female";
        String weight = etWeight.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String activityLevel = spActivityLevel.getSelectedItem().toString();
        String dietaryRestrictions = etDietaryRestrictions.getText().toString().trim();
        String goals = etGoals.getText().toString().trim();
        boolean keepAgePrivate = cbKeepAgePrivate.isChecked();
        boolean keepWeightPrivate = cbKeepWeightPrivate.isChecked();
        boolean keepHeightPrivate = cbKeepHeightPrivate.isChecked();

        // Validate all fields
        if (TextUtils.isEmpty(age) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(height) ||
                TextUtils.isEmpty(activityLevel) || TextUtils.isEmpty(dietaryRestrictions) || TextUtils.isEmpty(goals)) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile userProfile = new UserProfile(age, gender, weight, height, activityLevel, dietaryRestrictions, goals,
                keepAgePrivate, keepWeightPrivate, keepHeightPrivate);

        // Save user profile to Firebase
        databaseReference.setValue(userProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Profile saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserProfile() {
        // Load user profile from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        // Populate the UI with the loaded user profile data
                        etAge.setText(userProfile.getAge());
                        if ("Male".equals(userProfile.getGender())) {
                            rbMale.setChecked(true);
                        } else if ("Female".equals(userProfile.getGender())) {
                            rbFemale.setChecked(true);
                        }
                        etWeight.setText(userProfile.getWeight());
                        etHeight.setText(userProfile.getHeight());

                        String[] activityLevels = getResources().getStringArray(R.array.activity_levels);
                        for (int i = 0; i < activityLevels.length; i++) {
                            if (activityLevels[i].equals(userProfile.getActivityLevel())) {
                                spActivityLevel.setSelection(i);
                                break;
                            }
                        }

                        etDietaryRestrictions.setText(userProfile.getDietaryRestrictions());
                        etGoals.setText(userProfile.getGoals());
                        cbKeepAgePrivate.setChecked(userProfile.isKeepAgePrivate());
                        cbKeepWeightPrivate.setChecked(userProfile.isKeepWeightPrivate());
                        cbKeepHeightPrivate.setChecked(userProfile.isKeepHeightPrivate());
                    }
                } else {
                    Toast.makeText(getActivity(), "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
