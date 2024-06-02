package com.example.myapplication.ui.Login;

import com.example.myapplication.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginApiService {
    // 定義獲取用戶數據的端點，接受用戶名稱和密碼作為請求參數
    @GET("http://100.96.1.3/api_login.php")
    Call<User> getUser(@Query("username") String username,
                       @Query("password") String password);
}

