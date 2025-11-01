package com.example.firebasenavdemo.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebasenavdemo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * An activity that provides a user interface for creating a new user account.
 * It handles user input for name, email, and password and uses Firebase Authentication
 * to register the new user.
 */
public class SignUpActivity extends AppCompatActivity {

    // --- UI Components ---
    private EditText etName, etEmail, etPassword;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this activity.
        setContentView(R.layout.activity_sign_up);

        // --- Firebase Initialization ---
        // Get an instance of the FirebaseAuth service.
        auth = FirebaseAuth.getInstance();

        // --- View Initialization ---
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progress = findViewById(R.id.progress);
        Button btnCreate = findViewById(R.id.btnCreate);

        // --- Event Listener ---
        // Set a click listener on the "Create" button to trigger the account creation process.
        btnCreate.setOnClickListener(v -> doCreate());
    }

    /**
     * Handles the user account creation process.
     * It retrieves user input, validates it, and then attempts to create the user
     * and update their profile with the provided name.
     */
    private void doCreate() {
        // Get the name, email, and password from the EditText fields, removing whitespace.
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // --- Input Validation ---
        // Check if the required fields are empty and set an error if they are.
        if (TextUtils.isEmpty(name)) { etName.setError("Required"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Required"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Required"); return; }

        // Show the progress bar to indicate a network operation is in progress.
        progress.setVisibility(View.VISIBLE);

        // --- Firebase Account Creation ---
        // Use Firebase to create a new user with the given email and password.
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // This block runs when the user creation task is complete.
                    if (task.isSuccessful()) {
                        // If user creation is successful, get the newly created user.
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // --- Update User Profile ---
                            // Create a request to update the user's profile with their display name.
                            UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            // Apply the profile update.
                            user.updateProfile(req).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Hide the progress bar once all operations are complete.
                                    progress.setVisibility(View.GONE);
                                    if(task.isSuccessful()) {
                                        // If the profile update is successful, show a confirmation toast.
                                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                        // Close this activity and return to the previous screen (LoginActivity).
                                        finish();
                                    } else {
                                        // If profile update fails, show a generic failure message.
                                        Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // This case is unlikely but handled for safety.
                            progress.setVisibility(View.GONE);
                            Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If user creation fails (e.g., email already exists), hide progress and show error.
                        progress.setVisibility(View.GONE);
                        Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
