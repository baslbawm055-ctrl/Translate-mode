package com.example.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.adapters.TranslationEditorAdapter;
import com.example.dialogs.CustomDialogs;
import com.example.translation.TranslationItem;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class TranslationEditorActivity extends AppCompatActivity {

    private RecyclerView rvEditor;
    private TranslationEditorAdapter adapter;
    private List<TranslationItem> itemsList = new ArrayList<>();

    private TextView tvToolbarTitle;
    private TextView tvToolbarSubtitle;
    private EditText etSearch;
    private ImageButton btnClearSearch;
    private MaterialCardView cardSearchBar;

    // Stats TextViews
    private TextView tvStatTotal;
    private TextView tvStatTranslated;
    private TextView tvStatUntranslated;
    private TextView tvStatEdited;

    // Filters
    private ChipGroup chipGroupFilters;
    private String currentFilterType = "ALL";

    // Selection Header
    private LinearLayout layoutSelectionHeader;
    private TextView tvSelectionCount;
    private Button btnSelectAll;
    private Button btnDeselectAll;

    // Bottom Buttons
    private Button btnTranslateSelected;
    private Button btnTranslateAll;
    private Button btnClearAll;
    private Button btnSave;
    private Button btnCancel;

    // Empty state & Loading
    private LinearLayout layoutEmptyState;
    private LinearLayout layoutLoading;

    private String fileName = "strings.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_editor);

        // Bind Views
        rvEditor = findViewById(R.id.rv_translation_editor);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarSubtitle = findViewById(R.id.tv_toolbar_subtitle);
        etSearch = findViewById(R.id.et_search);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        cardSearchBar = findViewById(R.id.card_search_bar);

        tvStatTotal = findViewById(R.id.tv_stat_total);
        tvStatTranslated = findViewById(R.id.tv_stat_translated);
        tvStatUntranslated = findViewById(R.id.tv_stat_untranslated);
        tvStatEdited = findViewById(R.id.tv_stat_edited);

        chipGroupFilters = findViewById(R.id.chip_group_filters);

        layoutSelectionHeader = findViewById(R.id.layout_selection_header);
        tvSelectionCount = findViewById(R.id.tv_selection_count);
        btnSelectAll = findViewById(R.id.btn_select_all);
        btnDeselectAll = findViewById(R.id.btn_deselect_all);

        btnTranslateSelected = findViewById(R.id.btn_bottom_translate_selected);
        btnTranslateAll = findViewById(R.id.btn_bottom_translate_all);
        btnClearAll = findViewById(R.id.btn_bottom_clear_all);
        btnSave = findViewById(R.id.btn_bottom_save);
        btnCancel = findViewById(R.id.btn_bottom_cancel);

        layoutEmptyState = findViewById(R.id.layout_empty_state);
        layoutLoading = findViewById(R.id.layout_loading);

        // Fetch custom data or fallback to mock
        String customFileName = getIntent().getStringExtra("file_name");
        if (customFileName != null && !customFileName.isEmpty()) {
            fileName = customFileName;
        }
        tvToolbarSubtitle.setText(fileName);

        // Get intent items if available
        List<TranslationItem> passedItems = (List<TranslationItem>) getIntent().getSerializableExtra("items_list");
        if (passedItems != null && !passedItems.isEmpty()) {
            itemsList.addAll(passedItems);
        } else {
            // Load high-fidelity default mock data of 100 items for the preview/sandbox
            itemsList = generateMockItems(100);
        }

        setupRecyclerView();
        setupToolbarActions();
        setupSearch();
        setupFilters();
        setupBottomActions();
        setupSelectionHeader();

        updateStatistics();
    }

    private void setupRecyclerView() {
        rvEditor.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TranslationEditorAdapter(this, itemsList);
        rvEditor.setAdapter(adapter);

        adapter.setOnSelectionChangedListener(new TranslationEditorAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int selectedCount) {
                if (selectedCount > 0) {
                    layoutSelectionHeader.setVisibility(View.VISIBLE);
                    tvSelectionCount.setText("تم اختيار: " + selectedCount + " عبارات");
                    btnTranslateSelected.setEnabled(true);
                    btnTranslateSelected.setAlpha(1.0f);
                } else {
                    layoutSelectionHeader.setVisibility(View.GONE);
                    btnTranslateSelected.setEnabled(false);
                    btnTranslateSelected.setAlpha(0.5f);
                }
            }

            @Override
            public void onStatsUpdated() {
                updateStatistics();
            }
        });

        // Initially disable translate selected since nothing is selected
        btnTranslateSelected.setEnabled(false);
        btnTranslateSelected.setAlpha(0.5f);
    }

    private void setupToolbarActions() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        ImageButton btnSearch = findViewById(R.id.btn_toolbar_search);
        btnSearch.setOnClickListener(v -> {
            if (cardSearchBar.getVisibility() == View.VISIBLE) {
                cardSearchBar.setVisibility(View.GONE);
                etSearch.setText("");
            } else {
                cardSearchBar.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
            }
        });

        ImageButton btnTranslateAllToolbar = findViewById(R.id.btn_toolbar_translate_all);
        btnTranslateAllToolbar.setOnClickListener(v -> triggerTranslateAllFlow());

        ImageButton btnSaveToolbar = findViewById(R.id.btn_toolbar_save);
        btnSaveToolbar.setOnClickListener(v -> triggerSaveFlow());

        ImageButton btnOverflow = findViewById(R.id.btn_toolbar_overflow);
        btnOverflow.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(TranslationEditorActivity.this, btnOverflow);
            popup.getMenu().add(0, 1, 0, "توليد 10 عبارات (سرعة فائقة)");
            popup.getMenu().add(0, 2, 0, "توليد 100 عبارة (افتراضي)");
            popup.getMenu().add(0, 3, 0, "توليد 1000 عبارة (تحميل ضخم)");
            popup.getMenu().add(0, 4, 0, "توليد 10000 عبارة (اختبار الأداء القاسي)");
            popup.getMenu().add(0, 5, 0, "عن محرر الترجمة");

            popup.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == 1) {
                    loadSelectedMockDataScale(10);
                    return true;
                } else if (id == 2) {
                    loadSelectedMockDataScale(100);
                    return true;
                } else if (id == 3) {
                    loadSelectedMockDataScale(1000);
                    return true;
                } {
                    if (id == 4) {
                        loadSelectedMockDataScale(10000);
                        return true;
                    } else if (id == 5) {
                        CustomDialogs.showSuccessDialog(TranslationEditorActivity.this,
                                "محرر الترجمة الاحترافي BL",
                                "تم تصميمه بالكامل بلغة Java وبمكونات XML الأصلية لمحاكاة أسلوب MT Manager الرائد في تحرير نصوص التعريب.\n\nيدعم المحرر استجابة سلسة، وتراجع غير محدود، وحماية وتظليل علامات التنسيق (Placeholders)، بالإضافة لتصفية سريعة بلمسة واحدة.",
                                null
                        );
                        return true;
                    }
                }
                return false;
            });
            popup.show();
        });
    }

    private void loadSelectedMockDataScale(int count) {
        layoutLoading.setVisibility(View.VISIBLE);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            itemsList = generateMockItems(count);
            setupRecyclerView();
            updateStatistics();
            layoutLoading.setVisibility(View.GONE);
            Toast.makeText(TranslationEditorActivity.this, "تم توليد " + count + " نصوص بنجاح للفحص والتجربة!", Toast.LENGTH_LONG).show();
        }, 800);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                adapter.filter(query, currentFilterType);
                checkEmptyStates();
            }
        });

        btnClearSearch.setOnClickListener(v -> etSearch.setText(""));
    }

    private void setupFilters() {
        chipGroupFilters.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_filter_all) {
                currentFilterType = "ALL";
            } else if (checkedId == R.id.chip_filter_translated) {
                currentFilterType = "TRANSLATED";
            } else if (checkedId == R.id.chip_filter_untranslated) {
                currentFilterType = "UNTRANSLATED";
            } else if (checkedId == R.id.chip_filter_edited) {
                currentFilterType = "EDITED";
            } else if (checkedId == R.id.chip_filter_placeholders) {
                currentFilterType = "PLACEHOLDERS";
            } else if (checkedId == R.id.chip_filter_html) {
                currentFilterType = "HTML";
            } else if (checkedId == R.id.chip_filter_string) {
                currentFilterType = "STRING";
            } else if (checkedId == R.id.chip_filter_array) {
                currentFilterType = "ARRAY";
            } else if (checkedId == R.id.chip_filter_plural) {
                currentFilterType = "PLURAL";
            } else if (checkedId == R.id.chip_filter_favorite) {
                currentFilterType = "FAVORITE";
            } else {
                currentFilterType = "ALL";
            }
            adapter.filter(etSearch.getText().toString(), currentFilterType);
            checkEmptyStates();
        });
    }

    private void setupSelectionHeader() {
        btnSelectAll.setOnClickListener(v -> adapter.selectAll());
        btnDeselectAll.setOnClickListener(v -> adapter.deselectAll());
    }

    private void setupBottomActions() {
        btnTranslateSelected.setOnClickListener(v -> triggerTranslateSelectedFlow());
        btnTranslateAll.setOnClickListener(v -> triggerTranslateAllFlow());
        btnClearAll.setOnClickListener(v -> triggerClearAllFlow());
        btnSave.setOnClickListener(v -> triggerSaveFlow());
        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    private void triggerTranslateSelectedFlow() {
        final List<TranslationItem> selected = adapter.getSelectedItems();
        if (selected.isEmpty()) {
            Toast.makeText(this, "يرجى تحديد العبارات أولاً للتوليد!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Dialog progress = CustomDialogs.showProgressDialog(this);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            progress.dismiss();
            
            // Apply automated translation mockup to selected entries
            for (TranslationItem item : selected) {
                String original = item.getOriginalText();
                String trans = original + " [تعريب ذكي]";
                if ("Settings".equalsIgnoreCase(original)) trans = "الإعدادات";
                else if ("History".equalsIgnoreCase(original)) trans = "السجل";
                else if ("Home".equalsIgnoreCase(original)) trans = "الرئيسية";

                item.setTranslatedText(trans);
                item.setAiGenerated(true);
                item.setEditedManually(false);
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            }
            
            adapter.deselectAll();
            adapter.notifyDataSetChanged();
            updateStatistics();
            
            CustomDialogs.showSuccessDialog(TranslationEditorActivity.this,
                    "اكتملت الترجمة الفورية للمحدد",
                    "تم تعريب " + selected.size() + " عبارات بنجاح ومزامنتها بذاكرة الترجمة المؤقتة.",
                    null
            );
        }, 1200);
    }

    private void triggerTranslateAllFlow() {
        final Dialog progress = CustomDialogs.showProgressDialog(this);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            progress.dismiss();
            
            int translatedCount = 0;
            for (TranslationItem item : itemsList) {
                if (item.getTranslatedText().trim().isEmpty() || item.getStatus() == TranslationItem.STATUS_PENDING) {
                    String original = item.getOriginalText();
                    String trans = original + " [تعريب تلقائي]";
                    if ("Cancel".equalsIgnoreCase(original)) trans = "إلغاء";
                    else if ("Confirm".equalsIgnoreCase(original)) trans = "تأكيد";
                    else if ("Success".equalsIgnoreCase(original)) trans = "ناجح";
                    
                    item.setTranslatedText(trans);
                    item.setAiGenerated(true);
                    item.setEditedManually(false);
                    item.setStatus(TranslationItem.STATUS_SUCCESS);
                    translatedCount++;
                }
            }
            
            adapter.notifyDataSetChanged();
            updateStatistics();
            
            CustomDialogs.showSuccessDialog(TranslationEditorActivity.this,
                    "اكتمل تعريب كافة العبارات الفارغة",
                    "تم كشف وترجمة " + translatedCount + " عبارات فارغة تلقائياً بنجاح.",
                    null
            );
        }, 1500);
    }

    private void triggerClearAllFlow() {
        CustomDialogs.showConfirmDialog(this,
                "تفريغ كامل نصوص الترجمة",
                "هل أنت متأكد من رغبتك في مسح وتفريغ كامل نصوص الترجمة الحالية؟ لا يمكن استرجاع النصوص إلا عبر إلغاء التعديلات قبل الحفظ.",
                () -> {
                    for (TranslationItem item : itemsList) {
                        item.setTranslatedText("");
                        item.setEditedManually(false);
                        item.setAiGenerated(false);
                        item.setStatus(TranslationItem.STATUS_PENDING);
                    }
                    adapter.notifyDataSetChanged();
                    updateStatistics();
                    Toast.makeText(this, "تم تفريغ كافة نصوص الترجمة بنجاح.", Toast.LENGTH_SHORT).show();
                },
                null
        );
    }

    private void triggerSaveFlow() {
        final Dialog progress = CustomDialogs.showProgressDialog(this);
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            progress.dismiss();
            
            int total = adapter.getTotalCount();
            int done = adapter.getTranslatedCount();
            int edited = adapter.getEditedCount();
            
            CustomDialogs.showSuccessDialog(TranslationEditorActivity.this,
                    "تم الحفظ المؤقت بنجاح!",
                    "تم الاحتفاظ بكافة التعديلات بنجاح في ذاكرة التطبيق المؤقتة.\n\nإجمالي العبارات: " + total + "\nالعبارات المعربة: " + done + "\nالعبارات المعدلة يدوياً: " + edited + "\n\nجاهز للتصدير النهائي والكتابة للقرص في المرحلة المقبلة!",
                    () -> finish()
            );
        }, 1000);
    }

    private void updateStatistics() {
        int total = adapter.getTotalCount();
        int trans = adapter.getTranslatedCount();
        int untrans = adapter.getUntranslatedCount();
        int edited = adapter.getEditedCount();

        tvStatTotal.setText(String.valueOf(total));
        tvStatTranslated.setText(String.valueOf(trans));
        tvStatUntranslated.setText(String.valueOf(untrans));
        tvStatEdited.setText(String.valueOf(edited));
    }

    private void checkEmptyStates() {
        if (adapter.getItemCount() == 0) {
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        CustomDialogs.showConfirmDialog(this,
                "إلغاء التعديلات والخروج",
                "هل أنت متأكد من رغبتك في إغلاق المحرر وإلغاء كافة التغييرات غير المحفوظة؟",
                () -> super.onBackPressed(),
                null
        );
    }

    // --- High-fidelity Resource Mock Data Generator ---
    private List<TranslationItem> generateMockItems(int count) {
        List<TranslationItem> list = new ArrayList<>();
        
        // Base realistic resources
        String[][] baseWords = {
                {"app_name", "BL Manager", "string"},
                {"settings_title", "Settings", "string"},
                {"cancel_btn", "Cancel", "string"},
                {"confirm_btn", "Confirm", "string"},
                {"success_msg", "Operation completed successfully!", "string"},
                {"error_msg", "An unexpected error occurred. Please try again later.", "string"},
                {"search_hint", "Search for files, folders, or strings...", "string"},
                {"recent_files", "Recent Files", "string"},
                {"quick_actions", "Quick Actions", "string"},
                {"theme_light", "Light Mode", "string"},
                {"theme_dark", "Dark Mode", "string"},
                {"welcome_message", "Welcome back, %s! Nice to see you.", "string"},
                {"item_deleted_toast", "Deleted %1$d files from %2$s directory.", "string"},
                {"comment_prompt", "Add your comment for {name} here:", "string"},
                {"html_desc", "Configure your <b>advanced</b> connection preferences <a href=\"#\">here</a>.", "string"},
                {"plural_untranslated_files", "File", "plural"},
                {"array_languages", "English", "array"}
        };

        for (int i = 0; i < count; i++) {
            int baseIdx = i % baseWords.length;
            String key = baseWords[baseIdx][0];
            String text = baseWords[baseIdx][1];
            String type = baseWords[baseIdx][2];

            // Add unique suffix to make keys and texts distinct at scale
            if (i >= baseWords.length) {
                key = key + "_" + (i / baseWords.length);
                if (text.contains("%s")) {
                    text = text.replace("%s", "User_" + i);
                } else {
                    text = text + " [Item " + i + "]";
                }
            }

            TranslationItem item = new TranslationItem(key, text);
            item.setResourceType(type);
            item.setFilePath("res/values/strings.xml");
            item.setLineNumber(10 + i * 3);
            item.setResourceId("0x7f0f" + String.format("%04x", 12 + i));
            item.setComments("تسمية توضيحية لرمز المورد: " + key);

            if ("plural".equals(type)) {
                String[] q = {"zero", "one", "two", "few", "many", "other"};
                item.setPluralQuantity(q[i % q.length]);
            } else if ("array".equals(type)) {
                item.setArrayIndex(i % 5);
            }

            // Mock some initial translation states to look natural
            if (i % 3 == 0) {
                // Translated
                String ar = text + " [مترجم]";
                if (text.startsWith("BL Manager")) ar = "مترجم BL ذكي";
                else if (text.startsWith("Settings")) ar = "الإعدادات";
                else if (text.startsWith("Cancel")) ar = "إلغاء";
                else if (text.startsWith("Confirm")) ar = "تأكيد";
                
                item.setTranslatedText(ar);
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            } else if (i % 5 == 0) {
                // Edited manually
                String ar = text + " [معدل يدوياً]";
                if (text.startsWith("Settings")) ar = "خيارات الإعدادات";
                item.setTranslatedText(ar);
                item.setEditedManually(true);
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            } else {
                // Untranslated
                item.setTranslatedText("");
                item.setStatus(TranslationItem.STATUS_PENDING);
            }

            list.add(item);
        }

        return list;
    }
}
