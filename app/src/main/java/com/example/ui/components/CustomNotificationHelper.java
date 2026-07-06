package com.example.ui.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomNotificationHelper {

    public static void showSuccessSnackbar(View parentView, String message) {
        showCustomSnackbar(parentView, message, R.drawable.ic_check, R.color.accent_color);
    }

    public static void showErrorSnackbar(View parentView, String message) {
        showCustomSnackbar(parentView, message, android.view.View.NO_ID, R.color.error);
    }

    public static void showWarningSnackbar(View parentView, String message) {
        showCustomSnackbar(parentView, message, android.view.View.NO_ID, R.color.warning);
    }

    public static void showInfoSnackbar(View parentView, String message) {
        showCustomSnackbar(parentView, message, R.drawable.ic_history, R.color.accent_color);
    }

    private static void showCustomSnackbar(View parentView, String message, int iconRes, int colorRes) {
        Snackbar snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        
        // Dark, themed background
        snackbarView.setBackgroundColor(parentView.getContext().getResources().getColor(R.color.secondary_bg));
        
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setTextColor(parentView.getContext().getResources().getColor(R.color.text_primary));
            textView.setTextSize(14);
            if (iconRes != android.view.View.NO_ID) {
                textView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
                textView.setCompoundDrawablePadding(12);
                // Color filter tint on icon drawable if possible, else rely on vector default
            }
        }
        
        snackbar.show();
    }

    public static void showSuccessToast(Context context, String message) {
        showCustomToast(context, message, R.drawable.ic_check, R.color.accent_color);
    }

    public static void showErrorToast(Context context, String message) {
        showCustomToast(context, message, android.view.View.NO_ID, R.color.error);
    }

    private static void showCustomToast(Context context, String message, int iconRes, int colorRes) {
        View layout = LayoutInflater.from(context).inflate(R.layout.view_custom_toast, null);
        
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        
        ImageView icon = layout.findViewById(R.id.toast_icon);
        if (iconRes != android.view.View.NO_ID) {
            icon.setImageResource(iconRes);
            icon.setVisibility(View.VISIBLE);
            icon.setColorFilter(context.getResources().getColor(colorRes));
        } else {
            icon.setVisibility(View.GONE);
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
