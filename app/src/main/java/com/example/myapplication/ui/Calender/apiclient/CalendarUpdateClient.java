package com.example.myapplication.ui.Calender.apiclient;

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

public class CalendarUpdateClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String API_URL = "https://100.96.1.3/tset.php"; // 替换为你的 PHP 服务器的 URL

    private final OkHttpClient client;

    public CalendarUpdateClient() {
        client = new OkHttpClient();
    }

    public void updateEvent(String eventDataJson, CalendarCallback callback) {
        Log.d("CalendarUpdateClient", "EventData JSON: " + eventDataJson);

        RequestBody requestBody = RequestBody.create(eventDataJson, JSON);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("CalendarUpdateClient", "Response Code: " + response.code());
                String responseBody = response.body().string();
                Log.d("CalendarUpdateClient", "Response Body: " + responseBody);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.has("status") && jsonResponse.has("message")) {
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if ("success".equals(status)) {
                                callback.onSuccess(message);
                            } else {
                                callback.onError(message);
                            }
                        }
                    } catch (JSONException e) {
                        callback.onError("無效的伺服器響應: " + e.getMessage());
                    }
                } else {
                    callback.onError("HTTP 錯誤碼: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("新增事件失敗，請稍後再試: " + e.getMessage());
            }
        });
    }

    public interface CalendarCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}