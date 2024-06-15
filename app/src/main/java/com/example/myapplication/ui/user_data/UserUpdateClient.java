package com.example.myapplication.ui.user_data;

import android.util.Log;

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

public class UserUpdateClient {

    private static final String TAG = UserUpdateClient.class.getSimpleName();
    private static final String BASE_URL = "https://100.96.1.3/api_update_userdata.php"; // 替換成你的後端API的基本URL

    private OkHttpClient client;

    public UserUpdateClient() {
        client = new OkHttpClient();
    }

    // 更新使用者資料到後端API
    public void updateUser(String username, String email, String phone, String address, String birthday, String account,
                           final UserUpdateCallback callback) {
        // 創建 JSON 對象
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
            jsonObject.put("birthday", birthday);
            jsonObject.put("account", account); // 假設你需要使用者的帳戶資訊來識別身份
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 設置 POST 請求主體，這裡假設後端 API 接受的是 JSON 格式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // 創建 POST 請求
        Request request = new Request.Builder()
                .url(BASE_URL + "/updateUser") // 替換成你的後端API的實際端點
                .post(body)
                .build();

        // 執行異步請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(TAG, "更新使用者資料失敗：" + e.getMessage());
                callback.onError("更新使用者資料失敗：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
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

    // 定義回調介面，用於通知更新結果
    public interface UserUpdateCallback {
        void onSuccess(String message);
        void onError(String message);
    }
}
