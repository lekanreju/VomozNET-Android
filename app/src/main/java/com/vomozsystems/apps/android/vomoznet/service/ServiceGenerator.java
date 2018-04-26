package com.vomozsystems.apps.android.vomoznet.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leksrej on 7/30/16.
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = ApiClient.BASE_URL;
    public static final String API_BASE_FILE_URL = "https://vz.vomoz.net/vz/vzMediaUploadViaApp/";
    public static final String PROFILE_PICS_BASE_URL = "https://vz.vomoz.net/xTextersImageFilesLoc";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit.Builder fileBuilder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_FILE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createFileUploadService(Class<S> serviceClass) {
        Retrofit retrofit = fileBuilder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
