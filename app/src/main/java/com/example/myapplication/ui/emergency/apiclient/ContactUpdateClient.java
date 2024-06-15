// ContactUpdateClient.java

package com.example.myapplication.ui.emergency.apiclient;

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

public class ContactUpdateClient {
    private static final String TAG = "ContactUpdateClient";
    private static final String UPDATE_CONTACT_URL = "http://100.96.1.3/test.php";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;

    public ContactUpdateClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 更新聯繫人資料的方法
    public void updateContact(String json, final ContactUpdateCallback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(UPDATE_CONTACT_URL)
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
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "事件更新成功: " + responseBody); // 调试日志，确保服务器返回的数据正确
                        callback.onSuccess("事件更新成功");
                    } else {
                        Log.e(TAG, "更新事件失敗: " + response.message());
                        callback.onError("更新事件失敗: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "更新事件失敗: " + e.getMessage());
                    callback.onError("更新事件失敗: " + e.getMessage());
                }
            }
        });
    }

    public interface ContactUpdateCallback {
        void onSuccess(String message);

        void onError(String message);
    }
}
