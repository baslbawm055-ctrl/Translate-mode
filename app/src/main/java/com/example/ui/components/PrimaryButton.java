package com.example.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class PrimaryButton extends FrameLayout {

    private MaterialCardView cardView;
    private LinearLayout contentLayout;
    private ImageView imgIcon;
    private TextView tvText;
    private ProgressBar progressBar;
    
    private boolean isLoading = false;
    private float defaultElevation;

    public PrimaryButton(@NonNull Context context) {
        this(context, null);
    }

    public PrimaryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrimaryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_primary_button, this, true);
        
        cardView = view.findViewById(R.id.button_card);
        contentLayout = view.findViewById(R.id.content_layout);
        imgIcon = view.findViewById(R.id.button_icon);
        tvText = view.findViewById(R.id.button_text);
        progressBar = view.findViewById(R.id.button_progress);
        
        defaultElevation = cardView.getCardElevation();

        // Setup touch animations (Scale down a bit on press)
        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    cardView.animate().scaleX(0.97f).scaleY(0.97f).setDuration(80).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                    break;
            }
            return false;
        });

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrimaryButton);
            
            String text = a.getString(R.styleable.PrimaryButton_pb_text);
            Drawable icon = a.getDrawable(R.styleable.PrimaryButton_pb_icon);
            boolean loading = a.getBoolean(R.styleable.PrimaryButton_pb_loading, false);
            float radius = a.getDimension(R.styleable.PrimaryButton_pb_cornerRadius, -1);
            
            if (text != null) {
                tvText.setText(text);
            }
            if (icon != null) {
                imgIcon.setImageDrawable(icon);
                imgIcon.setVisibility(View.VISIBLE);
            }
            if (radius != -1) {
                cardView.setRadius(radius);
            }
            
            setLoading(loading);
            
            a.recycle();
        }
    }

    public void setText(String text) {
        tvText.setText(text);
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            imgIcon.setImageDrawable(icon);
            imgIcon.setVisibility(View.VISIBLE);
        } else {
            imgIcon.setVisibility(View.GONE);
        }
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            contentLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            cardView.setClickable(false);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            cardView.setClickable(isEnabled());
        }
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cardView.setEnabled(enabled);
        cardView.setClickable(enabled && !isLoading);
        if (enabled) {
            cardView.setAlpha(1.0f);
            cardView.setCardElevation(defaultElevation);
        } else {
            cardView.setAlpha(0.5f);
            cardView.setCardElevation(0f);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        cardView.setOnClickListener(l);
    }
}
