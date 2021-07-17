package com.project.askit.rest;

import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApi {

    public static Object getInstance(Class<?> cls) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.144:9090/api/")
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(cls);
    }
}
