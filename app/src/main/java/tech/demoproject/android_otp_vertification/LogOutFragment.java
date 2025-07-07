package tech.demoproject.android_otp_vertification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;

public class LogOutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_out, container, false);

        // Log out the user
        FirebaseAuth.getInstance().signOut();

        // Clear any local user data here
        clearLocalData();

        // Redirect to the login screen or start the login activity
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        return view;
    }

    private void clearLocalData() {
        // Clear SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("user_preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Clear any in-memory data or other cached data here if necessary
    }
}
