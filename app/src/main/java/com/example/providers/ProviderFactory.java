package com.example.providers;

import android.content.Context;
import com.example.security.SecureKeysManager;
import com.example.settings.TranslationSettings;

public class ProviderFactory {

    public static TranslationProvider getProvider(Context context) throws Exception {
        TranslationSettings settings = new TranslationSettings(context);
        SecureKeysManager keysManager = new SecureKeysManager(context);

        String providerName = settings.getProvider();
        int timeout = settings.getTimeoutSeconds();

        if (SecureKeysManager.PROVIDER_GOOGLE.equalsIgnoreCase(providerName)) {
            String key = keysManager.getKey(SecureKeysManager.PROVIDER_GOOGLE);
            if (key.isEmpty()) {
                throw new Exception("مفتاح واجهة Google API مفقود. يرجى إدخاله في صفحة الإعدادات لتفعيل الترجمة.");
            }
            return new GoogleProvider(key, timeout);
        } else if (SecureKeysManager.PROVIDER_MICROSOFT.equalsIgnoreCase(providerName)) {
            String key = keysManager.getKey(SecureKeysManager.PROVIDER_MICROSOFT);
            if (key.isEmpty()) {
                throw new Exception("مفتاح واجهة Microsoft API مفقود. يرجى إدخاله في صفحة الإعدادات لتفعيل الترجمة.");
            }
            return new MicrosoftProvider(key, null, timeout);
        } else if (SecureKeysManager.PROVIDER_DEEPL.equalsIgnoreCase(providerName)) {
            String key = keysManager.getKey(SecureKeysManager.PROVIDER_DEEPL);
            if (key.isEmpty()) {
                throw new Exception("مفتاح واجهة DeepL API مفقود. يرجى إدخاله في صفحة الإعدادات لتفعيل الترجمة.");
            }
            return new DeepLProvider(key, timeout);
        }

        throw new Exception("مزوّد الترجمة المحدّد غير مدعوم أو غير معروف.");
    }
}
