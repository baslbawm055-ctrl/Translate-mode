package com.example.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;

public class CustomSearchBar extends FrameLayout {

    private EditText etQuery;
    private ImageButton btnClear;
    private ImageButton btnVoice;

    private OnSearchActionListener searchActionListener;

    public interface OnSearchActionListener {
        void onQueryChanged(String query);
        void onSearchSubmitted(String query);
        void onVoiceClicked();
        void onClearClicked();
    }

    public CustomSearchBar(@NonNull Context context) {
        this(context, null);
    }

    public CustomSearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSearchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_custom_search_bar, this, true);
        
        etQuery = view.findViewById(R.id.et_search_query);
        btnClear = view.findViewById(R.id.btn_clear);
        btnVoice = view.findViewById(R.id.btn_voice);

        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                    btnVoice.setVisibility(View.GONE);
                } else {
                    btnClear.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.VISIBLE);
                }
                if (searchActionListener != null) {
                    searchActionListener.onQueryChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchActionListener != null) {
                    searchActionListener.onSearchSubmitted(etQuery.getText().toString());
                }
                return true;
            }
            return false;
        });

        btnClear.setOnClickListener(v -> {
            etQuery.setText("");
            if (searchActionListener != null) {
                searchActionListener.onClearClicked();
            }
        });

        btnVoice.setOnClickListener(v -> {
            if (searchActionListener != null) {
                searchActionListener.onVoiceClicked();
            }
        });

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomSearchBar);
            String hint = a.getString(R.styleable.CustomSearchBar_cs_hint);
            String query = a.getString(R.styleable.CustomSearchBar_cs_query);
            
            if (hint != null) {
                etQuery.setHint(hint);
            }
            if (query != null) {
                etQuery.setText(query);
            }
            a.recycle();
        }
    }

    public void setQuery(String query) {
        etQuery.setText(query);
    }

    public String getQuery() {
        return etQuery.getText().toString();
    }

    public void setOnSearchActionListener(OnSearchActionListener listener) {
        this.searchActionListener = listener;
    }

    public EditText getEditText() {
        return etQuery;
    }
}
