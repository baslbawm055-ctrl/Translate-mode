package com.example.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class FileCardView extends FrameLayout {

    private MaterialCardView cardView;
    private ImageView imgFileIcon;
    private TextView tvFileName;
    private TextView tvFileDetails;
    private ImageView imgPin;
    private ImageView imgStar;
    private CheckBox chkSelect;

    public FileCardView(@NonNull Context context) {
        this(context, null);
    }

    public FileCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_file_card, this, true);
        
        cardView = view.findViewById(R.id.card_view);
        imgFileIcon = view.findViewById(R.id.img_file_icon);
        tvFileName = view.findViewById(R.id.tv_file_name);
        tvFileDetails = view.findViewById(R.id.tv_file_details);
        imgPin = view.findViewById(R.id.img_pin);
        imgStar = view.findViewById(R.id.img_star);
        chkSelect = view.findViewById(R.id.chk_select);

        // Tactile Press Animation
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

    public void setFileName(String name) {
        tvFileName.setText(name);
        // Change file icon color based on format
        if (name != null) {
            if (name.endsWith(".xml")) {
                imgFileIcon.setColorFilter(getResources().getColor(R.color.accent_color));
            } else if (name.endsWith(".json")) {
                imgFileIcon.setColorFilter(getResources().getColor(R.color.warning));
            } else if (name.endsWith(".arsc")) {
                imgFileIcon.setColorFilter(getResources().getColor(R.color.error));
            } else {
                imgFileIcon.setColorFilter(getResources().getColor(R.color.text_secondary));
            }
        }
    }

    public void setFileDetails(String details) {
        tvFileDetails.setText(details);
    }

    public void setPinned(boolean pinned) {
        imgPin.setVisibility(pinned ? View.VISIBLE : View.GONE);
    }

    public void setFavorite(boolean favorite) {
        imgStar.setVisibility(favorite ? View.VISIBLE : View.GONE);
    }

    public void setSelectionModeEnabled(boolean enabled) {
        chkSelect.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void setSelected(boolean selected) {
        chkSelect.setChecked(selected);
        if (selected) {
            cardView.setStrokeColor(getResources().getColor(R.color.accent_color));
            cardView.setStrokeWidth(4);
        } else {
            cardView.setStrokeColor(getResources().getColor(R.color.divider));
            cardView.setStrokeWidth(2);
        }
    }

    public boolean isSelected() {
        return chkSelect.isChecked();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        cardView.setOnClickListener(l);
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        cardView.setOnLongClickListener(l);
    }
}
