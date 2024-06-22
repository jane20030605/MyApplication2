package com.example.myapplication.ui.change_password;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;
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

public class password_changeFragment extends Fragment {

    private EditText etCurrentPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private String account; // 用於存儲用戶帳號
    private SharedPreferences sharedPreferences;

    public static password_changeFragment newInstance() {
        return new password_changeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_change, container, false);

        // 初始化View
        etCurrentPassword = rootView.findViewById(R.id.et_current_password);
        etNewPassword = rootView.findViewById(R.id.et_new_password);
        etConfirmPassword = rootView.findViewById(R.id.et_confirm_password);
        Button btnConfirmChange = rootView.findViewById(R.id.btn_confirm_change);
        Button btnSubmit = rootView.findViewById(R.id.btn_submit);

        // 設置按鈕點擊事件
        btnConfirmChange.setOnClickListener(v -> confirmChange());
        btnSubmit.setOnClickListener(v -> submit());

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        account = sharedPreferences.getString("ACCOUNT", "");

        return rootView;
    }

    private void confirmChange() {
        // 獲取輸入的密碼
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // 簡單驗證密碼是否一致
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "新密碼和確認密碼不一致", Toast.LENGTH_SHORT).show();
            Log.d("PasswordChangeFragment", "新密碼和確認密碼不一致");
            return;
        }

        // 構建彈跳視窗顯示的文字
        String confirmationMessage = "確認帳號為: " + account + "\n修改密碼為: " + newPassword;
        Log.d("PasswordChangeFragment", "確認修改: 確認帳號為: " + account + ", 修改密碼為: " + newPassword);

        // 彈出確認彈跳視窗
        new AlertDialog.Builder(requireContext())
                .setTitle("確認修改")
                .setMessage(confirmationMessage)
                .setPositiveButton("是", (dialog, which) -> {
                    // 點選了是，執行密碼變更的邏輯
                    Log.d("PasswordChangeFragment", "確認修改彈窗 - 確認點擊");

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("account", account); // 使用自動填充的用戶帳號
                        jsonObject.put("current_password", currentPassword);
                        jsonObject.put("new_password", newPassword); // 不再進行本地哈希，直接傳遞新密碼
                        jsonObject.put("confirm_new_password", confirmPassword); // 不再進行本地哈希，直接傳遞確認新密碼
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 發送網絡請求
                    sendChangePasswordRequest(jsonObject.toString());
                })
                .setNegativeButton("否", (dialog, which) -> {
                    // 點選了否，取消操作
                    Log.d("PasswordChangeFragment", "確認修改彈窗 - 取消點擊");
                })
                .show();
    }

    private void sendChangePasswordRequest(String json) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_change_password.php") // 替換為你的PHP API URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("PasswordChangeFragment", "網絡請求失敗", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "網絡請求失敗", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            Log.d("PasswordChangeFragment", "網絡請求成功, onResponse: " + responseData);
                            if (status.equals("success")) {
                                // 密碼更新成功，執行登出操作
                                logout();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("PasswordChangeFragment", "密碼更新失敗: " + response.code());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "密碼更新失敗", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void logout() {
        // 清除SharedPreferences中的用戶信息
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 導航到登入畫面
        Navigation.findNavController(requireView()).navigate(R.id.nav_login);
    }

    private void submit() {
        // TODO: 提交邏輯
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
        Toast.makeText(getActivity(), "取消修改密碼", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigate(R.id.nav_user_data);
    }
}
