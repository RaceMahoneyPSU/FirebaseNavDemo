package com.example.firebasenavdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.firebasenavdemo.R;
import com.example.firebasenavdemo.ui.fragments.AccountFragment;
import com.example.firebasenavdemo.ui.fragments.HomeFragment;
import com.example.firebasenavdemo.ui.fragments.ItemsFragment;
import com.example.firebasenavdemo.ui.fragments.LanguageFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nonnull;

/**
 * The main container activity for the application after a user logs in.
 * It hosts a navigation drawer and a fragment container to display different
 * sections of the app like Home, Items, and Account.
 */
public class MainActivity extends AppCompatActivity {

    // --- UI Components for Navigation ---
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private ActionBarDrawerToggle toggle; // Manages the hamburger icon for the drawer.

    /**
     * Called when the activity is first created. This is where UI elements are
     * initialized, the navigation drawer is set up, and the initial fragment is displayed.
     * @param savedInstanceState If the activity is re-created, this bundle contains the most
     *                           recent data, otherwise it's null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Toolbar and Drawer Initialization ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the app bar.

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Connect the drawer layout with the toolbar using the toggle.
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Syncs the state of the hamburger icon.

        // --- Set Initial Fragment ---
        // If this is the first time the activity is created, show the Home fragment.
        if (savedInstanceState == null) {
            navigateTo(new HomeFragment(), getString(R.string.nav_home));
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // --- Populate Navigation Header with User Info ---
        populateNavHeader();

        // --- Navigation Item Click Listener ---
        // Set up the listener for when an item in the navigation menu is clicked.
        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true); // Highlight the selected item.
            drawerLayout.closeDrawers(); // Close the drawer after selection.
            handleNavigation(item); // Navigate to the corresponding fragment.
            return true;
        });
    }

    /**
     * Populates the header of the navigation drawer with the current user's
     * display name and email from Firebase Auth.
     */
    private void populateNavHeader() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get the header view from the NavigationView.
        View header = navigationView.getHeaderView(0);
        TextView tvName = header.findViewById(R.id.tvHeaderName);
        TextView tvEmail = header.findViewById(R.id.tvHeaderEmail);

        // If a user is logged in, set their details.
        if (user != null) {
            tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        }
    }

    /**
     * Replaces the content of the fragment container with a new fragment.
     * @param fragment The fragment to display.
     * @param title The title to set on the toolbar for this fragment.
     */
    private void navigateTo(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        // Update the toolbar's title to reflect the current screen.
        toolbar.setTitle(title);
    }

    /**
     * Handles navigation when a menu item in the drawer is selected.
     * @param item The selected menu item.
     */
    private void handleNavigation(@Nonnull MenuItem item) {
        int id = item.getItemId();
        // Navigate to the appropriate fragment based on the selected item's ID.
        if (id == R.id.nav_home) {
            navigateTo(new HomeFragment(), getString(R.string.nav_home));
        } else if (id == R.id.nav_items) {
            navigateTo(new ItemsFragment(), getString(R.string.nav_items));
        } else if (id == R.id.nav_account) {
            navigateTo(new AccountFragment(), getString(R.string.nav_account));
        } else if (id == R.id.nav_language) {
            navigateTo(new LanguageFragment(), getString(R.string.nav_language));
        } else if (id == R.id.nav_logout) {
            // Handle user logout.
            logout();
        }
    }

    /**
     * Signs the current user out of Firebase Authentication and navigates
     * back to the LoginActivity.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        // Create an intent to go back to the LoginActivity.
        Intent i = new Intent(this, LoginActivity.class);
        // Clear the activity stack to prevent the user from navigating back to MainActivity.
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish(); // Finish the current activity.
    }
}
