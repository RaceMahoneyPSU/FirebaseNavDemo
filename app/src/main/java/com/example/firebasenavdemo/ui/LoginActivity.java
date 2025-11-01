package com.example.firebasenavdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasenavdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * An activity that provides a user interface for signing in to the application.
 * It handles user input for email and password and communicates with Firebase Authentication.
 */
public class LoginActivity extends AppCompatActivity {

    // --- UI Components ---
    private EditText etEmail, etPassword;
    private ProgressBar progress;

    // --- Firebase ---
    private FirebaseAuth auth;

    /**
     * Called when the activity is first created. This is where the UI is initialized,
     * Firebase services are obtained, and event listeners are set up.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this activity.
        setContentView(R.layout.activity_login);

        // --- Firebase Initialization ---
        // Get an instance of the FirebaseAuth service.
        auth = FirebaseAuth.getInstance();

        // --- View Initialization ---
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progress = findViewById(R.id.progress);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        // --- Auto-Login Check (Commented Out) ---
        // This block can be uncommented to automatically skip the login screen
        // if the user is already signed in.
        // if (auth.getCurrentUser() != null) {
        //     startActivity(new Intent(this, MainActivity.class));
        //     finish();
        // }

        // --- Event Listeners ---
        // Set a click listener on the login button to trigger the login process.
        btnLogin.setOnClickListener(v -> doLogin());
        // Set a click listener on the "Sign Up" text to navigate to the SignUpActivity.
        tvGoToSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    /**
     * Handles the user login process.
     * It retrieves user input, validates it, and then attempts to sign in
     * using Firebase Authentication.
     */
    private void doLogin() {
        // Get the email and password from the EditText fields, removing whitespace.
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // --- Input Validation ---
        // Check if the email field is empty.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            return;
        }
        // Check if the password field is empty.
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Required");
            return;
        }

        // --- Firebase Sign-In ---
        // Show the progress bar to indicate a network operation is in progress.
        progress.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide the progress bar once the operation is complete.
                        progress.setVisibility(View.GONE);

                        // Check if the sign-in task was successful.
                        if (task.isSuccessful()) {
                            // On success, show a confirmation message.
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to the main screen of the app.
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            // Finish this activity so the user cannot navigate back to it.
                            finish();
                        } else {
                            // If sign-in fails, show an error message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
