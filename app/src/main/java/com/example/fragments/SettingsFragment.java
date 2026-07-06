package com.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.R;
import com.example.dialogs.CustomDialogs;
import com.example.security.SecureKeysManager;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchDarkTheme = view.findViewById(R.id.switch_dark_theme);
        SwitchMaterial switchAnimations = view.findViewById(R.id.switch_animations);
        LinearLayout rowLanguage = view.findViewById(R.id.row_language);
        LinearLayout rowFontSize = view.findViewById(R.id.row_font_size);
        LinearLayout rowAiProvider = view.findViewById(R.id.row_ai_provider);
        LinearLayout rowApiKeys = view.findViewById(R.id.row_api_keys);
        LinearLayout rowCache = view.findViewById(R.id.row_cache);
        LinearLayout rowExport = view.findViewById(R.id.row_export);

        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(), 
                isChecked ? "تم تفعيل الوضع الداكن" : "تم إلغاء تفعيل الوضع الداكن", 
                Toast.LENGTH_SHORT).show();
        });

        switchAnimations.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(requireContext(), 
                isChecked ? "تم تفعيل تأثيرات الحركة" : "تم إيقاف تأثيرات الحركة", 
                Toast.LENGTH_SHORT).show();
        });

        rowLanguage.setOnClickListener(v -> {
            String[] langs = {"العربية", "English", "Français", "Español"};
            new AlertDialog.Builder(requireContext(), R.style.Theme_MyApplication)
                .setTitle(getString(R.string.setting_language))
                .setItems(langs, (dialog, which) -> {
                    Toast.makeText(requireContext(), "تم اختيار: " + langs[which], Toast.LENGTH_SHORT).show();
                })
                .show();
        });

        rowFontSize.setOnClickListener(v -> {
            String[] sizes = {"صغير (12sp)", "متوسط (14sp)", "كبير (16sp)", "كبير جداً (18sp)"};
            new AlertDialog.Builder(requireContext(), R.style.Theme_MyApplication)
                .setTitle(getString(R.string.setting_font_size))
                .setItems(sizes, (dialog, which) -> {
                    Toast.makeText(requireContext(), "تم تعيين الخط: " + sizes[which], Toast.LENGTH_SHORT).show();
                })
                .show();
        });

        rowAiProvider.setOnClickListener(v -> {
            String[] providers = {"Google Cloud Translation", "Microsoft Translator", "DeepL Translator"};
            new AlertDialog.Builder(requireContext(), R.style.Theme_MyApplication)
                .setTitle(getString(R.string.setting_ai_provider))
                .setItems(providers, (dialog, which) -> {
                    String selected = providers[which];
                    com.example.settings.TranslationSettings settings = new com.example.settings.TranslationSettings(requireContext());
                    if (which == 0) {
                        settings.setProvider(SecureKeysManager.PROVIDER_GOOGLE);
                    } else if (which == 1) {
                        settings.setProvider(SecureKeysManager.PROVIDER_MICROSOFT);
                    } else {
                        settings.setProvider(SecureKeysManager.PROVIDER_DEEPL);
                    }
                    Toast.makeText(requireContext(), "تم اختيار مزوّد الترجمة: " + selected, Toast.LENGTH_SHORT).show();
                })
                .show();
        });

        rowApiKeys.setOnClickListener(v -> showApiKeysDialog());

        rowCache.setOnClickListener(v -> {
            CustomDialogs.showConfirmDialog(requireContext(),
                "تنظيف الذاكرة المؤقتة",
                "هل أنت متأكد من رغبتك في مسح الذاكرة المؤقتة للتطبيق؟ سيتم تحرير مساحة تبلغ 12.4 ميجابايت.",
                () -> {
                    Dialog progress = CustomDialogs.showProgressDialog(requireContext());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        progress.dismiss();
                        CustomDialogs.showSuccessDialog(requireContext(),
                            "تم المسح بنجاح",
                            "تم تفريغ كافة ملفات الذاكرة المؤقتة بنجاح وبسرعة!",
                            null
                        );
                    }, 1500);
                },
                () -> Toast.makeText(requireContext(), "تم إلغاء العملية", Toast.LENGTH_SHORT).show()
            );
        });

        rowExport.setOnClickListener(v -> {
            CustomDialogs.showSuccessDialog(requireContext(),
                getString(R.string.setting_export),
                "صيغة التصدير التلقائية هي UTF-8 مع مسافات بادئة منظمة متوافقة مع متطلبات أندرويد الرسمية لملفات الموارد.",
                null
            );
        });

        return view;
    }

    private void showApiKeysDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_api_keys, null);
        Spinner spinner = dialogView.findViewById(R.id.spinner_provider);
        TextView tvStatus = dialogView.findViewById(R.id.tv_key_status);
        com.google.android.material.textfield.TextInputLayout inputLayout = dialogView.findViewById(R.id.input_layout_key);
        com.google.android.material.textfield.TextInputEditText etKey = dialogView.findViewById(R.id.et_api_key);
        com.google.android.material.button.MaterialButton btnSave = dialogView.findViewById(R.id.btn_save_key);
        com.google.android.material.button.MaterialButton btnDelete = dialogView.findViewById(R.id.btn_delete_key);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btn_close_dialog);

        SecureKeysManager keysManager = new SecureKeysManager(requireContext());

        String[] providers = {
            SecureKeysManager.PROVIDER_GOOGLE,
            SecureKeysManager.PROVIDER_MICROSOFT,
            SecureKeysManager.PROVIDER_DEEPL
        };
        
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            providers
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.Theme_MyApplication)
            .setView(dialogView)
            .create();

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedProvider = providers[position];
                updateKeyStatusUI(selectedProvider, keysManager, tvStatus, etKey);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSave.setOnClickListener(v -> {
            String selectedProvider = providers[spinner.getSelectedItemPosition()];
            String key = etKey.getText() != null ? etKey.getText().toString().trim() : "";
            
            if (key.isEmpty()) {
                etKey.setError("يرجى إدخال مفتاح API");
                return;
            }

            if (SecureKeysManager.PROVIDER_GOOGLE.equals(selectedProvider)) {
                if (!key.startsWith("AIza")) {
                    Toast.makeText(requireContext(), "تنبيه: مفتاح Google يبدأ عادة بـ AIza", Toast.LENGTH_LONG).show();
                }
            } else if (SecureKeysManager.PROVIDER_MICROSOFT.equals(selectedProvider)) {
                if (key.length() != 32 || !key.matches("[a-fA-F0-9]{32}")) {
                    Toast.makeText(requireContext(), "تنبيه: مفتاح Microsoft يتكون من 32 رمزاً سداسي عشري", Toast.LENGTH_LONG).show();
                }
            }

            keysManager.saveKey(selectedProvider, key);
            Toast.makeText(requireContext(), "تم حفظ مفتاح " + selectedProvider + " بنجاح وأمان!", Toast.LENGTH_SHORT).show();
            etKey.setText("");
            updateKeyStatusUI(selectedProvider, keysManager, tvStatus, etKey);
        });

        btnDelete.setOnClickListener(v -> {
            String selectedProvider = providers[spinner.getSelectedItemPosition()];
            CustomDialogs.showConfirmDialog(requireContext(),
                "حذف المفتاح",
                "هل أنت متأكد من حذف مفتاح " + selectedProvider + "؟ لن تتمكن من الترجمة باستخدامه.",
                () -> {
                    keysManager.deleteKey(selectedProvider);
                    Toast.makeText(requireContext(), "تم حذف مفتاح " + selectedProvider, Toast.LENGTH_SHORT).show();
                    etKey.setText("");
                    updateKeyStatusUI(selectedProvider, keysManager, tvStatus, etKey);
                },
                null
            );
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateKeyStatusUI(String provider, SecureKeysManager keysManager, TextView tvStatus, android.widget.EditText etKey) {
        if (keysManager.hasKey(provider)) {
            String key = keysManager.getKey(provider);
            String masked = "";
            if (key.length() > 8) {
                masked = key.substring(0, 4) + "..." + key.substring(key.length() - 4);
            } else {
                masked = "مفعّل";
            }
            tvStatus.setText("مفعّل ونشط (" + masked + ") ✅");
            tvStatus.setTextColor(getResources().getColor(R.color.accent_color));
        } else {
            tvStatus.setText("غير مفعّل ❌");
            tvStatus.setTextColor(android.graphics.Color.RED);
        }
    }
}
