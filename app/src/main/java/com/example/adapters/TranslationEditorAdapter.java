package com.example.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.translation.TranslationItem;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationEditorAdapter extends RecyclerView.Adapter<TranslationEditorAdapter.EditorViewHolder> {

    private final Context context;
    private final List<TranslationItem> fullList; // Master list of all items
    private List<TranslationItem> displayList; // Filtered list shown in UI

    // Selection Tracking
    private final Set<String> selectedKeys = new HashSet<>();
    private boolean isMultiSelectActive = false;
    private OnSelectionChangedListener selectionListener;

    // Expand Tracking (Holds key of expanded items)
    private final Set<String> expandedKeys = new HashSet<>();

    // Per-item Undo/Redo stacks
    private final Map<String, Stack<String>> undoStacks = new HashMap<>();
    private final Map<String, Stack<String>> redoStacks = new HashMap<>();
    private final Map<String, String> initialTranslations = new HashMap<>(); // For Reset

    // Placeholders Regex
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
            "%[0-9]+\\$[a-zA-Z]|%[a-zA-Z]|\\{[a-zA-Z_0-9]+\\}|\\$\\{[a-zA-Z_0-9]+\\}|<xliff:g[^>]*>.*?</xliff:g>|<[^>]+>"
    );

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
        void onStatsUpdated();
    }

    public TranslationEditorAdapter(Context context, List<TranslationItem> items) {
        this.context = context;
        this.fullList = items;
        this.displayList = new ArrayList<>(items);
        
        // Cache initial states
        for (TranslationItem item : items) {
            initialTranslations.put(item.getKey(), item.getTranslatedText());
        }
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    @NonNull
    @Override
    public EditorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_translation_editor, parent, false);
        return new EditorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditorViewHolder holder, int position) {
        TranslationItem item = displayList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // --- Search & Filter Logic ---
    public void filter(String query, String filterType) {
        displayList.clear();
        String lowerQuery = query.toLowerCase().trim();

        for (TranslationItem item : fullList) {
            // Check Search match
            boolean matchesSearch = lowerQuery.isEmpty() ||
                    item.getKey().toLowerCase().contains(lowerQuery) ||
                    item.getOriginalText().toLowerCase().contains(lowerQuery) ||
                    item.getTranslatedText().toLowerCase().contains(lowerQuery);

            if (!matchesSearch) continue;

            // Check Chip filter match
            boolean matchesFilter = false;
            switch (filterType) {
                case "ALL":
                    matchesFilter = true;
                    break;
                case "TRANSLATED":
                    matchesFilter = !item.getTranslatedText().trim().isEmpty() && item.getStatus() != TranslationItem.STATUS_PENDING;
                    break;
                case "UNTRANSLATED":
                    matchesFilter = item.getTranslatedText().trim().isEmpty() || item.getStatus() == TranslationItem.STATUS_PENDING;
                    break;
                case "EDITED":
                    matchesFilter = item.isEditedManually();
                    break;
                case "PLACEHOLDERS":
                    matchesFilter = hasPlaceholders(item.getOriginalText());
                    break;
                case "HTML":
                    matchesFilter = item.getOriginalText().contains("<") && item.getOriginalText().contains(">");
                    break;
                case "STRING":
                    matchesFilter = "string".equalsIgnoreCase(item.getResourceType());
                    break;
                case "ARRAY":
                    matchesFilter = "array".equalsIgnoreCase(item.getResourceType());
                    break;
                case "PLURAL":
                    matchesFilter = "plural".equalsIgnoreCase(item.getResourceType());
                    break;
                case "FAVORITE":
                    matchesFilter = item.isBookmarked();
                    break;
            }

            if (matchesFilter) {
                displayList.add(item);
            }
        }
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onStatsUpdated();
        }
    }

    private boolean hasPlaceholders(String text) {
        return PLACEHOLDER_PATTERN.matcher(text).find();
    }

    // --- Selection Operations ---
    public boolean isMultiSelectActive() {
        return isMultiSelectActive;
    }

    public void setMultiSelectActive(boolean active) {
        this.isMultiSelectActive = active;
        if (!active) {
            selectedKeys.clear();
        }
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedKeys.size());
        }
    }

    public void selectAll() {
        for (TranslationItem item : displayList) {
            selectedKeys.add(item.getKey());
        }
        isMultiSelectActive = true;
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedKeys.size());
        }
    }

    public void deselectAll() {
        selectedKeys.clear();
        isMultiSelectActive = false;
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(0);
        }
    }

    public Set<String> getSelectedKeys() {
        return selectedKeys;
    }

    public List<TranslationItem> getSelectedItems() {
        List<TranslationItem> selectedList = new ArrayList<>();
        for (TranslationItem item : fullList) {
            if (selectedKeys.contains(item.getKey())) {
                selectedList.add(item);
            }
        }
        return selectedList;
    }

    // --- Statistics Helpers ---
    public int getTotalCount() { return fullList.size(); }
    public int getTranslatedCount() {
        int count = 0;
        for (TranslationItem item : fullList) {
            if (!item.getTranslatedText().trim().isEmpty() && item.getStatus() != TranslationItem.STATUS_PENDING) {
                count++;
            }
        }
        return count;
    }
    public int getUntranslatedCount() {
        return getTotalCount() - getTranslatedCount();
    }
    public int getEditedCount() {
        int count = 0;
        for (TranslationItem item : fullList) {
            if (item.isEditedManually()) {
                count++;
            }
        }
        return count;
    }

    // --- Placeholder Highlighting & Warnings ---
    private CharSequence highlightPlaceholders(String text) {
        if (text == null) return "";
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            // Apply bright pink/teal styling for placeholders
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#00E5FF")), start, end, 0);
            builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
        }
        return builder;
    }

    private List<String> extractPlaceholders(String text) {
        List<String> list = new ArrayList<>();
        if (text == null) return list;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    // --- ViewHolder Implementation ---
    public class EditorViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final CheckBox cbSelect;
        private final TextView tvResourceKey;
        private final TextView tvResourceType;
        private final TextView tvStatusBadge;
        private final TextView tvOriginalText;
        private final TextView tvOriginalCharCount;
        private final TextView tvTranslationCharCount;
        private final EditText etTranslation;

        private final LinearLayout layoutWarnings;
        private final TextView tvWarningMessage;

        private final ImageButton btnTranslate;
        private final ImageButton btnCopyOriginal;
        private final ImageButton btnCopyTranslation;
        private final ImageButton btnPaste;
        private final ImageButton btnUndo;
        private final ImageButton btnRedo;
        private final ImageButton btnReset;
        private final ImageButton btnClear;
        private final ImageButton btnBookmark;
        private final ImageButton btnExpandToggle;

        private final LinearLayout layoutExpandedDetails;
        private final TextView tvDetailFilePath;
        private final TextView tvDetailLineNumber;
        private final TextView tvDetailResourceId;
        private final TextView tvDetailComments;
        private final TextView tvDetailHasHtml;
        private final TextView tvDetailHasPlaceholder;
        private final TextView tvDetailPluralQty;
        private final TextView tvDetailArrayIdx;

        private TextWatcher translationTextWatcher;
        private TranslationItem boundItem;

        public EditorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_translation_item);
            cbSelect = itemView.findViewById(R.id.cb_select);
            tvResourceKey = itemView.findViewById(R.id.tv_resource_key);
            tvResourceType = itemView.findViewById(R.id.tv_resource_type);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvOriginalText = itemView.findViewById(R.id.tv_original_text);
            tvOriginalCharCount = itemView.findViewById(R.id.tv_original_char_count);
            tvTranslationCharCount = itemView.findViewById(R.id.tv_translation_char_count);
            etTranslation = itemView.findViewById(R.id.et_translation);

            layoutWarnings = itemView.findViewById(R.id.layout_warnings);
            tvWarningMessage = itemView.findViewById(R.id.tv_warning_message);

            btnTranslate = itemView.findViewById(R.id.btn_row_translate);
            btnCopyOriginal = itemView.findViewById(R.id.btn_row_copy_original);
            btnCopyTranslation = itemView.findViewById(R.id.btn_row_copy_translation);
            btnPaste = itemView.findViewById(R.id.btn_row_paste);
            btnUndo = itemView.findViewById(R.id.btn_row_undo);
            btnRedo = itemView.findViewById(R.id.btn_row_redo);
            btnReset = itemView.findViewById(R.id.btn_row_reset);
            btnClear = itemView.findViewById(R.id.btn_row_clear);
            btnBookmark = itemView.findViewById(R.id.btn_row_bookmark);
            btnExpandToggle = itemView.findViewById(R.id.btn_row_expand_toggle);

            layoutExpandedDetails = itemView.findViewById(R.id.layout_expanded_details);
            tvDetailFilePath = itemView.findViewById(R.id.tv_detail_file_path);
            tvDetailLineNumber = itemView.findViewById(R.id.tv_detail_line_number);
            tvDetailResourceId = itemView.findViewById(R.id.tv_detail_resource_id);
            tvDetailComments = itemView.findViewById(R.id.tv_detail_comments);
            tvDetailHasHtml = itemView.findViewById(R.id.tv_detail_has_html);
            tvDetailHasPlaceholder = itemView.findViewById(R.id.tv_detail_has_placeholder);
            tvDetailPluralQty = itemView.findViewById(R.id.tv_detail_plural_qty);
            tvDetailArrayIdx = itemView.findViewById(R.id.tv_detail_array_idx);
        }

        public void bind(final TranslationItem item) {
            this.boundItem = item;

            // Remove any old text watcher to avoid duplicate triggering
            if (translationTextWatcher != null) {
                etTranslation.removeTextChangedListener(translationTextWatcher);
            }

            // Bind Basic Info
            tvResourceKey.setText(item.getKey());
            tvResourceType.setText(item.getResourceType());
            tvOriginalText.setText(highlightPlaceholders(item.getOriginalText()));

            int originalLen = item.getOriginalText() != null ? item.getOriginalText().length() : 0;
            tvOriginalCharCount.setText("الحروف: " + originalLen);

            // Bind check state
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(selectedKeys.contains(item.getKey()));
            cbSelect.setVisibility(isMultiSelectActive ? View.VISIBLE : View.GONE);
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedKeys.add(item.getKey());
                } else {
                    selectedKeys.remove(item.getKey());
                }
                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(selectedKeys.size());
                }
            });

            // Card long click starts multi-select
            cardView.setOnLongClickListener(v -> {
                if (!isMultiSelectActive) {
                    setMultiSelectActive(true);
                    selectedKeys.add(item.getKey());
                    cbSelect.setChecked(true);
                    return true;
                }
                return false;
            });

            // Bind expanded details layout state
            boolean isExpanded = expandedKeys.contains(item.getKey());
            layoutExpandedDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            btnExpandToggle.setImageResource(isExpanded ? R.drawable.ic_collapse : R.drawable.ic_expand);

            tvDetailFilePath.setText("المسار: " + item.getFilePath());
            tvDetailLineNumber.setText("رقم السطر: " + item.getLineNumber());
            tvDetailResourceId.setText("المعرف: " + item.getResourceId());
            tvDetailComments.setText("التعليقات: " + item.getComments());

            boolean hasHtml = item.getOriginalText().contains("<") && item.getOriginalText().contains(">");
            tvDetailHasHtml.setText("يحتوي على HTML: " + (hasHtml ? "نعم" : "لا"));

            boolean hasPl = hasPlaceholders(item.getOriginalText());
            tvDetailHasPlaceholder.setText("يحتوي علامات تنسيق: " + (hasPl ? "نعم" : "لا"));

            if ("plural".equalsIgnoreCase(item.getResourceType())) {
                tvDetailPluralQty.setVisibility(View.VISIBLE);
                tvDetailPluralQty.setText("الجمع: " + (item.getPluralQuantity().isEmpty() ? "other" : item.getPluralQuantity()));
            } else {
                tvDetailPluralQty.setVisibility(View.GONE);
            }

            if ("array".equalsIgnoreCase(item.getResourceType())) {
                tvDetailArrayIdx.setVisibility(View.VISIBLE);
                tvDetailArrayIdx.setText("فهرس المصفوفة: " + item.getArrayIndex());
            } else {
                tvDetailArrayIdx.setVisibility(View.GONE);
            }

            // Expand Toggle action
            btnExpandToggle.setOnClickListener(v -> {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView.getParent());
                if (expandedKeys.contains(item.getKey())) {
                    expandedKeys.remove(item.getKey());
                    layoutExpandedDetails.setVisibility(View.GONE);
                    btnExpandToggle.setImageResource(R.drawable.ic_expand);
                    cardView.setStrokeColor(context.getResources().getColor(R.color.divider));
                    cardView.setStrokeWidth(1);
                } else {
                    expandedKeys.add(item.getKey());
                    layoutExpandedDetails.setVisibility(View.VISIBLE);
                    btnExpandToggle.setImageResource(R.drawable.ic_collapse);
                    cardView.setStrokeColor(context.getResources().getColor(R.color.accent_color));
                    cardView.setStrokeWidth(2);
                }
            });

            // Touch feed animation
            cardView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        cardView.animate().scaleX(0.99f).scaleY(0.99f).setDuration(80).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        cardView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        break;
                }
                return false;
            });

            // Set translation text & Highlighted style
            etTranslation.setText(highlightPlaceholders(item.getTranslatedText()));
            int transLen = item.getTranslatedText() != null ? item.getTranslatedText().length() : 0;
            tvTranslationCharCount.setText("الحروف: " + transLen);

            // Configure warning check initially
            validatePlaceholders(item);

            // Bind Bookmark style
            updateBookmarkUI(item);

            // Bind Status Badge style
            updateStatusUI(item);

            // Create Undo/Redo tracking if not present
            if (!undoStacks.containsKey(item.getKey())) {
                Stack<String> stack = new Stack<>();
                stack.push(item.getTranslatedText());
                undoStacks.put(item.getKey(), stack);
            }
            if (!redoStacks.containsKey(item.getKey())) {
                redoStacks.put(item.getKey(), new Stack<>());
            }

            // Create text change watcher
            translationTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    if (!text.equals(item.getTranslatedText())) {
                        // User modified the text manually
                        saveToUndoHistory(item.getKey(), item.getTranslatedText());
                        item.setTranslatedText(text);
                        item.setEditedManually(true);
                        item.setStatus(TranslationItem.STATUS_SUCCESS);

                        tvTranslationCharCount.setText("الحروف: " + text.length());
                        validatePlaceholders(item);
                        updateStatusUI(item);

                        if (selectionListener != null) {
                            selectionListener.onStatsUpdated();
                        }
                    }
                }
            };
            etTranslation.addTextChangedListener(translationTextWatcher);

            // --- Row Actions Implementations ---

            // Simulate Translate (Local AI translation emulation)
            btnTranslate.setOnClickListener(v -> {
                String original = item.getOriginalText();
                String mockTranslated = original + " [مترجم]";
                if ("Settings".equalsIgnoreCase(original)) mockTranslated = "الإعدادات";
                else if ("History".equalsIgnoreCase(original)) mockTranslated = "السجل";
                else if ("Home".equalsIgnoreCase(original)) mockTranslated = "الرئيسية";
                else if ("Projects".equalsIgnoreCase(original)) mockTranslated = "المشاريع";
                else if ("Choose file".equalsIgnoreCase(original)) mockTranslated = "اختر ملفاً";
                else if ("Language".equalsIgnoreCase(original)) mockTranslated = "اللغة";
                else if ("Theme".equalsIgnoreCase(original)) mockTranslated = "المظهر";
                else if ("Confirm".equalsIgnoreCase(original)) mockTranslated = "تأكيد";
                else if ("Cancel".equalsIgnoreCase(original)) mockTranslated = "إلغاء";
                else if ("Success".equalsIgnoreCase(original)) mockTranslated = "ناجح";

                // Retain placeholder formatters in mock translation
                List<String> originalPls = extractPlaceholders(original);
                if (!originalPls.isEmpty()) {
                    StringBuilder sb = new StringBuilder(mockTranslated);
                    for (String pl : originalPls) {
                        sb.append(" ").append(pl);
                    }
                    mockTranslated = sb.toString();
                }

                updateTranslationText(item, mockTranslated);
                item.setAiGenerated(true);
                item.setEditedManually(false);
                item.setStatus(TranslationItem.STATUS_SUCCESS);
                updateStatusUI(item);
                Toast.makeText(context, "تمت ترجمة العبارة بالذكاء الاصطناعي", Toast.LENGTH_SHORT).show();
            });

            // Copy Original text
            btnCopyOriginal.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("original", item.getOriginalText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "تم نسخ النص الأصلي", Toast.LENGTH_SHORT).show();
                }
            });

            // Copy Translation text
            btnCopyTranslation.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("translation", item.getTranslatedText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "تم نسخ النص المترجم", Toast.LENGTH_SHORT).show();
                }
            });

            // Paste translation from clipboard
            btnPaste.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null && clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription() != null) {
                    ClipData.Item clipItem = clipboard.getPrimaryClip().getItemAt(0);
                    CharSequence pasteData = clipItem.getText();
                    if (pasteData != null) {
                        updateTranslationText(item, pasteData.toString());
                        Toast.makeText(context, "تم اللصق بنجاح", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "الحافظة فارغة!", Toast.LENGTH_SHORT).show();
                }
            });

            // Clear text
            btnClear.setOnClickListener(v -> {
                updateTranslationText(item, "");
                Toast.makeText(context, "تم مسح الترجمة", Toast.LENGTH_SHORT).show();
            });

            // Reset text to initial value when the screen opened
            btnReset.setOnClickListener(v -> {
                String initial = initialTranslations.get(item.getKey());
                if (initial == null) initial = "";
                updateTranslationText(item, initial);
                item.setEditedManually(false);
                item.setAiGenerated(false);
                item.setStatus(initial.isEmpty() ? TranslationItem.STATUS_PENDING : TranslationItem.STATUS_SUCCESS);
                updateStatusUI(item);
                Toast.makeText(context, "تمت إعادة التعيين للقيمة الأصلية", Toast.LENGTH_SHORT).show();
            });

            // Bookmark Toggle
            btnBookmark.setOnClickListener(v -> {
                item.setBookmarked(!item.isBookmarked());
                updateBookmarkUI(item);
                Toast.makeText(context, item.isBookmarked() ? "تم الحفظ في المفضلة" : "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
            });

            // Undo Local Action
            btnUndo.setOnClickListener(v -> {
                Stack<String> uStack = undoStacks.get(item.getKey());
                Stack<String> rStack = redoStacks.get(item.getKey());
                if (uStack != null && uStack.size() > 1) {
                    String current = uStack.pop();
                    rStack.push(current);
                    String prev = uStack.peek();

                    // Temporarily detach watcher to set text
                    etTranslation.removeTextChangedListener(translationTextWatcher);
                    etTranslation.setText(highlightPlaceholders(prev));
                    item.setTranslatedText(prev);
                    tvTranslationCharCount.setText("الحروف: " + prev.length());
                    validatePlaceholders(item);
                    updateStatusUI(item);
                    etTranslation.addTextChangedListener(translationTextWatcher);

                    if (selectionListener != null) {
                        selectionListener.onStatsUpdated();
                    }
                    Toast.makeText(context, "تراجع", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "لا يوجد عمليات تراجع إضافية", Toast.LENGTH_SHORT).show();
                }
            });

            // Redo Local Action
            btnRedo.setOnClickListener(v -> {
                Stack<String> uStack = undoStacks.get(item.getKey());
                Stack<String> rStack = redoStacks.get(item.getKey());
                if (rStack != null && !rStack.isEmpty()) {
                    String nextText = rStack.pop();
                    uStack.push(nextText);

                    // Temporarily detach watcher to set text
                    etTranslation.removeTextChangedListener(translationTextWatcher);
                    etTranslation.setText(highlightPlaceholders(nextText));
                    item.setTranslatedText(nextText);
                    tvTranslationCharCount.setText("الحروف: " + nextText.length());
                    validatePlaceholders(item);
                    updateStatusUI(item);
                    etTranslation.addTextChangedListener(translationTextWatcher);

                    if (selectionListener != null) {
                        selectionListener.onStatsUpdated();
                    }
                    Toast.makeText(context, "إعادة التراجع", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "لا توجد عمليات لإعادتها", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void saveToUndoHistory(String key, String text) {
            Stack<String> uStack = undoStacks.get(key);
            if (uStack == null) {
                uStack = new Stack<>();
                undoStacks.put(key, uStack);
            }
            // Avoid duplicate contiguous commits
            if (uStack.isEmpty() || !uStack.peek().equals(text)) {
                uStack.push(text);
            }
            // Reset redo stack when a new change occurs
            Stack<String> rStack = redoStacks.get(key);
            if (rStack != null) {
                rStack.clear();
            }
        }

        private void updateTranslationText(TranslationItem item, String text) {
            saveToUndoHistory(item.getKey(), item.getTranslatedText());
            item.setTranslatedText(text);

            etTranslation.removeTextChangedListener(translationTextWatcher);
            etTranslation.setText(highlightPlaceholders(text));
            tvTranslationCharCount.setText("الحروف: " + text.length());
            validatePlaceholders(item);
            updateStatusUI(item);
            etTranslation.addTextChangedListener(translationTextWatcher);

            if (selectionListener != null) {
                selectionListener.onStatsUpdated();
            }
        }

        private void validatePlaceholders(TranslationItem item) {
            List<String> originalPls = extractPlaceholders(item.getOriginalText());
            List<String> transPls = extractPlaceholders(item.getTranslatedText());

            // Check discrepancy
            boolean discrepancy = false;
            if (!originalPls.isEmpty()) {
                if (transPls.isEmpty()) {
                    discrepancy = true;
                } else {
                    // Check if all original placeholders are in translation (order-agnostic or count)
                    for (String pl : originalPls) {
                        if (!item.getTranslatedText().contains(pl)) {
                            discrepancy = true;
                            break;
                        }
                    }
                }
            }

            if (discrepancy && !item.getTranslatedText().trim().isEmpty()) {
                layoutWarnings.setVisibility(View.VISIBLE);
                tvWarningMessage.setText("تنبيه: علامات التنسيق (Placeholders) في الترجمة لا تتطابق مع النص الأصلي!");
            } else {
                layoutWarnings.setVisibility(View.GONE);
            }
        }

        private void updateBookmarkUI(TranslationItem item) {
            if (item.isBookmarked()) {
                btnBookmark.setColorFilter(context.getResources().getColor(R.color.warning));
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                btnBookmark.setColorFilter(context.getResources().getColor(R.color.text_secondary));
                btnBookmark.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }

        private void updateStatusUI(TranslationItem item) {
            String statusText;
            int bgTint;
            int textColor;

            String translated = item.getTranslatedText().trim();

            if (translated.isEmpty()) {
                statusText = "غير مترجم";
                bgTint = Color.parseColor("#33FF5252"); // Translucent red
                textColor = Color.parseColor("#FF5252");
                item.setStatus(TranslationItem.STATUS_PENDING);
            } else if (item.isEditedManually()) {
                statusText = "يدوي";
                bgTint = Color.parseColor("#33FFC107"); // Translucent yellow
                textColor = Color.parseColor("#FFC107");
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            } else if (item.isAiGenerated()) {
                statusText = "ذكاء اصطناعي";
                bgTint = Color.parseColor("#3300E5FF"); // Translucent cyan/blue
                textColor = Color.parseColor("#00E5FF");
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            } else {
                statusText = "مترجم";
                bgTint = Color.parseColor("#334CAF50"); // Translucent green
                textColor = Color.parseColor("#4CAF50");
                item.setStatus(TranslationItem.STATUS_SUCCESS);
            }

            tvStatusBadge.setText(statusText);
            tvStatusBadge.setTextColor(textColor);
            
            // Set dynamic background
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(bgTint);
            gd.setCornerRadius(10f);
            tvStatusBadge.setBackground(gd);
        }
    }
}
