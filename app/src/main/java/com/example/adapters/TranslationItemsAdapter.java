package com.example.adapters;

import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.translation.TranslationItem;
import com.example.ui.cards.TranslationItemCardView;
import java.util.List;

public class TranslationItemsAdapter extends RecyclerView.Adapter<TranslationItemsAdapter.ViewHolder> {

    private final List<TranslationItem> items;

    public TranslationItemsAdapter(List<TranslationItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TranslationItemCardView cardView = new TranslationItemCardView(parent.getContext());
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TranslationItem item = items.get(position);
        holder.cardView.setOriginalText(item.getOriginalText());
        
        String translated = item.getTranslatedText();
        if (translated == null || translated.trim().isEmpty()) {
            holder.cardView.setTranslatedText("بانتظار الترجمة الآلية...");
        } else {
            holder.cardView.setTranslatedText(translated);
        }

        boolean isTranslating = item.getStatus() == TranslationItem.STATUS_TRANSLATING;
        boolean isSuccess = item.getStatus() == TranslationItem.STATUS_SUCCESS;
        boolean isError = item.getStatus() == TranslationItem.STATUS_ERROR;
        boolean isSkipped = item.getStatus() == TranslationItem.STATUS_SKIPPED;

        holder.cardView.setDone(isSuccess);
        holder.cardView.setEdited(isTranslating);
        holder.cardView.setBookmarked(item.isBookmarked());

        holder.cardView.setOnCommentClickListener(v -> {
            if (isError) {
                com.example.dialogs.CustomDialogs.showErrorDialog(v.getContext(), 
                    "تفاصيل خطأ الترجمة", 
                    item.getErrorMessage(), 
                    null
                );
            } else if (isSuccess) {
                com.example.dialogs.CustomDialogs.showSuccessDialog(v.getContext(), 
                    "معلومات الترجمة", 
                    "المزوّد: " + item.getProviderName() + "\n" +
                    "اللغة المصدر: " + item.getSourceLang() + "\n" +
                    "اللغة الهدف: " + item.getTargetLang() + "\n\n" +
                    "الترجمة: " + item.getTranslatedText(), 
                    null
                );
            } else if (isSkipped) {
                Toast.makeText(v.getContext(), "تم تخطي هذا العنصر تلقائياً.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "العبارة لم تترجم بعد، اضغط على 'بدء الترجمة' لتشغيل المحرك.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TranslationItemCardView cardView;

        public ViewHolder(@NonNull TranslationItemCardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
