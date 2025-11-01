package com.example.firebasenavdemo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.firebasenavdemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

/**
 * A fragment that displays the current user's account information and provides
 * an option to delete all their associated items from Firestore.
 */
public class AccountFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        // --- View Initialization ---
        TextView tv = v.findViewById(R.id.tvAccountInfo);
        Button btn = v.findViewById(R.id.btnDeleteAllItems);

        // --- Display User Info ---
        // Get the current signed-in user from Firebase Auth.
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            // If a user is logged in, display their name and email.
            tv.setText("Name: " + (u.getDisplayName() == null ? "" : u.getDisplayName())
                    + "\nEmail: " + (u.getEmail() == null ? "" : u.getEmail()));
        } else {
            // If no user is logged in, display a message.
            tv.setText("No user logged in");
        }

        // --- Button Click Listener for Deleting All Items ---
        btn.setOnClickListener(click -> {
            // Get the current user again to ensure they haven't signed out.
            FirebaseUser u2 = FirebaseAuth.getInstance().getCurrentUser();
            if (u2 == null) {
                Toast.makeText(requireContext(), "No user", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable the button to prevent multiple clicks during the operation.
            btn.setEnabled(false);

            // Get a Firestore instance and reference the user's "items" collection.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(u2.getUid()).collection("items")
                    .get() // Fetch all documents in the collection.
                    .addOnCompleteListener(task -> {
                        // Check if the fetch operation was unsuccessful.
                        if (!task.isSuccessful()) {
                            btn.setEnabled(true); // Re-enable the button.
                            // Show an error message.
                            Toast.makeText(requireContext(),
                                    task.getException() != null ? task.getException().getMessage() : "Query failed",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get the result of the query.
                        QuerySnapshot q = task.getResult();
                        if (q == null || q.isEmpty()) {
                            btn.setEnabled(true); // Re-enable the button.
                            Toast.makeText(requireContext(), "No items to delete", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // --- Batch Deletion ---
                        // Create a WriteBatch to delete all documents in a single atomic operation.
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot d : q.getDocuments()) {
                            // Add each document's deletion to the batch.
                            batch.delete(d.getReference());
                        }

                        // Commit the batch operation.
                        batch.commit()
                                .addOnSuccessListener(unused -> {
                                    // On success, re-enable the button and show a confirmation toast.
                                    btn.setEnabled(true);
                                    Toast.makeText(requireContext(), "All items deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // On failure, re-enable the button and show an error message.
                                    btn.setEnabled(true);
                                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    });
        });
        return v; // Return the configured view.
    }
}
