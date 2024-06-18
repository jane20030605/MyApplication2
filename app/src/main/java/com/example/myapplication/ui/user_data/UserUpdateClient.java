package com.example.myapplication.ui.user_data;

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

public class UserUpdateClient {
    private static final String TAG = "UserDataUpdateClient";
    private static final String UPDATE_USER_DATA_URL = "http://100.96.1.3/api_update_userdata.php"; // 根據實際情況修改URL
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;

    public UserUpdateClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 更新個人檔案資料的方法
    public void updateUserData(String json, final UserDataUpdateCallback callback) {

        // RequestBody body = RequestBody.create(JSON, jsonstr);
        RequestBody body = RequestBody.create(JSON , json);
        Request request = new Request.Builder()
                .url(UPDATE_USER_DATA_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "更新用戶數據失敗: " + e.getMessage());
                callback.onError("更新用戶數據失敗: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "用戶數據更新成功: " + responseBody); // 调试日志，确保服务器返回的数据正确
                        callback.onSuccess("用戶數據更新成功");
                    } else {
                        Log.e(TAG, "更新用戶數據失敗: " + response.message());
                        callback.onError("更新用戶數據失敗: " + response.message());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "更新用戶數據失敗: " + e.getMessage());
                    callback.onError("更新用戶數據失敗: " + e.getMessage());
                }
            }
        });
    }

    public interface UserDataUpdateCallback {
        void onSuccess(String message);

        void onError(String message);
    }
}
