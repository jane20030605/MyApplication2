package com.example.myapplication.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("reset_password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @GET("data")
    Call<List<ExampleEntity>> getData();

    @POST("data")
    Call<ExampleEntity> postData(@Body ExampleEntity example);
}
