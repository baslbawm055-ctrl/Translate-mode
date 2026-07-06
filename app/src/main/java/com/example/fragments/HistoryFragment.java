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
import com.example.adapters.HistoryAdapter;
import com.example.dialogs.CustomDialogs;
import com.example.models.HistoryItem;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private LinearLayout emptyView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rv_history);
        emptyView = view.findViewById(R.id.empty_history_view);
        MaterialButton btnChooseFileHistory = view.findViewById(R.id.btn_choose_file_history);

        historyList = new ArrayList<>();

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HistoryAdapter(historyList, item -> {
            Toast.makeText(requireContext(), "فتح سجل: " + item.getAction(), Toast.LENGTH_SHORT).show();
        });
        rvHistory.setAdapter(adapter);

        btnChooseFileHistory.setOnClickListener(v -> {
            Dialog progress = CustomDialogs.showProgressDialog(requireContext());
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                progress.dismiss();

                String action = "ترجمة نصوص XML بالذكاء الاصطناعي";
                String details = "strings_ar.xml • ترجمة 150 عبارة باستخدام Gemini API";
                String date = "الآن";

                historyList.add(new HistoryItem(action, details, date));
                adapter.notifyDataSetChanged();

                emptyView.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);

                CustomDialogs.showSuccessDialog(requireContext(),
                    "تم تحميل السجل",
                    "تمت إضافة الإجراء الجديد إلى السجل بنجاح!",
                    null
                );
            }, 1200);
        });

        return view;
    }
}
