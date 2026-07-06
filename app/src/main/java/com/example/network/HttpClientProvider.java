package com.example.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

public class HttpClientProvider {
    private static OkHttpClient baseClient;

    private static synchronized OkHttpClient getBaseClient() {
        if (baseClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            baseClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
        }
        return baseClient;
    }

    public static OkHttpClient getClient(int timeoutSeconds) {
        return getBaseClient().newBuilder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }
}
