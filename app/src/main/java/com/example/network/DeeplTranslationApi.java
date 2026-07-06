package com.example.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DeeplTranslationApi {

    @POST("translate")
    Call<ResponseEnvelope> translate(
            @Header("Authorization") String authHeader,
            @Body RequestBody request
    );

    class RequestBody {
        public List<String> text;
        public String target_lang;
        public String source_lang;

        public RequestBody(List<String> text, String targetLang, String sourceLang) {
            this.text = text;
            this.target_lang = targetLang != null ? targetLang.toUpperCase() : null;
            this.source_lang = (sourceLang != null && !"auto".equalsIgnoreCase(sourceLang)) ? sourceLang.toUpperCase() : null;
        }
    }

    class ResponseEnvelope {
        public List<TranslationResult> translations;
    }

    class TranslationResult {
        public String text;
        public String detected_source_language;
    }
}
