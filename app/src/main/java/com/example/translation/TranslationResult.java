package com.example.translation;

public class TranslationResult {
    private final String originalText;
    private final String translatedText;
    private final String providerName;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final boolean isSuccessful;
    private final String errorMessage;

    public TranslationResult(String originalText, String translatedText, String providerName,
                             String sourceLanguage, String targetLanguage, boolean isSuccessful, String errorMessage) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.providerName = providerName;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.isSuccessful = isSuccessful;
        this.errorMessage = errorMessage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
