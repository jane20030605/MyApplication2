package com.example.myapplication.ui.registration;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationApiClient {
    private static final String BASE_URL = "http://100.96.1.3/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;

    public RegistrationApiClient() {
        client = new OkHttpClient();
    }

    public void registerUser(JSONObject requestData, final RegistrationCallback callback) {
        // 將 JSON 資料轉換為字串
        String jsonBody = requestData.toString();

        // 建立請求本文
        RequestBody body = RequestBody.create(jsonBody, JSON);

        // 建立 POST 請求
        Request request = new Request.Builder()
                .url(BASE_URL + "api_register.php") // 註冊 API 的端點
                .post(body)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d("RegistrationApiClient", "Response: " + responseBody);
                    callback.onSuccess("註冊成功");
                } else {
                    callback.onError("註冊失敗，請稍後再試");
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onError("註冊失敗，請稍後再試");
                Log.e("RegistrationApiClient", "Request failed: " + e.getMessage());

            }
        });
    }

    public interface RegistrationCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}
