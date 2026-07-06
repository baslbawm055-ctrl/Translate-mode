package com.example.providers;

import com.example.network.HttpClientProvider;
import com.example.network.MicrosoftTranslationApi;
import com.example.translation.TranslationResult;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MicrosoftProvider implements TranslationProvider {
    private final String apiKey;
    private final String region;
    private final MicrosoftTranslationApi api;

    public MicrosoftProvider(String apiKey, String region, int timeoutSeconds) {
        this.apiKey = apiKey;
        this.region = (region == null || region.trim().isEmpty()) ? null : region.trim();
        OkHttpClient client = HttpClientProvider.getClient(timeoutSeconds);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cognitive.microsofttranslator.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        this.api = retrofit.create(MicrosoftTranslationApi.class);
    }

    @Override
    public String getName() {
        return "Microsoft";
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
        List<MicrosoftTranslationApi.TextItem> body = new ArrayList<>();
        for (String text : texts) {
            body.add(new MicrosoftTranslationApi.TextItem(text));
        }

        String from = "auto".equalsIgnoreCase(sourceLang) ? null : sourceLang;
        Response<List<MicrosoftTranslationApi.TranslationResponse>> response = 
                api.translate(apiKey, region, targetLang, from, body).execute();

        List<TranslationResult> results = new ArrayList<>();
        if (response.isSuccessful() && response.body() != null) {
            List<MicrosoftTranslationApi.TranslationResponse> items = response.body();
            for (int i = 0; i < texts.size(); i++) {
                String original = texts.get(i);
                if (i < items.size()) {
                    MicrosoftTranslationApi.TranslationResponse item = items.get(i);
                    if (item.translations != null && !item.translations.isEmpty()) {
                        results.add(new TranslationResult(
                                original,
                                item.translations.get(0).text,
                                getName(),
                                sourceLang,
                                targetLang,
                                true,
                                null
                        ));
                    } else {
                        results.add(new TranslationResult(original, "", getName(), sourceLang, targetLang, false, "فشل قراءة ترجمة العنصر"));
                    }
                } else {
                    results.add(new TranslationResult(original, "", getName(), sourceLang, targetLang, false, "مخرجات ناقصة"));
                }
            }
            return results;
        } else {
            String errorMsg = "خطأ استجابة مايكروسوفت (" + response.code() + ")";
            if (response.errorBody() != null) {
                try {
                    errorMsg += ": " + response.errorBody().string();
                } catch (Exception ignored) {}
            }
            throw new Exception(errorMsg);
        }
    }
}
