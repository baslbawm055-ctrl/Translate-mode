package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.models.ProjectItem;
import com.example.R;
import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(ProjectItem item);
    }

    private final List<ProjectItem> items;
    private final OnProjectClickListener listener;

    public ProjectsAdapter(List<ProjectItem> items, OnProjectClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectItem item = items.get(position);
        holder.tvProjectName.setText(item.getName());
        holder.tvProjectPath.setText(item.getPath());
        holder.tvProjectDate.setText(item.getDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProjectClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvProjectName;
        public final TextView tvProjectPath;
        public final TextView tvProjectDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.item_project_name);
            tvProjectPath = itemView.findViewById(R.id.item_project_path);
            tvProjectDate = itemView.findViewById(R.id.item_project_date);
        }
    }
}
