package com.example.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class HistoryCardView extends FrameLayout {

    private MaterialCardView cardView;
    private ImageView imgStatusBadge;
    private TextView tvOperationTitle;
    private TextView tvOperationDate;
    private ImageButton btnUndo;
    private ImageButton btnRedo;

    public HistoryCardView(@NonNull Context context) {
        this(context, null);
    }

    public HistoryCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_history_card, this, true);
        
        cardView = view.findViewById(R.id.card_view);
        imgStatusBadge = view.findViewById(R.id.img_status_badge);
        tvOperationTitle = view.findViewById(R.id.tv_operation_title);
        tvOperationDate = view.findViewById(R.id.tv_operation_date);
        btnUndo = view.findViewById(R.id.btn_undo);
        btnRedo = view.findViewById(R.id.btn_redo);

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

    public void setOperationTitle(String title) {
        tvOperationTitle.setText(title);
    }

    public void setOperationDate(String date) {
        tvOperationDate.setText(date);
    }

    public void setStatus(boolean success) {
        if (success) {
            imgStatusBadge.setImageResource(R.drawable.ic_check);
            imgStatusBadge.setColorFilter(getResources().getColor(R.color.accent_color));
        } else {
            imgStatusBadge.setImageResource(R.drawable.ic_history);
            imgStatusBadge.setColorFilter(getResources().getColor(R.color.error));
        }
    }

    public void setOnUndoClickListener(OnClickListener listener) {
        btnUndo.setOnClickListener(listener);
    }

    public void setOnRedoClickListener(OnClickListener listener) {
        btnRedo.setOnClickListener(listener);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        cardView.setOnClickListener(l);
    }
}
