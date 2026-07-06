package com.example.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ProjectCardView extends FrameLayout {

    private MaterialCardView cardView;
    private TextView tvThumbnailLang;
    private TextView tvProjectName;
    private TextView tvProjectDescription;
    private TextView tvFileCount;
    private TextView tvProgressPercent;
    private LinearProgressIndicator progressBar;
    private TextView tvLastOpened;

    public ProjectCardView(@NonNull Context context) {
        this(context, null);
    }

    public ProjectCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProjectCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_project_card, this, true);
        
        cardView = view.findViewById(R.id.card_view);
        tvThumbnailLang = view.findViewById(R.id.tv_thumbnail_lang);
        tvProjectName = view.findViewById(R.id.tv_project_name);
        tvProjectDescription = view.findViewById(R.id.tv_project_description);
        tvFileCount = view.findViewById(R.id.tv_file_count);
        tvProgressPercent = view.findViewById(R.id.tv_progress_percent);
        progressBar = view.findViewById(R.id.project_progress);
        tvLastOpened = view.findViewById(R.id.tv_last_opened);

        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    cardView.animate().scaleX(0.98f).scaleY(0.98f).setDuration(80).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                    break;
            }
            return false;
        });
    }

    public void setProjectName(String name) {
        tvProjectName.setText(name);
    }

    public void setProjectDescription(String description) {
        tvProjectDescription.setText(description);
    }

    public void setLanguage(String langCode) {
        if (langCode != null) {
            tvThumbnailLang.setText(langCode.toUpperCase());
        }
    }

    public void setFileCount(int count) {
        tvFileCount.setText("عدد الملفات: " + count);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
        tvProgressPercent.setText(progress + "%");
    }

    public void setLastOpened(String time) {
        tvLastOpened.setText("آخر تعديل: " + time);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        cardView.setOnClickListener(l);
    }
}
