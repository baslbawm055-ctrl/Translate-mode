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
    private String resourceType = "string"; // string, plural, array
    private String filePath = "res/values/strings.xml";
    private int lineNumber = 12;
    private String resourceId = "0x7f0f0001";
    private String comments = "تسمية توضيحية افتراضية";
    private String pluralQuantity = ""; // one, other, etc.
    private int arrayIndex = -1;
    private boolean isEditedManually = false;
    private boolean isAiGenerated = false;

    public TranslationItem(String key, String originalText) {
        this.key = key;
        this.originalText = originalText;
        this.translatedText = "";
        this.status = STATUS_PENDING;
        this.errorMessage = "";
        this.isBookmarked = false;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPluralQuantity() {
        return pluralQuantity;
    }

    public void setPluralQuantity(String pluralQuantity) {
        this.pluralQuantity = pluralQuantity;
    }

    public int getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public boolean isEditedManually() {
        return isEditedManually;
    }

    public void setEditedManually(boolean editedManually) {
        isEditedManually = editedManually;
    }

    public boolean isAiGenerated() {
        return isAiGenerated;
    }

    public void setAiGenerated(boolean aiGenerated) {
        isAiGenerated = aiGenerated;
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
