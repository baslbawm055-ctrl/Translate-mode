package com.example.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import com.example.R;
import com.google.android.material.button.MaterialButton;

public class CustomDialogs {

    public static Dialog showProgressDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        dialog.show();
        return dialog;
    }

    public static Dialog showSuccessDialog(Context context, String title, String message, Runnable onOk) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.dialog_success_title);
        TextView tvMsg = dialog.findViewById(R.id.dialog_success_msg);
        MaterialButton btnOk = dialog.findViewById(R.id.btn_dialog_success_ok);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMsg.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onOk != null) onOk.run();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showErrorDialog(Context context, String title, String message, Runnable onOk) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.dialog_error_title);
        TextView tvMsg = dialog.findViewById(R.id.dialog_error_msg);
        MaterialButton btnOk = dialog.findViewById(R.id.btn_dialog_error_ok);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMsg.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onOk != null) onOk.run();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showConfirmDialog(Context context, String title, String message, Runnable onOk, Runnable onCancel) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.dialog_confirm_title);
        TextView tvMsg = dialog.findViewById(R.id.dialog_confirm_msg);
        MaterialButton btnOk = dialog.findViewById(R.id.btn_dialog_confirm_ok);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_dialog_confirm_cancel);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMsg.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (onOk != null) onOk.run();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (onCancel != null) onCancel.run();
        });

        dialog.show();
        return dialog;
    }
}
