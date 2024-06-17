package com.example.myapplication.ui.user_data;

import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
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

    // 定義日誌標籤和使用者資料更新URL
    private static final String TAG = "UserUpdateClient";
    private static final String USER_DATA_UPDATE_URL = "https://100.96.1.3/api_update_userdata.php";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;

    // 建構子初始化OkHttpClient，設置超時時間
    public UserUpdateClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // 更新使用者資料的方法
    public void updateUser(
            String username, String email, String phone,
            String address, String birthday, String account,
            final UserUpdateCallback callback) {
        // 創建JSON物件，並將使用者資料放入
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", username);
            jsonObject.put("mail", email);
            jsonObject.put("tel", phone);
            jsonObject.put("address", address);
            jsonObject.put("birthday", birthday);
            jsonObject.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 創建RequestBody並設置請求內容為JSON
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        // 創建請求
        Request request = new Request.Builder()
                .url(USER_DATA_UPDATE_URL)
                .post(body)
                .build();

        // 發送請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.e(TAG, "更新使用者資料失敗：" + e.getMessage());
                callback.onError("更新使用者資料失敗：" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d(TAG, "更新使用者資料成功：" + responseBody);
                    callback.onSuccess("使用者資料已成功更新");
                } else {
                    Log.e(TAG, "更新使用者資料失敗：" + response.message());
                    callback.onError("更新使用者資料失敗：" + response.message());
                }
                response.close();
            }
        });
    }

    public void updateUser(UserUpdateEvent userUpdateEvent, UserUpdateCallback userUpdateCallback) {
    }

    // 定義回調接口
    public interface UserUpdateCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}