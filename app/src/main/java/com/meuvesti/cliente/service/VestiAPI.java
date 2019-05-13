package com.meuvesti.cliente.service;

/**
 * Created by hersonrodrigues on 21/01/17.
 */

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.meuvesti.cliente.realm.UsuarioRealm;
import com.meuvesti.cliente.realm.RealmService;
import com.meuvesti.cliente.utils.Globals;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class VestiAPI {

    private final int REST_TIMEOUT = 40;

    private static VestiAPI INSTANCE;
    private static String token;
    private Retrofit mRetrofit;

    public static VestiAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new VestiAPI();
        }
        return INSTANCE;
    }

    private Retrofit getRetrofit(Context context, boolean forceNewInstance) {
        //if (mRetrofit == null || token == null || forceNewInstance == true) {
        mRetrofit = getRetrofitBuilder(context);
        //}
        return mRetrofit;
    }

    private Retrofit getRetrofitBuilder(Context context) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);
        builder.readTimeout(REST_TIMEOUT, TimeUnit.SECONDS);
        builder.connectTimeout(REST_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);

        UsuarioRealm usuario = RealmService.getUsuarioLogado(context);
        if (usuario != null) {
            token = usuario.getToken();
            if (!token.isEmpty()) {
                Log.i("API", "Usando token: " + token);
                builder.interceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-type", "application/json")
                                .addHeader("Accept-Encoding", "application/json")
                                .addHeader("Authorization", "Bearer " + token)
                                .build();
                        return chain.proceed(request);
                    }
                });
            } else {
                builder.interceptors().add(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Content-type", "application/json")
                                .addHeader("Accept-Encoding", "application/json")
                                .build();
                        return chain.proceed(request);
                    }
                });
            }

        } else {

            builder.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-type", "application/json")
                            .addHeader("Accept-Encoding", "application/json")
                            .build();
                    return chain.proceed(request);
                }
            });

        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        return new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(Globals.URL_APP)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


    }

    public VestiService api(Context context) {
        return getRetrofit(context, false).create(VestiService.class);
    }

    public VestiService api(Context context, boolean b) {
        return getRetrofit(context, b).create(VestiService.class);
    }
}
