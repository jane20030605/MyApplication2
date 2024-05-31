package com.example.myapplication.ui.forget_password;

import com.example.myapplication.Request.ResetPasswordRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface forget_passwordApiService {
    //忘記密碼端點
    @POST("reset_password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

}