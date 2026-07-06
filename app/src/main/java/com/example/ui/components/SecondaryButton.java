package com.example.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class SecondaryButton extends FrameLayout {

    public enum Size {
        SMALL, MEDIUM, LARGE
    }

    private MaterialCardView cardView;
    private FrameLayout container;
    private LinearLayout contentLayout;
    private ImageView imgIcon;
    private TextView tvText;
    private ProgressBar progressBar;
    
    private boolean isLoading = false;

    public SecondaryButton(@NonNull Context context) {
        this(context, null);
    }

    public SecondaryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondaryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_secondary_button, this, true);
        
        cardView = view.findViewById(R.id.button_card);
        container = view.findViewById(R.id.button_container);
        contentLayout = view.findViewById(R.id.content_layout);
        imgIcon = view.findViewById(R.id.button_icon);
        tvText = view.findViewById(R.id.button_text);
        progressBar = view.findViewById(R.id.button_progress);

        // Scale anim on touch
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
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SecondaryButton);
            
            String text = a.getString(R.styleable.SecondaryButton_sb_text);
            Drawable icon = a.getDrawable(R.styleable.SecondaryButton_sb_icon);
            boolean loading = a.getBoolean(R.styleable.SecondaryButton_sb_loading, false);
            int sizeInt = a.getInt(R.styleable.SecondaryButton_sb_size, 1); // default medium
            
            if (text != null) {
                tvText.setText(text);
            }
            if (icon != null) {
                imgIcon.setImageDrawable(icon);
                imgIcon.setVisibility(View.VISIBLE);
            }
            
            Size size = Size.MEDIUM;
            if (sizeInt == 0) size = Size.SMALL;
            else if (sizeInt == 2) size = Size.LARGE;
            setSize(size);
            
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

    public void setSize(Size size) {
        ViewGroup.LayoutParams lp = container.getLayoutParams();
        int heightDp;
        float textSizeSp;
        
        switch (size) {
            case SMALL:
                heightDp = 36;
                textSizeSp = 12f;
                break;
            case LARGE:
                heightDp = 52;
                textSizeSp = 16f;
                break;
            case MEDIUM:
            default:
                heightDp = 44;
                textSizeSp = 14f;
                break;
        }
        
        int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
        lp.height = heightPx;
        container.setLayoutParams(lp);
        
        tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp);
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
        } else {
            cardView.setAlpha(0.5f);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        cardView.setOnClickListener(l);
    }
}
