package tech.demoproject.android_otp_vertification;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.annotations.Nullable;

public class HomeFragment extends Fragment {

    private LinearLayout goalCreation;
    private LinearLayout mealPlan;
    private LinearLayout nutritionMonitor;
    private LinearLayout mealCreation;
    private LinearLayout weightTracking;
    private LinearLayout goalsAdjustment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        goalCreation = view.findViewById(R.id.goalCreation);
        mealPlan = view.findViewById(R.id.mealPlan);
        nutritionMonitor = view.findViewById(R.id.nutritionMonitor);
        mealCreation = view.findViewById(R.id.mealCreation);
        weightTracking = view.findViewById(R.id.weightTracking);
        goalsAdjustment = view.findViewById(R.id.goalsAdjustment);

        for (LinearLayout item : new LinearLayout[] { goalCreation, mealPlan, nutritionMonitor, mealCreation, weightTracking, goalsAdjustment }) {
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), getTargetActivity(v));
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    private Class<?> getTargetActivity(View v) {
        switch (v.getId()) {
            case R.id.goalCreation:
                return addGoal.class;
            case R.id.mealPlan:
                return mealPlanActivity.class;
            case R.id.nutritionMonitor:
                return nutritionMonitorActivity.class;
            case R.id.mealCreation:
                return mealCreationActivity.class;
            case R.id.weightTracking:
                return weightTrackingActivity.class;
            case R.id.goalsAdjustment:
                return goalAdjustmentActivity.class;
            default:
                return null;
        }
    }
}