package com.example.myapplication.ui.Login;

import com.example.myapplication.RetrofitClient;
import com.example.myapplication.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginApiClient {

    private static final String BASE_URL = "http://100.96.1.3/";

    private Retrofit retrofit;
    private LoginApiService apiService;
    private com.example.myapplication.utils.PasswordHasher PasswordHasher;

    public LoginApiClient() {
        // 创建 Retrofit 实例
        retrofit = RetrofitClient.getClient();
        // 使用 Retrofit 实例创建 API 服务
        apiService = retrofit.create(LoginApiService.class);
    }

    // 发送网络请求以获取用户数据
    public void getUser(String username, String password, final LoginApiCallback<User> callback) {
        // 使用 PasswordHasher 类对密码进行哈希处理
        String hashedPassword = PasswordHasher.hashPassword(password);
        if (hashedPassword == null) {
            callback.onFailure("密碼雜湊失敗");
            return;
        }

        // 调用 API 服务的 getUser 方法，发送 GET 请求并获取用户数据
        Call<User> call = apiService.getUser(username, hashedPassword);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // 如果请求成功，则获取用户数据并调用回调函数的 onSuccess 方法
                    User user = response.body();
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("用戶資訊為空");
                    }
                } else {
                    // 如果请求失败，则调用回调函数的 onFailure 方法，并提供错误信息
                    callback.onFailure("無法讀取用戶資訊");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 如果请求失败，则调用回调函数的 onFailure 方法，并提供错误信息
                callback.onFailure(t.getMessage());
            }
        });
    }
}
