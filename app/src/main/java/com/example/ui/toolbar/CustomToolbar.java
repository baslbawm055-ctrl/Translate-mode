package com.example.ui.toolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;

public class CustomToolbar extends LinearLayout {

    private ImageButton btnBack;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageButton btnSearch;
    private ImageButton btnOverflow;

    public CustomToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        View view = LayoutInflater.from(context).inflate(R.layout.view_custom_toolbar, this, true);
        
        btnBack = view.findViewById(R.id.btn_back);
        tvTitle = view.findViewById(R.id.tv_title);
        tvSubtitle = view.findViewById(R.id.tv_subtitle);
        btnSearch = view.findViewById(R.id.btn_search);
        btnOverflow = view.findViewById(R.id.btn_overflow);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar);
            
            String title = a.getString(R.styleable.CustomToolbar_ct_title);
            String subtitle = a.getString(R.styleable.CustomToolbar_ct_subtitle);
            boolean showBack = a.getBoolean(R.styleable.CustomToolbar_ct_showBackButton, false);
            boolean showSearch = a.getBoolean(R.styleable.CustomToolbar_ct_showSearch, false);
            boolean showOverflow = a.getBoolean(R.styleable.CustomToolbar_ct_showOverflow, false);
            
            setTitle(title != null ? title : "");
            setSubtitle(subtitle);
            showBackButton(showBack);
            showSearchButton(showSearch);
            showOverflowButton(showOverflow);
            
            a.recycle();
        }
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setSubtitle(@Nullable String subtitle) {
        if (subtitle != null && !subtitle.isEmpty()) {
            tvSubtitle.setText(subtitle);
            tvSubtitle.setVisibility(VISIBLE);
        } else {
            tvSubtitle.setVisibility(GONE);
        }
    }

    public void showBackButton(boolean show) {
        btnBack.setVisibility(show ? VISIBLE : GONE);
    }

    public void showSearchButton(boolean show) {
        btnSearch.setVisibility(show ? VISIBLE : GONE);
    }

    public void showOverflowButton(boolean show) {
        btnOverflow.setVisibility(show ? VISIBLE : GONE);
    }

    public void setOnBackClickListener(OnClickListener listener) {
        btnBack.setOnClickListener(listener);
    }

    public void setOnSearchClickListener(OnClickListener listener) {
        btnSearch.setOnClickListener(listener);
    }

    public void setOnOverflowClickListener(OnClickListener listener) {
        btnOverflow.setOnClickListener(listener);
    }
}
