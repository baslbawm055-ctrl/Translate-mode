package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.models.HistoryItem;
import com.example.R;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnHistoryClickListener {
        void onHistoryClick(HistoryItem item);
    }

    private final List<HistoryItem> items;
    private final OnHistoryClickListener listener;

    public HistoryAdapter(List<HistoryItem> items, OnHistoryClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.tvHistoryAction.setText(item.getAction());
        holder.tvHistoryFile.setText(item.getFileDetails());
        holder.tvHistoryDate.setText(item.getDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHistoryClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvHistoryAction;
        public final TextView tvHistoryFile;
        public final TextView tvHistoryDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryAction = itemView.findViewById(R.id.item_history_action);
            tvHistoryFile = itemView.findViewById(R.id.item_history_file);
            tvHistoryDate = itemView.findViewById(R.id.item_history_date);
        }
    }
}
