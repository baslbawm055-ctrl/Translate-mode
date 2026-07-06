package com.example.ui.cards;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class TranslationItemCardView extends FrameLayout {

    private MaterialCardView cardView;
    private TextView tvOriginalText;
    private TextView tvTranslatedText;
    private ImageView imgEdited;
    private ImageView imgDone;
    private View expandableLayout;
    private TextView tvCharCount;
    private ImageButton btnBookmark;
    private ImageButton btnComment;

    private boolean isExpanded = false;
    private boolean isBookmarked = false;

    public TranslationItemCardView(@NonNull Context context) {
        this(context, null);
    }

    public TranslationItemCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TranslationItemCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_translation_item_card, this, true);
        
        cardView = view.findViewById(R.id.card_view);
        tvOriginalText = view.findViewById(R.id.tv_original_text);
        tvTranslatedText = view.findViewById(R.id.tv_translated_text);
        imgEdited = view.findViewById(R.id.img_edited);
        imgDone = view.findViewById(R.id.img_done);
        expandableLayout = view.findViewById(R.id.expandable_layout);
        tvCharCount = view.findViewById(R.id.tv_char_count);
        btnBookmark = view.findViewById(R.id.btn_bookmark);
        btnComment = view.findViewById(R.id.btn_comment);

        cardView.setOnClickListener(v -> toggleExpansion());

        btnBookmark.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;
            updateBookmarkUI();
        });

        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    cardView.animate().scaleX(0.99f).scaleY(0.99f).setDuration(80).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                    break;
            }
            return false;
        });
    }

    public void setOriginalText(String text) {
        tvOriginalText.setText(text);
        updateCharCount();
    }

    public void setTranslatedText(String text) {
        tvTranslatedText.setText(text);
        updateCharCount();
    }

    public void setEdited(boolean edited) {
        imgEdited.setVisibility(edited ? View.VISIBLE : View.GONE);
    }

    public void setDone(boolean done) {
        imgDone.setVisibility(done ? View.VISIBLE : View.GONE);
    }

    private void updateCharCount() {
        int originalLength = tvOriginalText.getText() != null ? tvOriginalText.getText().length() : 0;
        int translatedLength = tvTranslatedText.getText() != null ? tvTranslatedText.getText().length() : 0;
        tvCharCount.setText("الحروف: " + originalLength + " ➔ " + translatedLength);
    }

    public void toggleExpansion() {
        isExpanded = !isExpanded;
        TransitionManager.beginDelayedTransition((ViewGroup) getParent());
        expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (isExpanded) {
            cardView.setStrokeColor(getResources().getColor(R.color.accent_color));
            cardView.setStrokeWidth(3);
        } else {
            cardView.setStrokeColor(getResources().getColor(R.color.divider));
            cardView.setStrokeWidth(2);
        }
    }

    private void updateBookmarkUI() {
        if (isBookmarked) {
            btnBookmark.setColorFilter(getResources().getColor(R.color.warning));
        } else {
            btnBookmark.setColorFilter(getResources().getColor(R.color.text_secondary));
        }
    }

    public void setBookmarked(boolean bookmarked) {
        this.isBookmarked = bookmarked;
        updateBookmarkUI();
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setOnCommentClickListener(OnClickListener listener) {
        btnComment.setOnClickListener(listener);
    }
}
