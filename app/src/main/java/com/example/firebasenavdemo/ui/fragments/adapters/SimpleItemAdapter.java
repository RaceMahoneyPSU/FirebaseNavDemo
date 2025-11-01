package com.example.firebasenavdemo.ui.fragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasenavdemo.R;
import com.example.firebasenavdemo.ui.fragments.model.SimpleItem;

import java.util.List;

/**
 * A simple RecyclerView adapter for displaying a list of SimpleItem objects.
 * This is a basic adapter implementation that binds a list of data directly
 * to the views.
 */
public class SimpleItemAdapter extends RecyclerView.Adapter<SimpleItemAdapter.VH> {

    /**
     * ViewHolder class that holds and manages the views for a single list item.
     * This improves performance by avoiding repeated findViewById() calls.
     */
    public static class VH extends RecyclerView.ViewHolder {
        // View references for the list item.
        TextView tvTitle, tvSubtitle;

        /**
         * ViewHolder constructor.
         * @param itemView The root view of the list item layout (row_simple_item.xml).
         */
        public VH(@NonNull View itemView) {
            super(itemView);
            // Find and cache the views by their ID.
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }

    // The list of items to be displayed by the adapter.
    private final List<SimpleItem> data;

    /**
     * Constructor for the adapter.
     * @param data A list of SimpleItem objects to populate the adapter with.
     */
    public SimpleItemAdapter(List<SimpleItem> data) {
        this.data = data;
    }

    /**
     * Called by the RecyclerView when it needs a new ViewHolder to represent an item.
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new VH that holds the view for each item.
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single list item.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_simple_item, parent, false);
        return new VH(v);
    }

    /**
     * Called by the RecyclerView to display the data at a specified position.
     * This method updates the contents of the ViewHolder to reflect the item.
     * @param holder The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        // Get the data model for this position.
        SimpleItem it = data.get(position);
        // Set the text for the title and subtitle views.
        holder.tvTitle.setText(it.title);
        holder.tvSubtitle.setText(it.subtitle);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
