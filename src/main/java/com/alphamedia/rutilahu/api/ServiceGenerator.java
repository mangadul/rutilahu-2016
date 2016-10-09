package com.alphamedia.rutilahu.api;

import okhttp3.OkHttpClient;
import com.jakewharton.retrofit.Ok3Client;

import retrofit.RestAdapter;


public class ServiceGenerator {

    private ServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        OkHttpClient client = new OkHttpClient();
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new Ok3Client(client));

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }
}

/*
* https://github.com/JakeWharton/retrofit1-okhttp3-client
* */