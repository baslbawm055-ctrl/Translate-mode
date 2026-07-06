package com.example.ui.loading;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class LoadingView extends RelativeLayout {

    public enum Style {
        CIRCULAR, LINEAR, FULLSCREEN
    }

    private RelativeLayout container;
    private CircularProgressIndicator progressCircular;
    private LinearProgressIndicator progressLinear;
    private TextView tvMessage;

    public LoadingView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_loading, this, true);
        
        container = view.findViewById(R.id.loading_container);
        progressCircular = view.findViewById(R.id.progress_circular);
        progressLinear = view.findViewById(R.id.progress_linear);
        tvMessage = view.findViewById(R.id.tv_loading_message);
        
        setStyle(Style.CIRCULAR); // default
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    public void setStyle(Style style) {
        switch (style) {
            case LINEAR:
                progressCircular.setVisibility(GONE);
                progressLinear.setVisibility(VISIBLE);
                container.setBackgroundColor(Color.TRANSPARENT);
                break;
            case FULLSCREEN:
                progressCircular.setVisibility(VISIBLE);
                progressLinear.setVisibility(GONE);
                container.setBackgroundColor(getResources().getColor(R.color.primary_bg));
                break;
            case CIRCULAR:
            default:
                progressCircular.setVisibility(VISIBLE);
                progressLinear.setVisibility(GONE);
                container.setBackgroundColor(Color.TRANSPARENT);
                break;
        }
    }
}
