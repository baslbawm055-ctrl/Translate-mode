package com.example.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomPopupMenuHelper {

    public interface OnMenuItemClickListener {
        void onItemClick(MenuItem item);
    }

    @SuppressLint("RestrictedApi")
    public static PopupMenu showPopupMenu(Context context, View anchor, int menuRes, OnMenuItemClickListener listener) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.inflate(menuRes);

        // Force icons to show using reflection
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
            return true;
        });

        popup.show();
        return popup;
    }
}
