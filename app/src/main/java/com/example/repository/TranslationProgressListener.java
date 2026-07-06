package com.example.repository;

import com.example.translation.TranslationItem;
import java.util.List;

public interface TranslationProgressListener {
    void onStart(int totalItems);
    
    void onProgress(
            String currentString,
            int translatedCount,
            int remainingCount,
            int progressPercentage,
            long elapsedTimeMs,
            long estimatedRemainingTimeMs
    );
    
    void onItemTranslated(TranslationItem item);
    
    void onComplete(List<TranslationItem> results);
    
    void onError(String errorMessage);
}
