package com.example.fitnesstracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ActivityViewHistory.HistoryItem> historyItems;

    public HistoryAdapter(List<ActivityViewHistory.HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each history item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // Bind the data to the views
        ActivityViewHistory.HistoryItem historyItem = historyItems.get(position);
        holder.tvDate.setText(historyItem.getDate());
        holder.tvSteps.setText("Steps: " + historyItem.getSteps());
        holder.tvDistance.setText("Distance: " + String.format("%.2f km", historyItem.getDistance()));
        holder.tvCalories.setText("Calories: " + historyItem.getCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    // ViewHolder class to hold item views
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvSteps;
        TextView tvDistance;
        TextView tvCalories;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSteps = itemView.findViewById(R.id.tvSteps);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvCalories = itemView.findViewById(R.id.tvCalories);
        }
    }
}
