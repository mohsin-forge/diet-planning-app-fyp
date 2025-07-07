package tech.demoproject.android_otp_vertification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import tech.demoproject.android_otp_vertification.Login;
import tech.demoproject.android_otp_vertification.Register;

public class StartActivity extends AppCompatActivity {
    private ImageView Logo;
    private LinearLayout LinearLayout;
    private Button register;
    private Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Logo = findViewById(R.id.Logo);
        LinearLayout = findViewById(R.id.linear_layout);
        register = findViewById(R.id.registerMainActivity);
        logIn = findViewById(R.id.logInMainActivity);
        //Hide Linear Layout Initially
        LinearLayout.animate().alpha(0f);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        animation.setAnimationListener(new MyAnimationListener());
        Logo.setAnimation(animation);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, Register.class). addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }
    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Logo.clearAnimation();
            Logo.setVisibility(View.INVISIBLE);
            LinearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}