package com.example.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.card.MaterialCardView;

public class IconButton extends FrameLayout {

    public enum Shape {
        CIRCULAR, SQUARE
    }

    public enum Type {
        FILLED, OUTLINED
    }

    private MaterialCardView cardView;
    private ImageView imgIcon;

    private Shape shape = Shape.CIRCULAR;
    private Type type = Type.FILLED;

    public IconButton(@NonNull Context context) {
        this(context, null);
    }

    public IconButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_icon_button, this, true);
        
        cardView = view.findViewById(R.id.button_card);
        imgIcon = view.findViewById(R.id.button_icon);

        // Click animation
        cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    cardView.animate().scaleX(0.92f).scaleY(0.92f).setDuration(80).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                    break;
            }
            return false;
        });

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
            
            Drawable icon = a.getDrawable(R.styleable.IconButton_ib_icon);
            int shapeInt = a.getInt(R.styleable.IconButton_ib_shape, 0); // default circular
            int typeInt = a.getInt(R.styleable.IconButton_ib_type, 0);   // default filled
            
            if (icon != null) {
                imgIcon.setImageDrawable(icon);
            }
            
            Shape shape = (shapeInt == 0) ? Shape.CIRCULAR : Shape.SQUARE;
            Type type = (typeInt == 0) ? Type.FILLED : Type.OUTLINED;
            
            setShape(shape);
            setType(type);
            
            a.recycle();
        }
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            imgIcon.setImageDrawable(icon);
        }
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        int radiusDp = (shape == Shape.CIRCULAR) ? 22 : 12;
        float radiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusDp, getResources().getDisplayMetrics());
        cardView.setRadius(radiusPx);
    }

    public void setType(Type type) {
        this.type = type;
        if (type == Type.FILLED) {
            cardView.setCardBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.accent_color)));
            cardView.setStrokeWidth(0);
            imgIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            cardView.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.accent_color)));
            int strokePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
            cardView.setStrokeWidth(strokePx);
            imgIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent_color)));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cardView.setEnabled(enabled);
        cardView.setClickable(enabled);
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
