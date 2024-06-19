package com.example.wolseytechhr;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adapter for RecyclerView to display a list of tables in a file center.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    // LinkedHashMap to store table titles and corresponding TableLayouts
    private final LinkedHashMap<String, TableLayout> tables;
    private final Context context;

    /**
     * Constructor for the RecyclerView Adapter.
     *
     * @param context The context of the application.
     * @param tables  A LinkedHashMap containing table titles and corresponding TableLayouts.
     */
    public RecycleViewAdapter(Context context, LinkedHashMap<String, TableLayout> tables) {
        this.context = context;
        this.tables = tables;
    }

    /**
     * Creates a new ViewHolder instance.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.file_center_table, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Updates the contents of the ViewHolder to represent the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = getTableTitleAtIndex(tables, position);
        TableLayout tableLayout = getTableLayoutAtIndex(tables, position);

        if (title != null) {
            holder.tableTitle.setText(title);
            holder.tableTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
            FileCenter.setUpTableTitles(holder.tableTitle);
        }

        if (tableLayout != null) {
            holder.table.removeAllViews();  // Clear existing views
            holder.table.addView(tableLayout);
            tableLayout.setGravity(Gravity.CENTER);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return tables.size();
    }

    /**
     * ViewHolder class for holding the Views.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tableTitle;
        LinearLayout table;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The View associated with this ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tableTitle = itemView.findViewById(R.id.tableTitle);
            table = itemView.findViewById(R.id.sampleTable);
        }
    }

    /**
     * Converts a LinkedHashMap to an array of TableLayouts.
     *
     * @param map The LinkedHashMap to be converted.
     * @return An array of TableLayouts.
     */
    public static TableLayout[] convertMapToArray(LinkedHashMap<String, TableLayout> map) {
        TableLayout[] array = new TableLayout[map.size()];
        int index = 0;
        for (Map.Entry<String, TableLayout> entry : map.entrySet()) {
            array[index] = entry.getValue();
            index++;
        }
        return array;
    }

    /**
     * Retrieves the table title at a particular index of the map.
     *
     * @param map   The LinkedHashMap containing table titles and corresponding TableLayouts.
     * @param index The index of the desired table title.
     * @return The table title at the specified index.
     */
    public static String getTableTitleAtIndex(LinkedHashMap<String, TableLayout> map, int index) {
        int i = 0; // counter variable
        for (Map.Entry<String, TableLayout> entry : map.entrySet()) {
            if (i == index) {
                return entry.getKey(); // returning table title at that index
            }
            i++; // increasing counter
        }
        return null;
    }

    /**
     * Retrieves the TableLayout at a particular index of the map.
     *
     * @param map   The LinkedHashMap containing table titles and corresponding TableLayouts.
     * @param index The index of the desired TableLayout.
     * @return The TableLayout at the specified index.
     */
    public static TableLayout getTableLayoutAtIndex(LinkedHashMap<String, TableLayout> map, int index) {
        int i = 0; // counter variable
        for (Map.Entry<String, TableLayout> entry : map.entrySet()) {
            if (i == index) {
                return entry.getValue(); // returning table title at that index
            }
            i++; // increasing counter
        }
        return null;
    }
}
