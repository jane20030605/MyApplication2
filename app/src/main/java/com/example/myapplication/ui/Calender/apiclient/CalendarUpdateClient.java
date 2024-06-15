package com.example.myapplication.ui.Calender.apiclient;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CalendarUpdateClient {
    private static final String TAG = "CalendarUpdateClient";
    private static final String UPDATE_CALENDAR_URL = "http://100.96.1.3/api_update_calendar.php";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;

    public CalendarUpdateClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 更新事件的方法
    public void updateEvent(String json, final CalendarCallback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(UPDATE_CALENDAR_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "更新事件失敗: " + e.getMessage());
                callback.onError("更新事件失敗: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d(TAG, "事件更新成功: " + responseBody);
                    callback.onSuccess("事件更新成功");
                } else {
                    Log.e(TAG, "更新事件失敗: " + response.message());
                    callback.onError("更新事件失敗: " + response.message());
                }
            }
        });
    }

    // 定義回調介面
    public interface CalendarCallback {
        void onSuccess(String message);

        void onError(String message);
    }
}
