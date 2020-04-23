package br.com.jogo.velha.services.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.jogo.velha.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API Client for request on server
 *
 */
public class APIClient {

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private Picasso picasso;

    public APIClient(@NonNull Context context, @NonNull String baseUrl) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient()
                .newBuilder()
                .addInterceptor(checkConnectionInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(checkConnectionInterceptor)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    private final Interceptor checkConnectionInterceptor = chain -> {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Content-Type", "application/json")
                .method(original.method(),original.body())
                .build();
        return chain.proceed(request);
    };

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
