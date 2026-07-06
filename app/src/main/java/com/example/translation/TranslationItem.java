package com.example.translation;

import java.io.Serializable;

public class TranslationItem implements Serializable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_TRANSLATING = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_SKIPPED = 4;

    private String key;
    private String originalText;
    private String translatedText;
    private int status;
    private String errorMessage;
    private String providerName;
    private String sourceLang;
    private String targetLang;
    private boolean isBookmarked;

    public TranslationItem(String key, String originalText) {
        this.key = key;
        this.originalText = originalText;
        this.translatedText = "";
        this.status = STATUS_PENDING;
        this.errorMessage = "";
        this.isBookmarked = false;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.isBookmarked = bookmarked;
    }
}
