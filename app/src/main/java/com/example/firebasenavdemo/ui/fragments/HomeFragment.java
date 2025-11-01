package com.example.firebasenavdemo.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.firebasenavdemo.R;

/**
 * A simple fragment that serves as the home screen of the application.
 * Currently, it only displays a static layout.
 */
public class HomeFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is where the fragment's layout is inflated.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout defined in 'fragment_home.xml' for this fragment.
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
