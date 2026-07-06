package com.example.providers;

import com.example.network.DeeplTranslationApi;
import com.example.network.HttpClientProvider;
import com.example.translation.TranslationResult;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class DeepLProvider implements TranslationProvider {
    private final String apiKey;
    private final DeeplTranslationApi api;

    public DeepLProvider(String apiKey, int timeoutSeconds) {
        this.apiKey = apiKey;
        
        String baseUrl = "https://api.deepl.com/v2/";
        if (apiKey != null && apiKey.endsWith(":fx")) {
            baseUrl = "https://api-free.deepl.com/v2/";
        }

        OkHttpClient client = HttpClientProvider.getClient(timeoutSeconds);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        this.api = retrofit.create(DeeplTranslationApi.class);
    }

    @Override
    public String getName() {
        return "DeepL";
    }

    @Override
    public TranslationResult translate(String text, String sourceLang, String targetLang) throws Exception {
        List<String> q = new ArrayList<>();
        q.add(text);
        List<TranslationResult> results = translateBatch(q, sourceLang, targetLang);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        throw new Exception("عملية الترجمة لم ترجع أي نتيجة.");
    }

    @Override
    public List<TranslationResult> translateBatch(List<String> texts, String sourceLang, String targetLang) throws Exception {
        String authHeader = "DeepL-Auth-Key " + apiKey;
        DeeplTranslationApi.RequestBody body = new DeeplTranslationApi.RequestBody(texts, targetLang, sourceLang);
        Response<DeeplTranslationApi.ResponseEnvelope> response = api.translate(authHeader, body).execute();

        List<TranslationResult> results = new ArrayList<>();
        if (response.isSuccessful() && response.body() != null) {
            List<DeeplTranslationApi.TranslationResult> items = response.body().translations;
            for (int i = 0; i < texts.size(); i++) {
                String original = texts.get(i);
                if (i < items.size()) {
                    DeeplTranslationApi.TranslationResult item = items.get(i);
                    results.add(new TranslationResult(
                            original,
                            item.text,
                            getName(),
                            item.detected_source_language != null ? item.detected_source_language : sourceLang,
                            targetLang,
                            true,
                            null
                    ));
                } else {
                    results.add(new TranslationResult(original, "", getName(), sourceLang, targetLang, false, "مخرجات ناقصة"));
                }
            }
            return results;
        } else {
            String errorMsg = "خطأ استجابة ديب إل (" + response.code() + ")";
            if (response.errorBody() != null) {
                try {
                    errorMsg += ": " + response.errorBody().string();
                } catch (Exception ignored) {}
            }
            throw new Exception(errorMsg);
        }
    }
}
