package com.example.myapplication.ui.Login;

import com.example.myapplication.RetrofitClient;
import com.example.myapplication.models.User;
import com.example.myapplication.ui.Calender.CalendarApiService;
import com.example.myapplication.ui.Calender.CalendarEvent;
import com.example.myapplication.utils.PasswordHasher;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginApiClient {

    private static final String BASE_URL = "http://100.96.1.3/";

    private Retrofit retrofit;
    private LoginApiService loginApiService;
    private CalendarApiService calendarApiService;
    private PasswordHasher passwordHasher;

    public LoginApiClient() {
        // 建立 Retrofit 實例
        retrofit = RetrofitClient.getClient();
        // 使用 Retrofit 實例建立 API 服務
        loginApiService = retrofit.create(LoginApiService.class);
        calendarApiService = retrofit.create(CalendarApiService.class);
        passwordHasher = new PasswordHasher();
    }

    // 發送網路請求以獲取使用者資料
    public void getUser(String username, String password, final LoginApiCallback<User> callback) {
        // 使用 PasswordHasher 類對密碼進行雜湊處理
        String hashedPassword = passwordHasher.hashPassword(password);
        if (hashedPassword == null) {
            callback.onFailure("密碼雜湊失敗");
            return;
        }

        // 調用 API 服務的 getUser 方法，發送 GET 請求並獲取使用者資料
        Call<User> call = loginApiService.getUser(username, hashedPassword);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // 如果請求成功，則獲取使用者資料並呼叫回調函式的 onSuccess 方法
                    User user = response.body();
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("用戶資訊為空");
                    }
                } else {
                    // 如果請求失敗，則呼叫回調函式的 onFailure 方法，並提供錯誤訊息
                    callback.onFailure("無法讀取用戶資訊");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 如果請求失敗，則呼叫回調函式的 onFailure 方法，並提供錯誤訊息
                callback.onFailure(t.getMessage());
            }
        });
    }

}