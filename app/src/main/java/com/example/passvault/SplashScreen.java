package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DURATION = 5000; // Duration of splash screen in milliseconds

    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sessionManagement = new SessionManagement(getApplicationContext());

        // Check if the user is already logged in
        if (!sessionManagement.getEmailid().isEmpty())
        {
            // User is logged in, navigate to HomePage
            navigateToHomePage();
        }
        else
        {
            // No session, navigate to IndexPage for login/signup
            navigateToIndexPage();
        }
    }

    private void navigateToHomePage() {
        Intent intent = new Intent(SplashScreen.this, HomePage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Finish this activity so user cannot come back to splash screen
    }

    private void navigateToIndexPage() {
        Intent intent = new Intent(SplashScreen.this, IndexPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish(); // Finish this activity so user cannot come back to splash screen
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Delayed navigation to next screen using a Handler
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sessionManagement.getEmailid().isEmpty()) {
                    navigateToHomePage();
                } else {
                    navigateToIndexPage();
                }
            }
        }, SPLASH_DURATION);
    }
}
