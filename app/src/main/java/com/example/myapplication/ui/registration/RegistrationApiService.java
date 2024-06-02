package com.example.myapplication.ui.registration;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationApiService {
    @POST("api_register.php") // 註冊 API 的端點
    Call<Void> registerUser(@Body RegistrationRequest request);
}