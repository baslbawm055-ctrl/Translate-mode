package com.example.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.parser.ParserFactory;
import com.example.parser.TranslationFileParser;
import com.example.providers.ProviderFactory;
import com.example.providers.TranslationProvider;
import com.example.settings.TranslationSettings;
import com.example.translation.PlaceholderProtector;
import com.example.translation.TranslationItem;
import com.example.translation.TranslationResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslationRepository {

    private final Context context;
    private final TranslationSettings settings;
    private ExecutorService executorService;
    private final Handler mainHandler;
    private volatile boolean isCancelled = false;

    private List<TranslationItem> currentItems = new ArrayList<>();

    public TranslationRepository(Context context) {
        this.context = context.getApplicationContext();
        this.settings = new TranslationSettings(context);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public List<TranslationItem> getCurrentItems() {
        return currentItems;
    }

    public void setCurrentItems(List<TranslationItem> items) {
        this.currentItems = items != null ? items : new ArrayList<>();
    }

    /**
     * Loads and parses a file in the background, updating the loaded items list.
     */
    public void loadFile(InputStream inputStream, String fileName, LoadCallback callback) {
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        singleExecutor.execute(() -> {
            try {
                TranslationFileParser parser = ParserFactory.getParser(fileName);
                List<TranslationItem> parsed = parser.parse(inputStream);
                
                mainHandler.post(() -> {
                    currentItems = parsed;
                    callback.onSuccess(parsed);
                });
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage() != null ? e.getMessage() : "فشل قراءة وتحليل الملف."));
            } finally {
                singleExecutor.shutdown();
            }
        });
    }

    public interface LoadCallback {
        void onSuccess(List<TranslationItem> items);
        void onError(String error);
    }

    /**
     * Cancels any active translation process.
     */
    public void cancelTranslation() {
        isCancelled = true;
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    /**
     * Starts the translation process asynchronously for all loaded items.
     */
    public void startTranslation(TranslationProgressListener listener) {
        if (currentItems.isEmpty()) {
            listener.onError("لا توجد عبارات محملة لترجمتها.");
            return;
        }

        isCancelled = false;
        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                // Initialize translation provider
                TranslationProvider provider;
                try {
                    provider = ProviderFactory.getProvider(context);
                } catch (Exception e) {
                    postError(listener, e.getMessage());
                    return;
                }

                // Filter items based on translation mode
                List<TranslationItem> itemsToTranslate = filterItemsByMode(currentItems, settings.getTranslationMode());
                int totalToTranslate = itemsToTranslate.size();

                if (totalToTranslate == 0) {
                    mainHandler.post(() -> {
                        listener.onStart(0);
                        listener.onComplete(currentItems);
                    });
                    return;
                }

                postStart(listener, totalToTranslate);

                int batchSize = settings.getBatchSize();
                int retryCount = settings.getRetryCount();
                String sourceLang = settings.getSourceLanguage();
                String targetLang = settings.getTargetLanguage();

                long startTime = System.currentTimeMillis();
                int translatedCount = 0;

                for (int i = 0; i < totalToTranslate && !isCancelled; i += batchSize) {
                    int end = Math.min(i + batchSize, totalToTranslate);
                    List<TranslationItem> batchItems = itemsToTranslate.subList(i, end);

                    // Update status to TRANSLATING
                    for (TranslationItem item : batchItems) {
                        item.setStatus(TranslationItem.STATUS_TRANSLATING);
                        postItemTranslated(listener, item);
                    }

                    // Protect placeholders and collect masked strings
                    List<String> originalMaskedTexts = new ArrayList<>();
                    List<PlaceholderProtector.ProtectedText> protectedHelpers = new ArrayList<>();

                    for (TranslationItem item : batchItems) {
                        PlaceholderProtector.ProtectedText protectedText = PlaceholderProtector.protect(item.getOriginalText());
                        protectedHelpers.add(protectedText);
                        originalMaskedTexts.add(protectedText.getMaskedText());
                    }

                    // Execute batch translation with retry logic
                    List<TranslationResult> batchResults = null;
                    Exception lastException = null;

                    for (int attempt = 1; attempt <= retryCount + 1 && !isCancelled; attempt++) {
                        try {
                            batchResults = provider.translateBatch(originalMaskedTexts, sourceLang, targetLang);
                            break; // success!
                        } catch (Exception e) {
                            lastException = e;
                            if (attempt <= retryCount && !isCancelled) {
                                // Simple exponential wait before retry (e.g. 1s, 2s, 4s...)
                                try {
                                    Thread.sleep(attempt * 1000L);
                                } catch (InterruptedException ie) {
                                    break;
                                }
                            }
                        }
                    }

                    if (isCancelled) {
                        break;
                    }

                    // Process results
                    if (batchResults != null) {
                        for (int j = 0; j < batchItems.size(); j++) {
                            TranslationItem item = batchItems.get(j);
                            TranslationResult result = batchResults.get(j);
                            PlaceholderProtector.ProtectedText protectedHelper = protectedHelpers.get(j);

                            if (result.isSuccessful()) {
                                String restoredText = protectedHelper.restore(result.getTranslatedText());
                                item.setTranslatedText(restoredText);
                                item.setStatus(TranslationItem.STATUS_SUCCESS);
                                item.setSourceLang(result.getSourceLanguage());
                                item.setTargetLang(result.getTargetLanguage());
                                item.setProviderName(result.getProviderName());
                                item.setErrorMessage("");
                            } else {
                                item.setStatus(TranslationItem.STATUS_ERROR);
                                item.setErrorMessage(result.getErrorMessage());
                            }
                            postItemTranslated(listener, item);
                        }
                    } else {
                        // Entire batch failed
                        String finalError = lastException != null ? lastException.getMessage() : "خطأ غير معروف في الشبكة.";
                        for (TranslationItem item : batchItems) {
                            item.setStatus(TranslationItem.STATUS_ERROR);
                            item.setErrorMessage(finalError);
                            postItemTranslated(listener, item);
                        }
                    }

                    translatedCount += batchItems.size();
                    int remaining = totalToTranslate - translatedCount;
                    int percentage = (translatedCount * 100) / totalToTranslate;
                    long elapsed = System.currentTimeMillis() - startTime;
                    long msPerItem = elapsed / translatedCount;
                    long estimatedRemaining = msPerItem * remaining;

                    String currentString = batchItems.get(batchItems.size() - 1).getOriginalText();

                    postProgress(listener, currentString, translatedCount, remaining, percentage, elapsed, estimatedRemaining);
                }

                if (!isCancelled) {
                    mainHandler.post(() -> listener.onComplete(currentItems));
                }

            } catch (Exception e) {
                postError(listener, e.getMessage() != null ? e.getMessage() : "حدث خطأ غير متوقع أثناء الترجمة.");
            } finally {
                if (executorService != null) {
                    executorService.shutdown();
                }
            }
        });
    }

    private List<TranslationItem> filterItemsByMode(List<TranslationItem> allItems, int mode) {
        List<TranslationItem> filtered = new ArrayList<>();
        for (TranslationItem item : allItems) {
            switch (mode) {
                case TranslationSettings.MODE_TRANSLATE_ALL:
                    filtered.add(item);
                    break;
                case TranslationSettings.MODE_ONLY_EMPTY:
                    if (item.getTranslatedText() == null || item.getTranslatedText().trim().isEmpty()) {
                        filtered.add(item);
                    } else {
                        item.setStatus(TranslationItem.STATUS_SKIPPED);
                    }
                    break;
                case TranslationSettings.MODE_SKIP_TRANSLATED:
                    if (item.getStatus() != TranslationItem.STATUS_SUCCESS) {
                        filtered.add(item);
                    } else {
                        item.setStatus(TranslationItem.STATUS_SKIPPED);
                    }
                    break;
                case TranslationSettings.MODE_SKIP_IDENTICAL:
                    String trans = item.getTranslatedText();
                    if (trans == null || trans.trim().isEmpty() || item.getOriginalText().equals(trans)) {
                        filtered.add(item);
                    } else {
                        item.setStatus(TranslationItem.STATUS_SKIPPED);
                    }
                    break;
            }
        }
        return filtered;
    }

    // Main thread dispatch helpers
    private void postStart(TranslationProgressListener listener, int total) {
        mainHandler.post(() -> listener.onStart(total));
    }

    private void postProgress(TranslationProgressListener listener, String current, int trans, int rem, int pct, long elapsed, long est) {
        mainHandler.post(() -> listener.onProgress(current, trans, rem, pct, elapsed, est));
    }

    private void postItemTranslated(TranslationProgressListener listener, TranslationItem item) {
        mainHandler.post(() -> listener.onItemTranslated(item));
    }

    private void postError(TranslationProgressListener listener, String error) {
        mainHandler.post(() -> listener.onError(error));
    }
}
