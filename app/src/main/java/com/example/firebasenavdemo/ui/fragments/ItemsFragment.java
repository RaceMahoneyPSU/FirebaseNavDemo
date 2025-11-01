package com.example.firebasenavdemo.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasenavdemo.R;
import com.example.firebasenavdemo.ui.fragments.adapters.SimpleItemAdapter;
import com.example.firebasenavdemo.ui.fragments.model.SimpleItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays a list of items for the currently logged-in user.
 * It allows adding new items and deleting existing ones via a swipe gesture.
 * The data is synchronized in real-time with a Firestore database.
 */
public class ItemsFragment extends Fragment {

    // --- UI Components ---
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    // --- Firebase & Data ---
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private ListenerRegistration registration; // Listens for real-time database changes.
    private final List<SimpleItem> data = new ArrayList<>();
    private SimpleItemAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is where the fragment's layout is inflated and views are initialized.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View v = inflater.inflate(R.layout.fragment_items, container, false);

        // --- View Initialization ---
        recyclerView = v.findViewById(R.id.recycler);
        fabAdd = v.findViewById(R.id.fabAdd);

        // --- RecyclerView Setup ---
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SimpleItemAdapter(data);
        recyclerView.setAdapter(adapter);

        // --- Firestore Setup ---
        // Get a Firestore instance and the UID of the current user.
        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Create a reference to the user-specific 'items' sub-collection.
        itemsRef = db.collection("users").document(uid).collection("items");

        // --- Live Query (Real-time Data Synchronization) ---
        // Attach a listener that fires whenever data in the 'items' collection changes.
        registration = itemsRef.orderBy("title")
                .addSnapshotListener((snap, e) -> {
                    // Handle any errors during the snapshot.
                    if (e != null) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Clear the local data list before repopulating it.
                    data.clear();
                    // If the snapshot is not null, process the documents.
                    if (snap != null) {
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            // Convert each Firestore document into a SimpleItem object.
                            SimpleItem item = doc.toObject(SimpleItem.class);
                            data.add(item);
                        }
                    }
                    // Notify the adapter that the data set has changed to refresh the UI.
                    adapter.notifyDataSetChanged();
                });

        // --- Add Item Functionality ---
        // Set a click listener for the Floating Action Button to show the add dialog.
        fabAdd.setOnClickListener(vw -> showAddDialog());

        // --- Swipe-to-Delete Functionality ---
        // Create an ItemTouchHelper to handle swipe gestures on the RecyclerView.
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                // We are not implementing drag-and-drop, so return false.
                return false;
            }

            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                // Get the position of the swiped item.
                int pos = vh.getBindingAdapterPosition();
                // Safety check to ensure the position is valid.
                if (pos < 0 || pos >= data.size()) return;

                // Get the item object that was swiped.
                SimpleItem item = data.get(pos);

                // Firestore requires the document ID to delete it. Check if it's present.
                if (item.id == null) {
                    Toast.makeText(requireContext(), "Missing document id", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(pos); // Refresh the item to prevent it from disappearing visually.
                    return;
                }
                // Issue a delete command to Firestore for the specific document.
                itemsRef.document(item.id).delete()
                        .addOnFailureListener(ex -> Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
        // Attach the helper to the RecyclerView.
        helper.attachToRecyclerView(recyclerView);

        return v;
    }

    /**
     * Displays an AlertDialog to allow the user to input details for a new item.
     * On save, the new item is added to the Firestore database.
     */
    private void showAddDialog() {
        // Inflate the custom layout for the dialog.
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_item, null, false);
        EditText etTitle = form.findViewById(R.id.etTitle);
        EditText etSubtitle = form.findViewById(R.id.etSubtitle);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Item")
                .setView(form)
                .setPositiveButton("Save", (d, which) -> {
                    String t = etTitle.getText().toString().trim();
                    String s = etSubtitle.getText().toString().trim();
                    // Validate that the title is not empty.
                    if (TextUtils.isEmpty(t)) {
                        Toast.makeText(requireContext(), "Title required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create a new SimpleItem and add it to Firestore.
                    SimpleItem item = new SimpleItem(t, s);
                    itemsRef.add(item)
                            .addOnFailureListener(ex -> Toast.makeText(requireContext(), ex.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .show();
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     * This is a good place to clean up resources associated with the view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detach the Firestore listener to prevent memory leaks and unnecessary background processing.
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }
}
