package com.project.askit.rest;

import com.project.askit.entity.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserRestApi {
    @GET("users/{id}")
    Call<User> findById(@Path(value = "id") Integer id);
}