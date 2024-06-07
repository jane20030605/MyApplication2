package com.example.myapplication.ui.Login;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.models.User;
import com.example.myapplication.ui.Login.LoginApiCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginApiClient {

    private OkHttpClient client; // OkHttp 客戶端

    public LoginApiClient() {
        client = new OkHttpClient(); // 初始化 OkHttp 客戶端
    }

    // 登入方法
    public void login(String username, String password, final LoginApiCallback<User> callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("account", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            callback.onFailure("建立 JSON 請求失敗");
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url("http://100.96.1.3/login.php") // 登入 API 的 URL
                .post(requestBody)
                .build();

        // 使用 OkHttp 執行非同步請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // 解析 API 響應
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        Log.d("API_RESPONSE", responseBody);

                        // 將 JSON 字符串轉換為 JSONObject
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String message = jsonResponse.optString("message", "");

                        // 根據 API 返回的訊息判斷登入結果
                        if (message.equals("登入成功")) {
                            // 登入成功，將使用者資訊回調回去
                            User user = new User(username, password); // 這裡暫時只返回用戶名稱和密碼，你可以根據需求返回更多資訊
                            callback.onSuccess(user);
                        } else if (message.equals("帳號不存在") || message.equals("密碼錯誤")) {
                            // 帳號不存在或密碼錯誤
                            callback.onFailure("帳號不存在或密碼錯誤");
                        } else {
                            // 其他錯誤訊息
                            callback.onFailure("登入失敗：" + message);
                        }
                    } catch (JSONException e) {
                        // JSONException 發生時，認為登入失敗，並呼叫 onFailure 方法
                        callback.onFailure("登入失敗：解析 JSON 響應時出現錯誤");
                    }
                } else {
                    // 調用回調函式的 onFailure 方法，並提供錯誤訊息
                    callback.onFailure("登入失敗：伺服器返回錯誤狀態碼 " + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 調用回調函式的 onFailure 方法，並提供錯誤訊息
                callback.onFailure(e.getMessage());
            }
        });
    }
}