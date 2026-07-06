package com.example.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.example.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class CustomDialogs {

    public interface OnInputSubmittedListener {
        void onSubmitted(String input);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(int optionId);
    }

    // Static option IDs for Bottom Sheets
    public static final int OPT_COPY_NAME = 1;
    public static final int OPT_RENAME = 2;
    public static final int OPT_PIN = 3;
    public static final int OPT_FAVORITE = 4;
    public static final int OPT_DELETE = 5;

    public static final int OPT_OPEN_PROJECT = 6;
    public static final int OPT_EDIT_PROJECT = 7;
    public static final int OPT_DELETE_PROJECT = 8;

    public static final int OPT_EDIT_TRANSLATION = 9;
    public static final int OPT_COPY_ORIGINAL = 10;
    public static final int OPT_COPY_TRANSLATED = 11;
    public static final int OPT_BOOKMARK = 12;
    public static final int OPT_ADD_COMMENT = 13;

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

    public static Dialog showInputDialog(Context context, String title, String initialText, String hint, OnInputSubmittedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_input);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = dialog.findViewById(R.id.dialog_title);
        EditText etInput = dialog.findViewById(R.id.dialog_input_field);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);
        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);

        if (title != null) tvTitle.setText(title);
        if (initialText != null) etInput.setText(initialText);
        if (hint != null) etInput.setHint(hint);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            String val = etInput.getText().toString().trim();
            if (listener != null) {
                listener.onSubmitted(val);
            }
            dialog.dismiss();
        });

        dialog.show();
        return dialog;
    }

    public static Dialog showAboutDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnClose = dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        return dialog;
    }

    public static void showFileOptionsBottomSheet(Context context, String title, OnOptionSelectedListener listener) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_file_options, null);
        bottomSheet.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (title != null) tvTitle.setText(title);

        view.findViewById(R.id.option_copy_name).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_COPY_NAME);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_rename).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_RENAME);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_pin).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_PIN);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_favorite).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_FAVORITE);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_DELETE);
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    public static void showProjectOptionsBottomSheet(Context context, String title, OnOptionSelectedListener listener) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_project_options, null);
        bottomSheet.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (title != null) tvTitle.setText(title);

        view.findViewById(R.id.option_open_project).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_OPEN_PROJECT);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_edit_details).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_EDIT_PROJECT);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_delete_project).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_DELETE_PROJECT);
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }

    public static void showTranslationOptionsBottomSheet(Context context, String title, OnOptionSelectedListener listener) {
        BottomSheetDialog bottomSheet = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_translation_options, null);
        bottomSheet.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (title != null) tvTitle.setText(title);

        view.findViewById(R.id.option_edit_translation).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_EDIT_TRANSLATION);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_copy_original).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_COPY_ORIGINAL);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_copy_translated).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_COPY_TRANSLATED);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_bookmark).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_BOOKMARK);
            bottomSheet.dismiss();
        });

        view.findViewById(R.id.option_add_comment).setOnClickListener(v -> {
            if (listener != null) listener.onOptionSelected(OPT_ADD_COMMENT);
            bottomSheet.dismiss();
        });

        bottomSheet.show();
    }
}
