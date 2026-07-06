package com.example.providers;

import com.example.network.GoogleTranslationApi;
import com.example.network.HttpClientProvider;
import com.example.translation.TranslationResult;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class GoogleProvider implements TranslationProvider {
    private final String apiKey;
    private final GoogleTranslationApi api;

    public GoogleProvider(String apiKey, int timeoutSeconds) {
        this.apiKey = apiKey;
        OkHttpClient client = HttpClientProvider.getClient(timeoutSeconds);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://translation.googleapis.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        this.api = retrofit.create(GoogleTranslationApi.class);
    }

    @Override
    public String getName() {
        return "Google";
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
        GoogleTranslationApi.RequestBody body = new GoogleTranslationApi.RequestBody(texts, targetLang, sourceLang);
        Response<GoogleTranslationApi.ResponseEnvelope> response = api.translate(apiKey, body).execute();
        
        List<TranslationResult> results = new ArrayList<>();
        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
            List<GoogleTranslationApi.TranslationResult> items = response.body().data.translations;
            for (int i = 0; i < texts.size(); i++) {
                String original = texts.get(i);
                if (i < items.size()) {
                    GoogleTranslationApi.TranslationResult item = items.get(i);
                    results.add(new TranslationResult(
                            original,
                            item.translatedText,
                            getName(),
                            item.detectedSourceLanguage != null ? item.detectedSourceLanguage : sourceLang,
                            targetLang,
                            true,
                            null
                    ));
                } else {
                    results.add(new TranslationResult(original, "", getName(), sourceLang, targetLang, false, "مخرجات مفقودة"));
                }
            }
            return results;
        } else {
            String errorMsg = "خطأ استجابة جوجل (" + response.code() + ")";
            if (response.errorBody() != null) {
                try {
                    errorMsg += ": " + response.errorBody().string();
                } catch (Exception ignored) {}
            }
            throw new Exception(errorMsg);
        }
    }
}
