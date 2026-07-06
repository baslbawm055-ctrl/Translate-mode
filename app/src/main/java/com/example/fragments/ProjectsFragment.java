package com.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.adapters.ProjectsAdapter;
import com.example.dialogs.CustomDialogs;
import com.example.models.ProjectItem;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment {

    private RecyclerView rvProjects;
    private LinearLayout emptyView;
    private ProjectsAdapter adapter;
    private List<ProjectItem> projectsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        rvProjects = view.findViewById(R.id.rv_projects);
        emptyView = view.findViewById(R.id.empty_projects_view);
        MaterialButton btnCreateProject = view.findViewById(R.id.btn_create_project);

        projectsList = new ArrayList<>();

        rvProjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProjectsAdapter(projectsList, item -> {
            Toast.makeText(requireContext(), "تم فتح مشروع: " + item.getName(), Toast.LENGTH_SHORT).show();
        });
        rvProjects.setAdapter(adapter);

        btnCreateProject.setOnClickListener(v -> {
            Dialog progress = CustomDialogs.showProgressDialog(requireContext());
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();
                
                // Add a mock project item
                String projName = "تعريب تطبيق التراسل الذكي";
                String projPath = "/storage/emulated/0/BLManager/Projects/ChatAppAr";
                String projDate = "الآن";
                
                projectsList.add(new ProjectItem(projName, projPath, projDate));
                adapter.notifyDataSetChanged();
                
                // Toggle view visibility
                emptyView.setVisibility(View.GONE);
                rvProjects.setVisibility(View.VISIBLE);
                
                CustomDialogs.showSuccessDialog(requireContext(), 
                    getString(R.string.dialog_success_title), 
                    "تم إنشاء وتنظيم مجلدات المشروع الجديد \"" + projName + "\" بنجاح!", 
                    null
                );
            }, 1500);
        });

        return view;
    }
}
