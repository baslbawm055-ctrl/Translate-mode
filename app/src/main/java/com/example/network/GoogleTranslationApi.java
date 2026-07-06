package com.example.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GoogleTranslationApi {

    @POST("language/translate/v2")
    Call<ResponseEnvelope> translate(
            @Query("key") String apiKey,
            @Body RequestBody request
    );

    class RequestBody {
        public List<String> q;
        public String target;
        public String source;
        public String format = "text";

        public RequestBody(List<String> q, String target, String source) {
            this.q = q;
            this.target = target;
            this.source = "auto".equalsIgnoreCase(source) ? null : source;
        }
    }

    class ResponseEnvelope {
        public ResponseData data;
    }

    class ResponseData {
        public List<TranslationResult> translations;
    }

    class TranslationResult {
        public String translatedText;
        public String detectedSourceLanguage;
    }
}
