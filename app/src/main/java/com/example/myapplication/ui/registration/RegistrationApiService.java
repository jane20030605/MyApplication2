package com.example.myapplication.ui.registration;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationApiService {
    @POST("http://100.96.1.3/api_register.php") // 註冊 API 的端點
    Call<Void> registerUser(@Body JSONObject request);
}