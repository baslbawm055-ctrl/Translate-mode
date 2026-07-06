package com.example.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class TranslationSettings {
    private static final String PREFS_NAME = "translation_settings";

    // Keys
    private static final String KEY_PROVIDER = "pref_translation_provider";
    private static final String KEY_SOURCE_LANG = "pref_source_lang";
    private static final String KEY_TARGET_LANG = "pref_target_lang";
    private static final String KEY_AUTO_DETECT = "pref_auto_detect";
    private static final String KEY_BATCH_SIZE = "pref_batch_size";
    private static final String KEY_RETRY_COUNT = "pref_retry_count";
    private static final String KEY_TIMEOUT_SEC = "pref_timeout_sec";
    private static final String KEY_TRANSLATION_MODE = "pref_translation_mode";

    // Modes
    public static final int MODE_TRANSLATE_ALL = 0;
    public static final int MODE_ONLY_EMPTY = 1;
    public static final int MODE_SKIP_TRANSLATED = 2;
    public static final int MODE_SKIP_IDENTICAL = 3;

    private final SharedPreferences prefs;

    public TranslationSettings(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getProvider() {
        return prefs.getString(KEY_PROVIDER, "Google");
    }

    public void setProvider(String provider) {
        prefs.edit().putString(KEY_PROVIDER, provider).apply();
    }

    public String getSourceLanguage() {
        return prefs.getString(KEY_SOURCE_LANG, "auto");
    }

    public void setSourceLanguage(String langCode) {
        prefs.edit().putString(KEY_SOURCE_LANG, langCode).apply();
    }

    public String getTargetLanguage() {
        return prefs.getString(KEY_TARGET_LANG, "ar");
    }

    public void setTargetLanguage(String langCode) {
        prefs.edit().putString(KEY_TARGET_LANG, langCode).apply();
    }

    public boolean isAutoDetectLanguage() {
        return prefs.getBoolean(KEY_AUTO_DETECT, true);
    }

    public void setAutoDetectLanguage(boolean autoDetect) {
        prefs.edit().putBoolean(KEY_AUTO_DETECT, autoDetect).apply();
    }

    public int getBatchSize() {
        return prefs.getInt(KEY_BATCH_SIZE, 5);
    }

    public void setBatchSize(int size) {
        prefs.edit().putInt(KEY_BATCH_SIZE, size).apply();
    }

    public int getRetryCount() {
        return prefs.getInt(KEY_RETRY_COUNT, 3);
    }

    public void setRetryCount(int count) {
        prefs.edit().putInt(KEY_RETRY_COUNT, count).apply();
    }

    public int getTimeoutSeconds() {
        return prefs.getInt(KEY_TIMEOUT_SEC, 30);
    }

    public void setTimeoutSeconds(int seconds) {
        prefs.edit().putInt(KEY_TIMEOUT_SEC, seconds).apply();
    }

    public int getTranslationMode() {
        return prefs.getInt(KEY_TRANSLATION_MODE, MODE_TRANSLATE_ALL);
    }

    public void setTranslationMode(int mode) {
        prefs.edit().putInt(KEY_TRANSLATION_MODE, mode).apply();
    }
}
