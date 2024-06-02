package com.example.myapplication.ui.Login;

import android.util.Log;

import com.example.myapplication.models.User;
import com.example.myapplication.utils.PasswordHasher;

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
    private PasswordHasher passwordHasher; // 密碼雜湊工具

    public LoginApiClient() {
        client = new OkHttpClient(); // 初始化 OkHttp 客戶端
        passwordHasher = new PasswordHasher(); // 初始化密碼雜湊工具
    }

    // 登入方法
    public void login(String username, String password, final LoginApiCallback<User> callback) {
        // 對密碼進行雜湊處理
        String hashedPassword = passwordHasher.hashPassword(password);
        if (hashedPassword == null) {
            callback.onFailure("密碼雜湊失敗");
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", hashedPassword); // 發送雜湊後的密碼
        } catch (JSONException e) {
            callback.onFailure("建立 JSON 請求失敗");
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_login.php") // 登入 API 的 URL
                .post(requestBody)
                .build();

        // 使用 OkHttp 執行非同步請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // 解析 API 響應
                        String responseBody = response.body().string();
                        Log.d("API_RESPONSE", responseBody);

                        // 將 JSON 字符串轉換為 JSONObject
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        // 使用 optString 方法獲取 "id/account/password" 鍵的值，如果不存在則返回默認值 ""
                        String userId = jsonResponse.optString("id", "");
                        String username = jsonResponse.optString("account", "");
                        String password = jsonResponse.optString("password", "");
                        // 檢查 userId 是否為空
                        if (!userId.isEmpty() && !password.isEmpty()) {
                            // 創建用戶對象
                            User user = new User(userId, username, password);
                            // 調用回調函式的 onSuccess 方法
                            callback.onSuccess(user);
                        } else {
                            // 如果 userId 為空，則認為登入失敗，並呼叫 onFailure 方法
                            callback.onFailure("登入失敗：找不到用戶ID或密碼");
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
