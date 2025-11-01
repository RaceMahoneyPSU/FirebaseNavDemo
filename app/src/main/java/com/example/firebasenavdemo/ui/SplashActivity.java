package com.example.firebasenavdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasenavdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The initial screen displayed when the application is launched.
 * It shows a splash screen for a brief period and then navigates
 * either to the LoginActivity or the MainActivity based on the user's
 * authentication state.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * A developer flag. If set to true, the app will always show the login screen,
     * ignoring any existing signed-in user. This is useful for testing the login flow.
     */
    private static final boolean FORCE_LOGIN_EVERY_TIME = true;

    /**
     * Called when the activity is first created. This method sets up the splash screen
     * and a delayed handler to navigate to the next appropriate activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the user interface layout for this activity from activity_splash.xml.
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the execution of the navigation logic.
        // This creates the "splash screen" effect by showing the layout for a short duration.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i; // The intent to be launched after the delay.

            // Check the developer flag to decide the navigation path.
            if (FORCE_LOGIN_EVERY_TIME) {
                // If forcing login, always navigate to LoginActivity.
                i = new Intent(this, LoginActivity.class);
            } else {
                // --- Normal Behavior ---
                // Check if a user is already signed in with Firebase.
                FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();

                // If a user exists (is not null), navigate to the MainActivity.
                // Otherwise, navigate to the LoginActivity.
                i = (u != null) ? new Intent(this, MainActivity.class)
                        : new Intent(this, LoginActivity.class);
            }
            // Launch the determined activity.
            startActivity(i);
            // Finish the SplashActivity so the user cannot navigate back to it.
            finish();
        }, 900); // The delay in milliseconds (approx. 0.9 seconds).
    }
}
