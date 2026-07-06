package com.example.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.adapters.RecentFilesAdapter;
import com.example.dialogs.CustomDialogs;
import com.example.models.RecentFileItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public interface TabSwitcher {
        void switchToTab(int menuItemId);
    }

    private TabSwitcher tabSwitcher;
    private RecyclerView rvRecentFiles;
    private RecentFilesAdapter adapter;
    private List<RecentFileItem> recentFiles;

    public void setTabSwitcher(TabSwitcher tabSwitcher) {
        this.tabSwitcher = tabSwitcher;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Bind Views
        rvRecentFiles = view.findViewById(R.id.rv_recent_files);
        MaterialButton btnChooseFile = view.findViewById(R.id.btn_choose_file);
        
        MaterialCardView qaRecent = view.findViewById(R.id.qa_recent);
        MaterialCardView qaTranslate = view.findViewById(R.id.qa_translate);
        MaterialCardView qaProjects = view.findViewById(R.id.qa_projects);
        MaterialCardView qaSettings = view.findViewById(R.id.qa_settings);
        MaterialCardView qaAbout = view.findViewById(R.id.qa_about);

        // Configure Choose File Button click
        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), com.example.activities.TranslationActivity.class);
            startActivity(intent);
        });

        // Populate Recent Files
        recentFiles = new ArrayList<>();
        recentFiles.add(new RecentFileItem("strings_ar.xml", "صيغة XML • الحجم: 45 كيلوبايت • تعديل: منذ ساعتين"));
        recentFiles.add(new RecentFileItem("resources.arsc", "صيغة ARSC • الحجم: 1.2 ميجابايت • تعديل: أمس"));
        recentFiles.add(new RecentFileItem("messages_en.json", "صيغة JSON • الحجم: 12 كيلوبايت • تعديل: منذ 3 أيام"));

        rvRecentFiles.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecentFilesAdapter(recentFiles, new RecentFilesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecentFileItem item) {
                simulateFileLoad(item.getName());
            }

            @Override
            public void onOpenClick(RecentFileItem item) {
                simulateFileLoad(item.getName());
            }
        });
        rvRecentFiles.setAdapter(adapter);

        // Quick Actions clicks
        qaRecent.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "الملفات الأخيرة معروضة في الأسفل", Toast.LENGTH_SHORT).show();
        });

        qaTranslate.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), com.example.activities.TranslationActivity.class);
            startActivity(intent);
        });

        qaProjects.setOnClickListener(v -> {
            if (tabSwitcher != null) tabSwitcher.switchToTab(R.id.nav_projects);
        });

        qaSettings.setOnClickListener(v -> {
            if (tabSwitcher != null) tabSwitcher.switchToTab(R.id.nav_settings);
        });

        qaAbout.setOnClickListener(v -> {
            CustomDialogs.showSuccessDialog(requireContext(), 
                getString(R.string.app_name), 
                getString(R.string.setting_about_desc) + "\n\n" + getString(R.string.app_version), 
                null
            );
        });

        return view;
    }

    private void simulateFileLoad(String fileName) {
        Intent intent = new Intent(requireActivity(), com.example.activities.TranslationActivity.class);
        intent.putExtra("file_name", fileName);
        startActivity(intent);
    }
}
