package com.project.askit.rest;

import com.project.askit.entity.Category;
import com.project.askit.entity.Wrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryRestApi {

    @GET("categories")
    Call<Wrapper<Category>> findAllByFields(
            @Query(value = "page") Integer page,
            @Query(value = "size") Integer size,
            @Query(value = "sortBy") String sortBy,
            @Query(value = "order") String order,
            @Query(value = "title") String title);

    @GET("categories/{id}")
    Call<Category> findById(@Path(value = "id") Integer id);
}