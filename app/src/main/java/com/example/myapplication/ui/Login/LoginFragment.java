package com.example.myapplication.ui.Login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(requireContext());

        final EditText editText = root.findViewById(R.id.editText);
        final EditText passwordEditText = root.findViewById(R.id.password);
        final Button loginButton = root.findViewById(R.id.button11);
        final Button registerButton = root.findViewById(R.id.button12);
        final Button forgotPasswordButton = root.findViewById(R.id.forgot_password);
        final CheckBox rememberPasswordCheckBox = root.findViewById(R.id.remember_password);

        // 在这里加载保存的用户名和密码
        loadSavedCredentials(editText, passwordEditText, rememberPasswordCheckBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "請輸入使用者名稱及密碼", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_registration);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseRequireInsteadOfGet")
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_forget_password);
            }
        });

        return root;
    }

    // 加载保存的用户名和密码
    private void loadSavedCredentials(EditText editText, EditText passwordEditText, CheckBox rememberPasswordCheckBox) {
        String savedUsername = sharedPreferences.getString("username", null);
        String savedPassword = sharedPreferences.getString("password", null);
        boolean rememberPassword = sharedPreferences.getBoolean("remember_password", false);

        if (savedUsername != null) {
            editText.setText(savedUsername);
        }
        if (savedPassword != null) {
            passwordEditText.setText(savedPassword);
        }
        rememberPasswordCheckBox.setChecked(rememberPassword);
    }

    // 登入方法
    private void login(String username, String password) {
        // 建立 OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // 建立 FormBody，將使用者名稱和密碼添加到請求中
        RequestBody formBody = new FormBody.Builder()
                .add("account", username)
                .add("password", password)
                .build();

        // 建立 POST 請求
        Request request = new Request.Builder()
                .url("http://100.96.1.3/api_login.php")
                .post(formBody)
                .build();

        // 執行請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "登入失敗，請檢查網路連接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                if (responseData.contains("登入成功")) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 登入成功，執行相應操作
                            handleLoginSuccess(username, password);
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 登入失敗，顯示錯誤訊息
                            Toast.makeText(requireContext(), "登入失敗，請檢查帳號和密碼", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // 登入成功處理
    private void handleLoginSuccess(String username, String password) {
        // 保存使用者名稱到偏好設置
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ACCOUNT", username); // 保存帳戶信息
        editor.apply();

        // 導航到 MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("ACCOUNT", username);
        startActivity(intent);
        Toast.makeText(requireContext(), "登入成功", Toast.LENGTH_SHORT).show();

        // 如果需要記住密碼，保存使用者名稱和密碼到偏好設置
        CheckBox rememberPasswordCheckBox = requireView().findViewById(R.id.remember_password);
        editor = sharedPreferences.edit();
        if (rememberPasswordCheckBox.isChecked()) {
            sessionManager.login(username);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putBoolean("remember_password", true);
            editor.apply();
        } else {
            sessionManager.login(username);
            editor.remove("username");
            editor.remove("password");
            editor.putBoolean("remember_password", false);
            editor.apply();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 更新選單項目
        updateMenuItems();
    }

    // 更新選單項目
    private void updateMenuItems() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMenuItems();
        }
    }
}
