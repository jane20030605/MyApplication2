package com.example.myapplication.ui.emergency.apiclient;

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

public class ContactAddClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String ADD_CONTACT_URL = "http://100.96.1.3/api_add_contact.php";

    // 建立 OkHttpClient 實例
    private final OkHttpClient client;

    public ContactAddClient() {
        this.client = new OkHttpClient();
    }

    public void addContact(String eventDataJson, final ContactAddCallback callback) {
        //Log.d("ContactAddClient", "EventData JSON: " + eventDataJson); // 記錄事件資料

        // 建立請求本文
        RequestBody requestBody = RequestBody.create(eventDataJson, JSON);
        Request request = new Request.Builder()
                .url(ADD_CONTACT_URL)
                .post(requestBody)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Log.d("ContactAddClient", "Response Code: " + response.code());
                String responseBody = response.body().string();
                //Log.d("ContactAddClient", "Response Body: " + responseBody);

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

    // 定義 Callback 接口
    public interface ContactAddCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}
