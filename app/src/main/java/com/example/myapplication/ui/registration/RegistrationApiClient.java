package com.example.myapplication.ui.registration;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class RegistrationApiClient {
    // 建立 OkHttpClient 實例
    private final OkHttpClient client;
    // 定義 JSON 資料的 MediaType
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RegistrationApiClient() {
        client = new OkHttpClient();
    }

    /**
     * 註冊使用者的方法
     * @param requestData 註冊資料的 JSON 物件
     * @param callback 註冊結果的回調介面
     */
    public void registerUser(JSONObject requestData, final RegistrationCallback callback) {
        // 將 JSON 物件轉換為字串
        String jsonBody = requestData.toString();
        // 建立請求本文
        RequestBody requestBody = RequestBody.create(jsonBody, JSON);
        // 建立 POST 請求
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_register.php") // 註冊 API 的端點
                .post(requestBody)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("RegistrationApiClient", "Response: " + responseBody);
                    try {
                        // 驗證 JSON 格式
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        // 檢查是否包含必要的字段
                        if (jsonResponse.has("status") && jsonResponse.has("message")) {
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if ("success".equals(status)) {
                                callback.onSuccess(message);
                            } else {
                                callback.onError(message);
                            }
                        } else {
                            Log.e("RegistrationApiClient", "Invalid JSON response: missing 'status' or 'message'");
                            callback.onError("伺服器響應中缺少必要的字段");
                        }
                    } catch (JSONException e) {
                        Log.e("RegistrationApiClient", "JSON Parsing error: " + e.getMessage());
                        callback.onError("註冊失敗，無效的伺服器響應: " );
                    }
                } else {
                    String errorResponse = response.body() != null ? response.body().string() : "No response body";
                    Log.e("RegistrationApiClient", "HTTP error code: " + response.code() + ", response: " + errorResponse);
                    callback.onError("註冊失敗，請稍後再試。HTTP 錯誤碼: ");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("RegistrationApiClient", "Request failed: " + e.getMessage());
                callback.onError("註冊失敗，請稍後再試: " + e.getMessage());
            }
        });
    }

    /**
     * 註冊回調介面
     */
    public interface RegistrationCallback {
        void onSuccess(String message); // 註冊成功的回調方法
        void onError(String message);   // 註冊失敗的回調方法
    }
}
