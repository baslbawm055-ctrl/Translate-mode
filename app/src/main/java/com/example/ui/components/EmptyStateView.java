package com.example.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;

public class EmptyStateView extends LinearLayout {

    private ImageView imgIllustration;
    private TextView tvTitle;
    private TextView tvDescription;
    private PrimaryButton btnAction;

    public EmptyStateView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.view_empty_state, this, true);
        
        imgIllustration = view.findViewById(R.id.img_illustration);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        btnAction = view.findViewById(R.id.btn_action);
    }

    public void setIllustration(int drawableRes) {
        imgIllustration.setImageResource(drawableRes);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDescription(String description) {
        tvDescription.setText(description);
    }

    public void setAction(String buttonText, OnClickListener listener) {
        if (buttonText != null && !buttonText.isEmpty() && listener != null) {
            btnAction.setText(buttonText);
            btnAction.setOnClickListener(listener);
            btnAction.setVisibility(VISIBLE);
        } else {
            btnAction.setVisibility(GONE);
        }
    }
}
