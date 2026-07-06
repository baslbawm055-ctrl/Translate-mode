package com.example.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.adapters.TranslationItemsAdapter;
import com.example.dialogs.CustomDialogs;
import com.example.repository.TranslationProgressListener;
import com.example.repository.TranslationRepository;
import com.example.translation.TranslationItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TranslationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 4224;

    private TranslationRepository repository;
    private TranslationItemsAdapter adapter;
    private final List<TranslationItem> itemsList = new ArrayList<>();

    private TextView tvFileName;
    private TextView tvFileType;
    private TextView tvFileSize;
    private TextView tvEmptyStrings;

    private Spinner spinnerSourceLang;
    private Spinner spinnerTargetLang;
    private Spinner spinnerTransMode;

    private MaterialButton btnStart;
    private MaterialButton btnCancel;

    private MaterialCardView cardProgress;
    private TextView tvProgressTitle;
    private LinearProgressIndicator progressBar;
    private TextView tvProgressCounts;
    private TextView tvProgressTimes;
    private TextView tvProgressCurrentStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        repository = new TranslationRepository(this);

        // Bind Custom Toolbar
        android.widget.ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> onBackPressed());
        }

        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("بيئة الترجمة الآلية");
        }

        tvFileName = findViewById(R.id.tv_file_name);
        tvFileType = findViewById(R.id.tv_file_type);
        tvFileSize = findViewById(R.id.tv_file_size);
        tvEmptyStrings = findViewById(R.id.tv_empty_strings);

        spinnerSourceLang = findViewById(R.id.spinner_source_lang);
        spinnerTargetLang = findViewById(R.id.spinner_target_lang);
        spinnerTransMode = findViewById(R.id.spinner_trans_mode);

        btnStart = findViewById(R.id.btn_start_translation);
        btnCancel = findViewById(R.id.btn_cancel_translation);

        cardProgress = findViewById(R.id.card_progress);
        tvProgressTitle = findViewById(R.id.tv_progress_title);
        progressBar = findViewById(R.id.progress_bar);
        tvProgressCounts = findViewById(R.id.tv_progress_counts);
        tvProgressTimes = findViewById(R.id.tv_progress_times);
        tvProgressCurrentStr = findViewById(R.id.tv_progress_current_str);

        setupSpinners();

        RecyclerView rvItems = findViewById(R.id.rv_translation_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TranslationItemsAdapter(itemsList);
        rvItems.setAdapter(adapter);

        btnStart.setOnClickListener(v -> startTranslationProcess());
        btnCancel.setOnClickListener(v -> cancelTranslationProcess());

        Uri fileUri = getIntent().getParcelableExtra("file_uri");
        String fileName = getIntent().getStringExtra("file_name");
        if (fileUri != null) {
            loadUriFile(fileUri, fileName);
        } else {
            pickFileUsingSAF();
        }
    }

    private void setupSpinners() {
        String[] srcLangs = {"كشف تلقائي (Auto)", "الإنجليزية (en)", "العربية (ar)", "الفرنسية (fr)", "الإسبانية (es)"};
        ArrayAdapter<String> srcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, srcLangs);
        srcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSourceLang.setAdapter(srcAdapter);

        String[] targetLangs = {"العربية (ar)", "الإنجليزية (en)", "الفرنسية (fr)", "الإسبانية (es)"};
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, targetLangs);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTargetLang.setAdapter(targetAdapter);

        String[] modes = {
                "ترجمة كافة النصوص",
                "ترجمة النصوص الفارغة فقط",
                "تخطي العبارات المترجمة بنجاح",
                "تخطي العبارات المتطابقة"
        };
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransMode.setAdapter(modeAdapter);
    }

    private void pickFileUsingSAF() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                loadUriFile(uri, null);
            }
        } else if (requestCode == REQUEST_CODE_PICK_FILE) {
            Toast.makeText(this, "تم إلغاء اختيار الملف.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUriFile(Uri uri, @Nullable String customName) {
        String name = customName;
        long size = 0;

        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (nameIndex != -1 && name == null) {
                    name = cursor.getString(nameIndex);
                }
                if (sizeIndex != -1) {
                    size = cursor.getLong(sizeIndex);
                }
                cursor.close();
            }
        } catch (Exception ignored) {}

        if (name == null) {
            name = "resource_file.xml";
        }

        tvFileName.setText("اسم الملف: " + name);
        tvFileType.setText("نوع التنسيق: " + getExtension(name).toUpperCase());
        tvFileSize.setText("حجم الملف: " + formatSize(size));

        Dialog progressDialog = CustomDialogs.showProgressDialog(this);

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            repository.loadFile(inputStream, name, new TranslationRepository.LoadCallback() {
                @Override
                public void onSuccess(List<TranslationItem> items) {
                    progressDialog.dismiss();
                    itemsList.clear();
                    itemsList.addAll(items);
                    adapter.notifyDataSetChanged();
                    
                    if (itemsList.isEmpty()) {
                        tvEmptyStrings.setVisibility(View.VISIBLE);
                        tvEmptyStrings.setText("لم يتم العثور على أي نصوص صالحة للترجمة في هذا الملف.");
                    } else {
                        tvEmptyStrings.setVisibility(View.GONE);
                    }
                    
                    CustomDialogs.showSuccessDialog(TranslationActivity.this,
                            "تم تحميل وتحليل الملف بنجاح!",
                            "تم استخراج " + items.size() + " نصوص صالحة للترجمة والتعريب بنجاح واحترافية.",
                            null
                    );
                }

                @Override
                public void onError(String error) {
                    progressDialog.dismiss();
                    tvEmptyStrings.setVisibility(View.VISIBLE);
                    tvEmptyStrings.setText("حدث خطأ أثناء تحميل وتحليل الملف: " + error);
                    CustomDialogs.showErrorDialog(TranslationActivity.this, "فشل قراءة الملف", error, null);
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            tvEmptyStrings.setVisibility(View.VISIBLE);
            tvEmptyStrings.setText("حدث خطأ غير متوقع: " + e.getMessage());
            CustomDialogs.showErrorDialog(this, "خطأ غير متوقع", e.getMessage(), null);
        }
    }

    private void startTranslationProcess() {
        com.example.settings.TranslationSettings settings = new com.example.settings.TranslationSettings(this);
        
        String[] srcCodes = {"auto", "en", "ar", "fr", "es"};
        String[] targetCodes = {"ar", "en", "fr", "es"};

        settings.setSourceLanguage(srcCodes[spinnerSourceLang.getSelectedItemPosition()]);
        settings.setTargetLanguage(targetCodes[spinnerTargetLang.getSelectedItemPosition()]);
        settings.setTranslationMode(spinnerTransMode.getSelectedItemPosition());

        btnStart.setEnabled(false);
        btnCancel.setVisibility(View.VISIBLE);
        cardProgress.setVisibility(View.VISIBLE);

        repository.startTranslation(new TranslationProgressListener() {
            @Override
            public void onStart(int totalItems) {
                progressBar.setMax(totalItems);
                progressBar.setProgress(0);
                tvProgressTitle.setText("جاري معالجة الترجمة الآلية...");
                tvProgressCounts.setText("المكتمل: 0 / المتبقي: " + totalItems + " (0%)");
            }

            @Override
            public void onProgress(String currentString, int translatedCount, int remainingCount, int progressPercentage, long elapsedTimeMs, long estimatedRemainingTimeMs) {
                progressBar.setProgress(translatedCount);
                tvProgressCounts.setText("المكتمل: " + translatedCount + " / المتبقي: " + remainingCount + " (" + progressPercentage + "%)");
                tvProgressTimes.setText("الوقت المستغرق: " + formatDuration(elapsedTimeMs) + " • الوقت المتبقي المقدر: " + formatDuration(estimatedRemainingTimeMs));
                tvProgressCurrentStr.setText("العبارة الحالية: " + currentString);
            }

            @Override
            public void onItemTranslated(TranslationItem item) {
                for (int i = 0; i < itemsList.size(); i++) {
                    if (itemsList.get(i).getKey().equals(item.getKey())) {
                        itemsList.set(i, item);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onComplete(List<TranslationItem> results) {
                btnStart.setEnabled(true);
                btnCancel.setVisibility(View.GONE);
                cardProgress.setVisibility(View.GONE);

                CustomDialogs.showSuccessDialog(TranslationActivity.this,
                        "اكتملت الترجمة الآلية!",
                        "تم تعريب وترجمة كامل العبارات المتاحة بنجاح!",
                        null
                );
            }

            @Override
            public void onError(String errorMessage) {
                btnStart.setEnabled(true);
                btnCancel.setVisibility(View.GONE);
                cardProgress.setVisibility(View.GONE);
                CustomDialogs.showErrorDialog(TranslationActivity.this, "فشلت عملية الترجمة", errorMessage, null);
            }
        });
    }

    private void cancelTranslationProcess() {
        repository.cancelTranslation();
        btnStart.setEnabled(true);
        btnCancel.setVisibility(View.GONE);
        cardProgress.setVisibility(View.GONE);
        Toast.makeText(this, "تم إلغاء عملية الترجمة.", Toast.LENGTH_SHORT).show();
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1);
    }

    private String formatSize(long size) {
        if (size <= 0) return "0 بايت";
        final String[] units = new String[]{"بايت", "كيلوبايت", "ميجابايت", "جيجابايت"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new java.text.DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private String formatDuration(long ms) {
        if (ms <= 0) return "0 ثانية";
        long sec = ms / 1000;
        long min = sec / 60;
        sec = sec % 60;
        if (min > 0) {
            return min + " دقيقة " + sec + " ثانية";
        }
        return sec + " ثانية";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repository.cancelTranslation();
    }
}
