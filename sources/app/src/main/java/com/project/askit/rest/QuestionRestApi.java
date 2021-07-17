package com.project.askit.rest;

import com.project.askit.entity.Question;
import com.project.askit.entity.QuestionStatistics;
import com.project.askit.entity.Wrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuestionRestApi {

//    @GET("questions")
//    Call<Wrapper<Question>> findAllByFields(
//            @Query("size") Integer size,
//            @Query("page") Integer page,
//            @Query("sort") String sort,
//            @Query("order") String order,
//            @Query("subject") String subject,
//            @Query("htmlText") String htmlText,
////            @Query("createdDate") Timestamp createdDate,
//            @Query("categoryId") Integer categoryId,
//            @Query("userId") Integer userId,
//            @Query("approved") Integer approved);

    @GET("questions/{id}")
    Call<Question> findById(@Path(value = "id") Integer id);

    @GET("questions/query")
    Call<Wrapper<Question>> findByQuery(
            @Query(value = "page") Integer page,
            @Query(value = "size") Integer size,
            @Query(value = "sortBy") String sortBy,
            @Query(value = "order") String order,
            @Query(value = "categoryTitle") String categoryTitle,
            @Query(value = "createdDate") String createdDate,
            @Query(value = "search") String search,
            @Query(value = "approved") Integer approved);

    @GET("questions/{id}/statistics")
    Call<QuestionStatistics> getStatistics(@Path(value = "id") Integer id);

}