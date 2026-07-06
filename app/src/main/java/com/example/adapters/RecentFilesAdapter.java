package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.models.RecentFileItem;
import com.example.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class RecentFilesAdapter extends RecyclerView.Adapter<RecentFilesAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RecentFileItem item);
        void onOpenClick(RecentFileItem item);
    }

    private final List<RecentFileItem> items;
    private final OnItemClickListener listener;

    public RecentFilesAdapter(List<RecentFileItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentFileItem item = items.get(position);
        holder.tvFileName.setText(item.getName());
        holder.tvFileDesc.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnOpen.setOnClickListener(v -> {
            if (listener != null) listener.onOpenClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvFileName;
        public final TextView tvFileDesc;
        public final MaterialButton btnOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.item_file_name);
            tvFileDesc = itemView.findViewById(R.id.item_file_desc);
            btnOpen = itemView.findViewById(R.id.btn_open_file);
        }
    }
}
