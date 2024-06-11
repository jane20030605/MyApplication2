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

public class CalendarAddClient {
    // 定義 JSON 資料的 MediaType
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //private static final MediaType mediaType = MediaType.parse("text/plain");
    // API 端點
    private static final String ADD_EVENT_URL = "http://100.96.1.3/api_add_calendar.php";

    // 建立 OkHttpClient 實例
    private final OkHttpClient client;

    public CalendarAddClient() {
        client = new OkHttpClient();
    }

    /**
     * 使用 HTTP POST 請求向日曆 API 添加日曆事件。
     * @param eventDataJson 表示事件數據的 JSON 字符串。
     * @param callback 添加事件結果的回調介面
     */
    public void addEvent(String eventDataJson, final CalendarCallback callback) {
        Log.d("CalendarAddClient", "EventData JSON: " + eventDataJson); // 記錄事件資料

        // 建立請求本文
        RequestBody requestBody = RequestBody.create(eventDataJson, JSON);


        // 建立 POST 請求
/*
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("date_up","2024-06-10 23:59:00")
                .addFormDataPart("date_end","2024-06-10 23:59:00")
                .addFormDataPart("people","android_test")
                .addFormDataPart("thing","測試")
                .addFormDataPart("describe","Android Test TTTTTTEEEESY")
                .addFormDataPart("account","qwe")
                .build();
*/
        Request request = new Request.Builder()
                .url(ADD_EVENT_URL)
                .post(requestBody)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("CalendarAddClient", "Response Code: " + response.code());
                String responseBody = response.body().string();
                Log.d("CalendarAddClient", "Response Body: " + responseBody);

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

    /**
     * 行事曆回調介面
     */
    public interface CalendarCallback {
        void onSuccess(String message); // 新增事件成功的回調方法
        void onError(String message);   // 新增事件失敗的回調方法
    }
}