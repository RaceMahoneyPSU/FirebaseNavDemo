package com.example.firebasenavdemo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.example.firebasenavdemo.R;

/**
 * A fragment that allows the user to change the application's display language.
 * It provides options (e.g., English, Spanish) and applies the selected locale
 * to the entire app.
 */
public class LanguageFragment extends Fragment {

    // --- UI Components ---
    private RadioGroup rgLanguages;
    private RadioButton rbEnglish, rbSpanish;
    private Button btnApply;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is where the layout is inflated and UI elements are configured.
     *
     * @param inflater The LayoutInflater object to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View v = inflater.inflate(R.layout.fragment_language, container, false);

        // --- View Initialization ---
        rgLanguages = v.findViewById(R.id.rgLanguages);
        rbEnglish   = v.findViewById(R.id.rbEnglish);
        rbSpanish   = v.findViewById(R.id.rbSpanish);
        btnApply    = v.findViewById(R.id.btnApplyLanguage);

        // --- Set Initial State ---
        // Get the current application locale list.
        LocaleListCompat current = AppCompatDelegate.getApplicationLocales();
        // Get the language tag (e.g., "en", "es"). Default to "en" if empty.
        String tag = (current.isEmpty()) ? "en" : current.toLanguageTags();
        // Check the appropriate RadioButton based on the current language.
        if (tag.startsWith("es")) {
            rbSpanish.setChecked(true);
        } else {
            rbEnglish.setChecked(true);
        }

        // --- Button Click Listener ---
        // Set a listener to handle the apply button click.
        btnApply.setOnClickListener(vw -> {
            // Determine which language was chosen based on the selected RadioButton.
            String chosenLanguageTag = rbSpanish.isChecked() ? "es" : "en";
            // Create a new LocaleList for the chosen language.
            LocaleListCompat locales = LocaleListCompat.forLanguageTags(chosenLanguageTag);
            // Apply the new locale to the entire application. The system will handle recreating the UI.
            AppCompatDelegate.setApplicationLocales(locales);
            // Show a confirmation toast. Note: This will display in the old language until the UI is recreated.
            Toast.makeText(requireContext(), getString(R.string.apply), Toast.LENGTH_SHORT).show();
        });

        // Return the configured view.
        return v;
    }
}
