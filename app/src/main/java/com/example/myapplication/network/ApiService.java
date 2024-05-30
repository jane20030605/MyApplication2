package com.example.myapplication.network;

import com.example.myapplication.Request.Calender_thingRequest;
import com.example.myapplication.Request.LoginRequest;
import com.example.myapplication.Request.RegisterRequest;
import com.example.myapplication.Request.ResetPasswordRequest;
import com.example.myapplication.Response.Calender_thingResponse;
import com.example.myapplication.Response.LoginResponse;
import com.example.myapplication.Response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // 定義 API 端點

    //忘記密碼端點
    @POST("reset_password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    // 登錄端點
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // 註冊端點
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    //行事曆事件端點
    @POST("calender_thing")
    Call<Calender_thingResponse> calender_thing(@Body Calender_thingRequest request);

}
