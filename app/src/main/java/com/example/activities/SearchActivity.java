package com.example.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.R;
import com.example.dialogs.CustomDialogs;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnClear;
    private LinearLayout suggestionsContainer;
    private LinearLayout emptySearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Bind views
        etSearch = findViewById(R.id.et_search);
        ImageButton btnBack = findViewById(R.id.btn_search_back);
        btnClear = findViewById(R.id.btn_search_clear);
        suggestionsContainer = findViewById(R.id.search_suggestions_container);
        emptySearchView = findViewById(R.id.empty_search_view);

        TextView recentItem1 = findViewById(R.id.recent_item_1);
        TextView recentItem2 = findViewById(R.id.recent_item_2);
        TextView suggestItem1 = findViewById(R.id.suggest_item_1);
        TextView suggestItem2 = findViewById(R.id.suggest_item_2);
        TextView suggestItem3 = findViewById(R.id.suggest_item_3);
        
        View btnRetrySearch = findViewById(R.id.btn_retry_search);

        // Back button action
        btnBack.setOnClickListener(v -> finish());

        // TextWatcher to listen to query entry
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                    suggestionsContainer.setVisibility(View.VISIBLE);
                    emptySearchView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear button
        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            btnClear.setVisibility(View.GONE);
        });

        // Search Action from Keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Clicking recent/suggestions searches
        View.OnClickListener searchItemListener = v -> {
            TextView tv = (TextView) v;
            String query = tv.getText().toString();
            etSearch.setText(query);
            etSearch.setSelection(query.length());
            performSearch(query);
        };

        recentItem1.setOnClickListener(searchItemListener);
        recentItem2.setOnClickListener(searchItemListener);
        suggestItem1.setOnClickListener(searchItemListener);
        suggestItem2.setOnClickListener(searchItemListener);
        suggestItem3.setOnClickListener(searchItemListener);

        // Retry Action on empty search
        btnRetrySearch.setOnClickListener(v -> {
            etSearch.setText("");
            suggestionsContainer.setVisibility(View.VISIBLE);
            emptySearchView.setVisibility(View.GONE);
        });
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            return;
        }

        Dialog progress = CustomDialogs.showProgressDialog(this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progress.dismiss();

            // Simulate result toggle: if user types "empty", "مفقود", "لا يوجد" or anything unknown, show empty
            if (query.toLowerCase().contains("empty") || query.contains("فارغ") || query.length() > 8) {
                suggestionsContainer.setVisibility(View.GONE);
                emptySearchView.setVisibility(View.VISIBLE);
            } else {
                suggestionsContainer.setVisibility(View.VISIBLE);
                emptySearchView.setVisibility(View.GONE);
                Toast.makeText(this, "تم العثور على نتائج ذات صلة بـ \"" + query + "\"", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }
}
