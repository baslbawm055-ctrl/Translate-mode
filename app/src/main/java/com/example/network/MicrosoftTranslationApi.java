package com.example.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MicrosoftTranslationApi {

    @POST("translate?api-version=3.0")
    Call<List<TranslationResponse>> translate(
            @Header("Ocp-Apim-Subscription-Key") String apiKey,
            @Header("Ocp-Apim-Subscription-Region") String region,
            @Query("to") String targetLang,
            @Query("from") String sourceLang,
            @Body List<TextItem> body
    );

    class TextItem {
        public String Text;

        public TextItem(String text) {
            this.Text = text;
        }
    }

    class TranslationResponse {
        public List<TranslationItem> translations;
    }

    class TranslationItem {
        public String text;
        public String to;
    }
}
